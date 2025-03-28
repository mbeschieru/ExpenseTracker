package com.example.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpensePageResponseDTO {
    private List<ExpenseDTO> content;
    private int page;
    private int pageSize;
    private long total;
    private int totalPages;
    private double totalAmount;
}
