package crypto.labs567.converters;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Component
@Converter
public class AttributeEncryptor implements AttributeConverter<String, byte[]> {
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;
    private final SecretKey key;

    public AttributeEncryptor() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        key = keyGenerator.generateKey();
    }

    @Override
    @SneakyThrows
    public byte[] convertToDatabaseColumn(String attribute) {
        byte[] IV = randomIV();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
        return cipher.doFinal(attribute.getBytes());
    }

    @Override
    @SneakyThrows
    public String convertToEntityAttribute(byte[] dbData) {
        byte[] IV = randomIV();
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
        byte[] decryptedText = cipher.doFinal(dbData);
        return new String(decryptedText);
    }

    private byte[] randomIV() {
        byte[] IV = new byte[GCM_IV_LENGTH];
        Random random = new Random(100);
        random.nextBytes(IV);
        return IV;
    }
}
