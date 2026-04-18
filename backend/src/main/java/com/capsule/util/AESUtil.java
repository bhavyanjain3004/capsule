package com.capsule.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

public class AESUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_SIZE_BYTE = 32; // 256 bits

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Derives a 256-bit AES SecretKey from any string using SHA-256.
     * Same input always produces same key — deterministic.
     */
    public static SecretKey deriveKey(String secret) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(hash, "AES");
    }

    /**
     * Encrypts plaintext using AES/GCM/NoPadding.
     * Generates a fresh cryptographically random 12-byte IV for EVERY call.
     * Prepends IV to ciphertext, Base64 encodes the whole thing.
     */
    public static String encrypt(String plaintext, SecretKey key) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SECURE_RANDOM.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
        byteBuffer.put(iv);
        byteBuffer.put(ciphertext);

        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }

    /**
     * Decrypts Base64 ciphertext.
     * Reads first 12 bytes as IV.
     * Remaining bytes are ciphertext + auth tag.
     */
    public static String decrypt(String ciphertext, SecretKey key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(ciphertext);

        ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);

        byte[] actualCiphertext = new byte[byteBuffer.remaining()];
        byteBuffer.get(actualCiphertext);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decrypted = cipher.doFinal(actualCiphertext);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Wraps encryptionSecret using masterKey string for DB storage.
     * masterKey is the hex string from CAPSULE_MASTER_KEY env var.
     */
    public static String wrapSecret(String encryptionSecret, String masterKey) throws Exception {
        SecretKey key = deriveKeyFromHex(masterKey);
        return encrypt(encryptionSecret, key);
    }

    /**
     * Unwraps encrypted_secret from DB using masterKey.
     */
    public static String unwrapSecret(String encryptedSecret, String masterKey) throws Exception {
        SecretKey key = deriveKeyFromHex(masterKey);
        return decrypt(encryptedSecret, key);
    }

    /**
     * Returns lowercase hex SHA-256 hash of input string.
     */
    public static String sha256Hex(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }

    /**
     * Helper to derive a key from a hex string (masterKey).
     * If the hex string is 64 chars (32 bytes), use it directly.
     * Otherwise, hash it to ensure 256 bits.
     */
    private static SecretKey deriveKeyFromHex(String hexKey) throws Exception {
        byte[] keyBytes = HexFormat.of().parseHex(hexKey);
        if (keyBytes.length != AES_KEY_SIZE_BYTE) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            keyBytes = digest.digest(keyBytes);
        }
        return new SecretKeySpec(keyBytes, "AES");
    }
}
