package crypto.lab4;

import com.opencsv.CSVWriter;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.BiConsumer;

public class PasswordGeneratorStartup {
    public static void main(String[] args) {
        PasswordGenerator generator = new PasswordGenerator();
        writeGeneratedPasswords("data/md5.csv", generator, (password, writer) ->
                writer.writeNext(new String[] {DigestUtils.md5Hex(password).toLowerCase()}));
        writeGeneratedPasswords("data/sha1-with-salt.csv", generator, (password, writer) -> {
            String salt = generator.randomPassword();
            writer.writeNext(new String[] {DigestUtils.sha1Hex(password + salt).toLowerCase(Locale.ROOT), salt});
        });
    }

    @SneakyThrows
    private static void writeGeneratedPasswords(String fileName, PasswordGenerator generator, BiConsumer<String, CSVWriter> entryWriter) {
//        try (FileChannel fileChannel = Files.newByteChannel(Paths.get(fileName)))
        try (CSVWriter writer = new CSVWriter(Files.newBufferedWriter(Paths.get(fileName)))) {
            for (int i = 0; i < 100_000; i++) {
                entryWriter.accept(generator.generatePassword(), writer);
            }
        }
    }
}
