package com.example.bankcards.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

@Component
public class EncryptionUtil {

    @Value("${card.encryption.secret}")
    private String secret;

    private static final String ALGORITHM = "AES";

    private SecretKeySpec getKey() {
        return new SecretKeySpec(secret.getBytes(), ALGORITHM);
    }

    // Шифрование карты
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования", e);
        }
    }

    // Дешифровка карты
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка дешифровки", e);
        }
    }

    // Генерация номера карты
    public String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10)); // добавляет 0-9
        }
        return cardNumber.toString();
    }

    // Маскировка номера карты
    public String maskCardNumber(String cardNumber) {
        if (cardNumber.length() != 16) {
            return "**** MASK ERROR ****";
        }
        return "**** **** **** " + cardNumber.substring(12);
    }
}

