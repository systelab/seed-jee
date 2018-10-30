package com.systelab.seed.infrastructure.constraints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckDateValidator.class)
public @interface CheckDate {
    // required
    String message() default "{com.systelab.seed.infrastructure.constraints.CheckDate.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // optional
    String dateFormat();
}