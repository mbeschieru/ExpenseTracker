package com.example.restapi.dto;

import com.example.restapi.entity.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDTO {
    private String expenseId;

    private String name;

    private String description;

    private ExpenseCategory category;

    private Date date;

    private BigDecimal amount;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    public double getAmountDoubleValue() {
        return  amount.doubleValue();
    }
}
