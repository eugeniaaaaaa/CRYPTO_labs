package crypto.labs567.validation;

import crypto.labs567.service.CommonPasswordService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotCommonPasswordValidator implements ConstraintValidator<NotCommonPassword, String> {
    private final CommonPasswordService commonPasswordService;

    public NotCommonPasswordValidator(CommonPasswordService commonPasswordService) {
        this.commonPasswordService = commonPasswordService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return commonPasswordService.isCommon(value);
    }
}
