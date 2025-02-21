package com.example.car_management.validator;

import jakarta.validation.Constraint;
import org.springframework.messaging.handler.annotation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullableNotBlankValidator.class)
@Documented
public @interface NullableNotBlank {
    String message() default "Field can't be blank when provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
