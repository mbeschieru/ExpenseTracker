package com.example.restapi.controllers;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.dto.ExpensePageResponseDTO;
import com.example.restapi.io.ExpenseRequest;
import com.example.restapi.io.ExpenseResponse;
import com.example.restapi.service.IExpenseService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Long.sum;

@RestController
@RequiredArgsConstructor
@Slf4j

public class ExpensesController {

    private final IExpenseService expenseService;
    private final ModelMapper modelMapper;
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/expenses/{expenseId}")
    public ExpenseResponse getExpenseById(@PathVariable String expenseId) {
        ExpenseDTO expenseDTO = expenseService.getByExpenseId(expenseId);
        return mapExpenseDTOToResponse(expenseDTO);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/expenses")
    public ExpensePageResponseDTO getExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount
    ) {
        ExpensePageResponseDTO expenseDTOS = expenseService.getAllExpensesPaged(page, pageSize,category,startDate,endDate,minAmount,maxAmount);

        return expenseDTOS;
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

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/expenses/total")
    public double getTotalExpenses() {
        List<ExpenseDTO> dtoList = expenseService.getAllExpenses();
        double sum = dtoList.stream()
                .mapToDouble(ExpenseDTO::getAmountDoubleValue)
                .sum();
        return sum;
    }

    private ExpenseResponse mapExpenseDTOToResponse(ExpenseDTO expenseDTO) {
        return modelMapper.map(expenseDTO, ExpenseResponse.class);
    }

    private ExpenseDTO mapExpenseRequestToDTO (ExpenseRequest expenseRequest) {
        return modelMapper.map(expenseRequest,ExpenseDTO.class);
    }

}

