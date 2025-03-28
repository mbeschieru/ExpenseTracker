package com.example.restapi.util;

import com.example.restapi.validators.ExpenseCategoryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExpenseCategoryValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidExpenseCategory {
    String message() default "Invalid expense category";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
