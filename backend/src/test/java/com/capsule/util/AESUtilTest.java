package com.capsule.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AESUtilTest {

    @Test
    @DisplayName("Test 1 — Roundtrip: Encrypt and decrypt a secret memory")
    void testRoundtrip() throws Exception {
        String original = "Hello, this is a secret memory.";
        SecretKey key = AESUtil.deriveKey("test-secret-uuid");
        
        String encrypted = AESUtil.encrypt(original, key);
        assertNotNull(encrypted);
        assertNotEquals(original, encrypted);
        
        String decrypted = AESUtil.decrypt(encrypted, key);
        assertEquals(original, decrypted);
    }

    @Test
    @DisplayName("Test 2 — IV randomness: Same plaintext/key should produce different ciphertexts")
    void testIvRandomness() throws Exception {
        SecretKey key = AESUtil.deriveKey("same-secret");
        String plaintext = "same plaintext";
        
        String enc1 = AESUtil.encrypt(plaintext, key);
        String enc2 = AESUtil.encrypt(plaintext, key);
        
        assertNotEquals(enc1, enc2);
        
        // Both should still decrypt to the same thing
        assertEquals(plaintext, AESUtil.decrypt(enc1, key));
        assertEquals(plaintext, AESUtil.decrypt(enc2, key));
    }

    @Test
    @DisplayName("Test 3 — wrapSecret / unwrapSecret roundtrip")
    void testWrapUnwrapRoundtrip() throws Exception {
        String secret = UUID.randomUUID().toString();
        // 64 character hex string (32 bytes)
        String masterKey = "0000000000000000000000000000000000000000000000000000000000000001";
        
        String wrapped = AESUtil.wrapSecret(secret, masterKey);
        assertNotNull(wrapped);
        
        String unwrapped = AESUtil.unwrapSecret(wrapped, masterKey);
        assertEquals(secret, unwrapped);
    }

    @Test
    @DisplayName("Test 4 — Tamper detection: GCM auth tag check must fail if ciphertext is altered")
    void testTamperDetection() throws Exception {
        SecretKey key = AESUtil.deriveKey("tamper-test");
        String originalData = "sensitive data";
        String encrypted = AESUtil.encrypt(originalData, key);
        
        // Corrupt the ciphertext by flipping a character in the middle
        char[] chars = encrypted.toCharArray();
        int index = chars.length / 2;
        chars[index] = chars[index] == 'A' ? 'B' : 'A';
        String tampered = new String(chars);
        
        // GCM auth tag check must fail (throws AEADBadTagException, but we check for any Exception)
        assertThrows(Exception.class, () -> AESUtil.decrypt(tampered, key));
    }

    @Test
    @DisplayName("Test 5 — Deterministic key derivation: Same input produces same key")
    void testDeterministicKeyDerivation() throws Exception {
        String input = "same-input";
        SecretKey key1 = AESUtil.deriveKey(input);
        SecretKey key2 = AESUtil.deriveKey(input);
        
        assertArrayEquals(key1.getEncoded(), key2.getEncoded());
    }
}
