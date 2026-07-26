package com.joaovpg.economize.usuario.http.validacao;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = TimezoneValidoValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimezoneValido {
    String message() default "deve ser um timezone IANA valido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
