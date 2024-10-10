package com.cojac.storyteller.user.annotation;

import com.cojac.storyteller.user.dto.ReissueDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, ReissueDTO> {

    @Override
    public void initialize(AtLeastOneNotNull constraintAnnotation) {
    }

    @Override
    public boolean isValid(ReissueDTO dto, ConstraintValidatorContext context) {
        return dto.getUsername() != null || dto.getAccountId() != null;
    }
}
