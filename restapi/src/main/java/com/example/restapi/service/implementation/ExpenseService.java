package com.example.restapi.service.implementation;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.entity.ExpenseEntity;
import com.example.restapi.exceptions.ResourceNotFoundException;
import com.example.restapi.repository.IExpenseRepository;
import com.example.restapi.service.IExpenseService;
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
public class ExpenseService implements IExpenseService {

    private final IExpenseRepository expenseRepository;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Override
    public List<ExpenseDTO> getAllExpenses() {
        List<ExpenseEntity> expenseEntities = expenseRepository.findByOwnerId(getLoggedInProfileId());
        List<ExpenseDTO> expenseDTOS = expenseEntities.stream().map(expenseEntity -> mapExpenseToDTO(expenseEntity)).collect(Collectors.toList());
        return expenseDTOS;
    }

    @Override
    public ExpenseDTO getByExpenseId(String expenseId) {
        ExpenseEntity optionalExpense = expenseRepository.findByOwnerIdAndExpenseId(getLoggedInProfileId(),expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found exception for id " + expenseId));
        return mapExpenseToDTO(optionalExpense);
    }

    @Override
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        ExpenseEntity newExpenseEntity = mapDtoToExpense(expenseDTO);
        newExpenseEntity.setExpenseId(UUID.randomUUID().toString());
        newExpenseEntity.setOwner(authService.getLoggedInProfile());
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
        updatedExpense.setOwner(existingExpense.getOwner());
        updatedExpense = expenseRepository.save(updatedExpense);
        return mapExpenseToDTO(updatedExpense);
    }

    @Override
    public void deleteExpenseByEpenseId(String expenseId) {
        ExpenseEntity expenseEntity = getExpenseEntity(expenseId);
        expenseRepository.delete(expenseEntity);
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

    private Long getLoggedInProfileId() {
        return authService.getLoggedInProfile().getId();
    }
}
