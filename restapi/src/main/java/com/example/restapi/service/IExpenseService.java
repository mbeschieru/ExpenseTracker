package com.example.restapi.service;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.dto.ExpensePageResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IExpenseService {
    List<ExpenseDTO> getAllExpenses();
    ExpensePageResponseDTO getAllExpensesPaged(int page, int pageSize, String category, String startDate, String endDate, Double minAmount, Double maxAmount);
    ExpenseDTO getByExpenseId(String expenseId);

    ExpenseDTO createExpense(ExpenseDTO expenseDTO);

    ExpenseDTO updateExpense(ExpenseDTO expenseDTO , String expenseId);

    void deleteExpenseByEpenseId(String expenseId);
}
