package com.joaovpg.economize.usuario.http.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZoneId;

public class TimezoneValidoValidator implements ConstraintValidator<TimezoneValido, String> {
  @Override
  public boolean isValid(String timezone, ConstraintValidatorContext context) {
    return timezone == null
        || (timezone.contains("/")
            && !timezone.startsWith("Etc/")
            && ZoneId.getAvailableZoneIds().contains(timezone));
  }
}
