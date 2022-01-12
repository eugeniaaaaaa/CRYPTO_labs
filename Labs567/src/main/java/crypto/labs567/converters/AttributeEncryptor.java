package crypto.labs567.converters;

import crypto.labs567.properties.CryptoProperties;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

@Component
@Scope("prototype")
public class AttributeEncryptor {
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    private final SecretKey key;

    public AttributeEncryptor(CryptoProperties cryptoProperties) throws NoSuchAlgorithmException {
        // Key is already generated and set in application.properties
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(AES_KEY_SIZE);
        byte[] decodedKey = Base64.getDecoder().decode(cryptoProperties.getContentEncodingSecretKey());
        key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    @SneakyThrows
    public byte[] encrypt(String attribute, byte[] salt) {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, salt);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
        return cipher.doFinal(attribute.getBytes());
    }

    @SneakyThrows
    public String decrypt(byte[] dbData, byte[] salt) {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, salt);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
        byte[] decryptedText = cipher.doFinal(dbData);
        return new String(decryptedText);
    }

    public byte[] randomSalt() {
        byte[] IV = new byte[GCM_IV_LENGTH];
        Random random = new Random(100);
        random.nextBytes(IV);
        return IV;
    }
}
