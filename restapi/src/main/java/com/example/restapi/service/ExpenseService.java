package com.example.restapi.service;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.entity.ExpenseEntity;
import com.example.restapi.repository.IExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService{

    private final IExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ExpenseDTO> getAllExpenses() {
        List<ExpenseEntity> expenseEntities = expenseRepository.findAll();
        List<ExpenseDTO> expenseDTOS = expenseEntities.stream().map(expenseEntity -> mapExpenseToDTO(expenseEntity)).collect(Collectors.toList());
        return expenseDTOS;
    }

    private ExpenseDTO mapExpenseToDTO(ExpenseEntity expenseEntity) {
        return modelMapper.map(expenseEntity, ExpenseDTO.class);
    }
}
