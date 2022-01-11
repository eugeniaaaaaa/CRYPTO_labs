package crypto.labs567.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotCommonPasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotCommonPassword {
    String message() default "Password is listed in top most common passwords, it's easy to break";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}