package com.example.restapi.io;

import com.example.restapi.entity.ExpenseCategory;
import com.example.restapi.util.ValidExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseRequest {

    @NotBlank(message = "Expense name is required.")
    @Size(min = 3, message = "Expense name is required.")
    private String name;

    private String description;
    
    @ValidExpenseCategory
    private ExpenseCategory category;

    @NotNull(message = "Expense category is required.")
    private Date date;

    @NotNull(message =  "Expense amount is required.")
    private BigDecimal amount;
}
