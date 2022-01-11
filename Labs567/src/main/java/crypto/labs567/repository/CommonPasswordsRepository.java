package crypto.labs567.repository;

import crypto.labs567.properties.CryptoProperties;
import lombok.SneakyThrows;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

@Component
public class CommonPasswordsRepository {
    private static final String COMMON_PASSWORDS_TABLE_NAME = "common_passwords";
    private static final String VALIDATION_QUERY =
            format("SELECT * FROM {0}", COMMON_PASSWORDS_TABLE_NAME);
    private static final String INSERT_QUERY_PATTERN =
            format("INSERT INTO {0} VALUES {1}", COMMON_PASSWORDS_TABLE_NAME, "{0}");
    private static final String CREATE_TABLE =
            format("CREATE TABLE {0} (password_encoded VARCHAR(255))", COMMON_PASSWORDS_TABLE_NAME);
    private static final String SELECT_PASSWORD_QUERY =
            format("SELECT * FROM {0} WHERE password_encoded = ?", COMMON_PASSWORDS_TABLE_NAME);
    private final CryptoProperties cryptoProperties;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public CommonPasswordsRepository(CryptoProperties cryptoProperties, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.cryptoProperties = cryptoProperties;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @SneakyThrows
    public void init() {
        jdbcTemplate.execute(format("DROP TABLE IF EXISTS {0}", COMMON_PASSWORDS_TABLE_NAME));
        jdbcTemplate.execute(CREATE_TABLE);

        String passwords = Files.lines(Paths.get(cryptoProperties.getCommonPasswordsFileName()))
                .map(passwordEncoder::encode)
                .collect(Collectors.joining("', '", "'", "'"));

        jdbcTemplate.execute(format(INSERT_QUERY_PATTERN, passwords));
    }

    public boolean tableExists() {
        try {
            jdbcTemplate.query(VALIDATION_QUERY, rs -> true);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean containsPassword(String password) {
        return Boolean.TRUE.equals(jdbcTemplate.query(SELECT_PASSWORD_QUERY, ResultSet::next, password));
    }
}
