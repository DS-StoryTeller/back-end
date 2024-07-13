package com.cojac.storyteller.annotation;

import com.cojac.storyteller.dto.user.ReissueDTO;
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
