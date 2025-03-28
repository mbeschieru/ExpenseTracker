package com.example.restapi.validators;

import com.example.restapi.entity.ExpenseCategory;
import com.example.restapi.util.ValidExpenseCategory;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ExpenseCategoryValidator implements ConstraintValidator<ValidExpenseCategory, ExpenseCategory> {

    @Override
    public boolean isValid(ExpenseCategory expenseCategory, ConstraintValidatorContext constraintValidatorContext) {
        if (expenseCategory == null) return false;

        return Arrays.asList(ExpenseCategory.values()).contains(expenseCategory);
    }
}
