package com.example.restapi.service;

import com.example.restapi.dto.ExpenseDTO;

import java.util.List;

public interface IExpenseService {
    List<ExpenseDTO> getAllExpenses();
    ExpenseDTO getByExpenseId(String expenseId);

    ExpenseDTO createExpense(ExpenseDTO expenseDTO);

    ExpenseDTO updateExpense(ExpenseDTO expenseDTO , String expenseId);

}
