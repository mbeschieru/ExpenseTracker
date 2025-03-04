package com.example.restapi.controllers;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.io.ExpenseRequest;
import com.example.restapi.io.ExpenseResponse;
import com.example.restapi.service.IExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        List<ExpenseResponse> response = dtoList.stream().map(expenseDTO -> mapExpenseDTOToResponse(expenseDTO)).collect(Collectors.toList());
        return response;
    }
    @GetMapping("/expenses/{expenseId}")
    public ExpenseResponse getExpenseById(@PathVariable String expenseId) {
        ExpenseDTO expenseDTO = expenseService.getByExpenseId(expenseId);
        return mapExpenseDTOToResponse(expenseDTO);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/expenses")
    public ExpenseResponse createExpense(@Valid @RequestBody ExpenseRequest request) {
        ExpenseDTO expenseDTO = mapExpenseRequestToDTO(request);
        expenseDTO = expenseService.createExpense(expenseDTO);
        return mapExpenseDTOToResponse(expenseDTO);
    }

    @PutMapping("/expenses/{expenseId}")
    public ExpenseResponse updateExpense(@Valid @RequestBody ExpenseRequest updateRequest , @PathVariable String expenseId) {
        ExpenseDTO updatedExpenseDTO = mapExpenseRequestToDTO(updateRequest);
        updatedExpenseDTO = expenseService.updateExpense(updatedExpenseDTO,expenseId);
        return mapExpenseDTOToResponse(updatedExpenseDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/expenses/{expenseId}")
    public void deleteExpense(@PathVariable String expenseId) {
        expenseService.deleteExpenseByEpenseId(expenseId);
    }

    private ExpenseResponse mapExpenseDTOToResponse(ExpenseDTO expenseDTO) {
        return modelMapper.map(expenseDTO, ExpenseResponse.class);
    }

    private ExpenseDTO mapExpenseRequestToDTO (ExpenseRequest expenseRequest) {
        return modelMapper.map(expenseRequest,ExpenseDTO.class);
    }

}

