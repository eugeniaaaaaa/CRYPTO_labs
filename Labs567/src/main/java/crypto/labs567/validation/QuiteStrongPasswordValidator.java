package crypto.labs567.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.IntStream;

public class QuiteStrongPasswordValidator implements ConstraintValidator<QuiteStrongPassword, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (value.length() >= 8) &&
                containsAnyFromRange(value, 'A', 'Z') &&
                containsAnyFromRange(value, 'a', 'z') &&
                containsAnyFromRange(value, '0', '9');
    }

    private boolean containsAnyFromRange(String value, int fromSymbol, int toSymbol) {
        return IntStream.range(fromSymbol, toSymbol)
                .mapToObj(i -> (char) i)
                .map(String::valueOf)
                .anyMatch(value::contains);
    }
}
