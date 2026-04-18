package com.capsule.controller;

import com.capsule.dto.*;
import com.capsule.service.CapsuleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/capsule")
public class CapsuleController {

    private final CapsuleService capsuleService;

    public CapsuleController(CapsuleService capsuleService) {
        this.capsuleService = capsuleService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateCapsuleResponse> create(
            @Valid @RequestBody CreateCapsuleRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.status(201).body(capsuleService.createCapsule(request));
    }

    @PostMapping("/{token}/files")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @PathVariable UUID token,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(capsuleService.uploadFile(token, file));
    }

    @PostMapping("/{token}/doodles")
    public ResponseEntity<DoodleResponse> saveDoodle(
            @PathVariable UUID token,
            @RequestBody DoodleRequest request) {
        return ResponseEntity.ok(capsuleService.saveDoodle(token, request));
    }

    @GetMapping("/{token}/preview")
    public ResponseEntity<PreviewResponse> preview(@PathVariable UUID token) {
        return ResponseEntity.ok(capsuleService.preview(token));
    }

    @PostMapping("/{token}/verify")
    public ResponseEntity<VerifyResponse> verify(
            @PathVariable UUID token,
            @Valid @RequestBody VerifyRequest request) {
        return ResponseEntity.ok(capsuleService.verify(token, request.getEmail()));
    }

    @GetMapping("/recover")
    public ResponseEntity<Void> recover(@RequestParam String email) {
        capsuleService.recover(email);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Void> delete(@PathVariable UUID token) {
        capsuleService.delete(token);
        return ResponseEntity.ok().build();
    }
}
