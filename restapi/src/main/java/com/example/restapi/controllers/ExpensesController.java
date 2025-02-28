package com.example.restapi.controllers;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.io.ExpenseResponse;
import com.example.restapi.service.ExpenseService;
import com.example.restapi.service.IExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ExpensesController {

    private final IExpenseService expenseService;
    private final ModelMapper modelMapper;

    @GetMapping("/expenses")
    public List<ExpenseResponse> getExpenses() {
        log.info("API GET /expenses called");
        List<ExpenseDTO> dtoList = expenseService.getAllExpenses();
        List<ExpenseResponse> response = dtoList.stream().map(expenseDTO -> mapExpenseToResponse(expenseDTO)).collect(Collectors.toList());
        return response;
    }

    private ExpenseResponse mapExpenseToResponse(ExpenseDTO expenseDTO) {
        return modelMapper.map(expenseDTO, ExpenseResponse.class);
    }
}
