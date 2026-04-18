package com.capsule.service;

import com.capsule.dto.*;
import com.capsule.exception.AccessDeniedException;
import com.capsule.exception.CapsuleNotFoundException;
import com.capsule.exception.StorageQuotaExceededException;
import com.capsule.model.*;
import com.capsule.repository.CapsuleDoodleRepository;
import com.capsule.repository.CapsuleFileRepository;
import com.capsule.repository.CapsuleRecipientRepository;
import com.capsule.repository.CapsuleRepository;
import com.capsule.util.AESUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapsuleService {

    private final CapsuleRepository capsuleRepo;
    private final CapsuleRecipientRepository recipientRepo;
    private final CapsuleFileRepository fileRepo;
    private final CapsuleDoodleRepository doodleRepo;
    private final CapsuleStateMachine stateMachine;
    private final StorageService storageService;
    private final SqsService sqsService;
    private final EmailService emailService;
    private final AiReflectionService aiReflectionService;

    @Value("${capsule.master-key}")
    private String masterKey;

    @Value("${capsule.base-url}")
    private String baseUrl;

    @Value("${capsule.presigned-url-expiry-minutes:15}")
    private int presignedUrlExpiryMinutes;

    public CapsuleService(
            CapsuleRepository capsuleRepo,
            CapsuleRecipientRepository recipientRepo,
            CapsuleFileRepository fileRepo,
            CapsuleDoodleRepository doodleRepo,
            CapsuleStateMachine stateMachine,
            StorageService storageService,
            SqsService sqsService,
            EmailService emailService,
            AiReflectionService aiReflectionService) {
        this.capsuleRepo = capsuleRepo;
        this.recipientRepo = recipientRepo;
        this.fileRepo = fileRepo;
        this.doodleRepo = doodleRepo;
        this.stateMachine = stateMachine;
        this.storageService = storageService;
        this.sqsService = sqsService;
        this.emailService = emailService;
        this.aiReflectionService = aiReflectionService;
    }

    // ═══════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════
    @Transactional
    public CreateCapsuleResponse createCapsule(CreateCapsuleRequest request) {
        // 1. Validate unlock is at least 1 day in future
        if (request.getUnlockAt().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new IllegalArgumentException("Unlock date must be at least 1 day in the future");
        }

        // 2. Generate two separate secrets
        UUID token = UUID.randomUUID();               // URL identifier only
        String encryptionSecret = UUID.randomUUID().toString(); // crypto key seed, never exposed

        // 3. Encrypt content if present
        String encryptedContent = null;
        if (request.getContent() != null && !request.getContent().isBlank()) {
            try {
                SecretKey contentKey = AESUtil.deriveKey(encryptionSecret);
                encryptedContent = AESUtil.encrypt(request.getContent(), contentKey);
            } catch (Exception e) {
                throw new RuntimeException("Encryption failed", e);
            }
        }

        // 4. Wrap encryptionSecret with master key for DB storage
        String wrappedSecret;
        String encryptionKeyHash;
        try {
            wrappedSecret = AESUtil.wrapSecret(encryptionSecret, masterKey);
            encryptionKeyHash = AESUtil.sha256Hex(encryptionSecret);
        } catch (Exception e) {
            throw new RuntimeException("Secret wrapping failed", e);
        }

        // 5. Build and save capsule
        Capsule capsule = Capsule.builder()
            .token(token)
            .encryptedSecret(wrappedSecret)
            .encryptionKeyHash(encryptionKeyHash)
            .creatorEmail(request.getCreatorEmail())
            .title(request.getTitle())
            .encryptedContent(encryptedContent)
            .unlockAt(request.getUnlockAt())
            .status(CapsuleStatus.SEALED)
            .backgroundTexture(request.getBackgroundTexture())
            .build();

        capsule = capsuleRepo.save(capsule);

        // 6. Save recipients
        final Capsule savedCapsule = capsule;
        List<CapsuleRecipient> recipients = request.getRecipients().stream()
            .map(email -> CapsuleRecipient.builder()
                .capsule(savedCapsule)
                .email(email)
                .build())
            .collect(Collectors.toList());
        recipientRepo.saveAll(recipients);

        // 7. Enqueue SQS unlock job
        sqsService.enqueue(capsule.getId(), capsule.getUnlockAt().toInstant(ZoneOffset.UTC));

        // 8. Return
        return new CreateCapsuleResponse(token, baseUrl + "/capsule/" + token);
    }

    // ═══════════════════════════════════════
    // FILE UPLOAD
    // ═══════════════════════════════════════
    public FileUploadResponse uploadFile(UUID token, MultipartFile file) {
        Capsule capsule = findByTokenOrThrow(token);

        // Check quota
        Integer totalKb = fileRepo.sumFileSizeKbByCapsuleId(capsule.getId());
        if (totalKb == null) totalKb = 0;
        
        int fileSizeKb = (int) (file.getSize() / 1024);
        if (totalKb + fileSizeKb > 100 * 1024) { // 100MB
            throw new StorageQuotaExceededException();
        }

        String storageKey;
        try {
            storageKey = storageService.upload(capsule.getId(), file);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }

        CapsuleFile capsuleFile = CapsuleFile.builder()
            .capsule(capsule)
            .fileType(file.getContentType())
            .storageUrl(storageKey)
            .fileSizeKb(fileSizeKb)
            .build();
        fileRepo.save(capsuleFile);

        String presignedUrl = storageService.generatePresignedUrl(storageKey);
        return new FileUploadResponse(presignedUrl);
    }

    // ═══════════════════════════════════════
    // DOODLE
    // ═══════════════════════════════════════
    public DoodleResponse saveDoodle(UUID token, DoodleRequest request) {
        Capsule capsule = findByTokenOrThrow(token);

        CapsuleDoodle doodle = CapsuleDoodle.builder()
            .capsule(capsule)
            .type(request.getType())
            .stickerId(request.getStickerId())
            .svgData(request.getSvgData())
            .positionX(request.getPositionX())
            .positionY(request.getPositionY())
            .scale(request.getScale())
            .rotation(request.getRotation())
            .build();

        doodle = doodleRepo.save(doodle);
        return new DoodleResponse(doodle.getId());
    }

    // ═══════════════════════════════════════
    // PREVIEW
    // ═══════════════════════════════════════
    public PreviewResponse preview(UUID token) {
        Capsule capsule = findByTokenOrThrow(token);

        if (LocalDateTime.now().isBefore(capsule.getUnlockAt())) {
            return new PreviewResponse(CapsuleStatus.SEALED, capsule.getUnlockAt(), capsule.getTitle());
        }
        return new PreviewResponse(CapsuleStatus.UNLOCKED, capsule.getUnlockAt(), capsule.getTitle());
    }

    // ═══════════════════════════════════════
    // VERIFY
    // ═══════════════════════════════════════
    @Transactional
    public VerifyResponse verify(UUID token, String email) {
        Capsule capsule = findByTokenOrThrow(token);

        // Check unlock time
        if (LocalDateTime.now().isBefore(capsule.getUnlockAt())) {
            throw new AccessDeniedException("Capsule is not yet unlocked");
        }

        // Check recipient — never reveal whether email exists
        CapsuleRecipient recipient = recipientRepo
            .findByCapsuleAndEmail(capsule, email)
            .orElseThrow(() -> new AccessDeniedException("Access denied"));

        // Decrypt content
        String decryptedContent = null;
        if (capsule.getEncryptedContent() != null) {
            try {
                String encryptionSecret = AESUtil.unwrapSecret(
                    capsule.getEncryptedSecret(), masterKey);
                SecretKey contentKey = AESUtil.deriveKey(encryptionSecret);
                decryptedContent = AESUtil.decrypt(capsule.getEncryptedContent(), contentKey);
            } catch (Exception e) {
                throw new RuntimeException("Decryption failed", e);
            }
        }

        // Fetch files and generate pre-signed URLs
        List<FileDto> fileDtos = fileRepo.findByCapsule(capsule).stream()
            .map(f -> new FileDto(
                storageService.generatePresignedUrl(f.getStorageUrl()),
                f.getFileType()
            ))
            .collect(Collectors.toList());

        // Fetch doodles
        List<DoodleDto> doodleDtos = doodleRepo.findByCapsule(capsule).stream()
            .map(d -> new DoodleDto(d.getId(), d.getType(), d.getStickerId(),
                d.getSvgData(), d.getPositionX(), d.getPositionY(),
                d.getScale(), d.getRotation()))
            .collect(Collectors.toList());

        // Mark opened
        recipient.setOpenedAt(LocalDateTime.now());
        recipientRepo.save(recipient);

        // Transition state if needed
        if (capsule.getStatus() == CapsuleStatus.UNLOCKED) {
            stateMachine.transition(capsule, CapsuleStatus.OPENED);
            capsuleRepo.save(capsule);
        }

        // Trigger AI reflection async — fire and forget
        final String contentForAi = decryptedContent;
        aiReflectionService.generateReflectionIfAbsent(capsule, contentForAi);

        return new VerifyResponse(
            decryptedContent,
            fileDtos,
            doodleDtos,
            capsule.getBackgroundTexture(),
            capsule.getAiReflection()
        );
    }

    // ═══════════════════════════════════════
    // RECOVER
    // ═══════════════════════════════════════
    public void recover(String email) {
        List<Capsule> capsules = capsuleRepo.findByCreatorEmail(email);
        // Send email regardless of whether capsules exist
        // Never confirm or deny whether email has capsules
        emailService.sendRecoveryEmail(email, capsules);
    }

    // ═══════════════════════════════════════
    // DELETE
    // ═══════════════════════════════════════
    @Transactional
    public void delete(UUID token) {
        Capsule capsule = findByTokenOrThrow(token);

        // Transition to DELETED via state machine
        stateMachine.transition(capsule, CapsuleStatus.DELETED);

        // Delete all files from R2
        fileRepo.findByCapsule(capsule)
            .forEach(f -> storageService.delete(f.getStorageUrl()));

        // GDPR erasure — null out all PII
        capsule.setEncryptedContent(null);
        capsule.setEncryptedSecret(null);
        capsule.setDeletedAt(LocalDateTime.now());

        // Delete recipient emails
        List<CapsuleRecipient> recipients = recipientRepo.findByCapsule(capsule);
        recipientRepo.deleteAll(recipients);

        capsuleRepo.save(capsule);
    }

    // ═══════════════════════════════════════
    // HELPER
    // ═══════════════════════════════════════
    private Capsule findByTokenOrThrow(UUID token) {
        Capsule capsule = capsuleRepo.findByToken(token)
            .orElseThrow(CapsuleNotFoundException::new);
        if (capsule.getDeletedAt() != null) {
            throw new CapsuleNotFoundException();
        }
        return capsule;
    }
}
