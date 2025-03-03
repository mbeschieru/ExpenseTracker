package com.example.restapi.service;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.entity.ExpenseEntity;
import com.example.restapi.exceptions.ResourceNotFoundException;
import com.example.restapi.repository.IExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
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

    @Override
    public ExpenseDTO getByExpenseId(String expenseId) {
        ExpenseEntity optionalExpense = expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found exception for id " + expenseId));
        return mapExpenseToDTO(optionalExpense);
    }

    @Override
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        ExpenseEntity newExpenseEntity = mapDtoToExpense(expenseDTO);
        newExpenseEntity.setExpenseId(UUID.randomUUID().toString());
        expenseRepository.save(newExpenseEntity);
        return mapExpenseToDTO(newExpenseEntity);
    }

    @Override
    public ExpenseDTO updateExpense(ExpenseDTO expenseDTO, String expenseId) {
        ExpenseEntity existingExpense = getExpenseEntity(expenseId);
        ExpenseEntity updatedExpense = mapDtoToExpense(expenseDTO);
        updatedExpense.setId(existingExpense.getId());
        updatedExpense.setExpenseId(expenseId);
        updatedExpense.setCreatedAt(existingExpense.getCreatedAt());
        updatedExpense.setUpdatedAt(existingExpense.getUpdatedAt());
        updatedExpense = expenseRepository.save(updatedExpense);
        return mapExpenseToDTO(updatedExpense);
    }


    private ExpenseDTO mapExpenseToDTO(ExpenseEntity expenseEntity) {
        return modelMapper.map(expenseEntity, ExpenseDTO.class);
    }

    private ExpenseEntity mapDtoToExpense(ExpenseDTO expenseDTO) {
        return modelMapper.map(expenseDTO, ExpenseEntity.class);
    }

    private ExpenseEntity getExpenseEntity (String expenseId) {
        return expenseRepository
                .findByExpenseId(expenseId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Expense not found for the expense id " + expenseId)
                );
    }
}
