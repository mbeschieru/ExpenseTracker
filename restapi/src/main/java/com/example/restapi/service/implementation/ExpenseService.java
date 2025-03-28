package com.example.restapi.service.implementation;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.dto.ExpensePageResponseDTO;
import com.example.restapi.entity.ExpenseEntity;
import com.example.restapi.exceptions.ResourceNotFoundException;
import com.example.restapi.repository.IExpenseRepository;
import com.example.restapi.service.IExpenseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public ExpensePageResponseDTO getAllExpensesPaged(int page, int pageSize, String category, String startDate, String endDate, Double minAmount, Double maxAmount) {
        List<ExpenseEntity> expenses = expenseRepository.findByOwnerId(getLoggedInProfileId());
        List<ExpenseDTO> filteredExpenses = expenses.stream()
                .filter(expense -> category == null || category.isEmpty() || String.valueOf(expense.getCategory()).equals(category))
                .filter(expense -> {
                    if (startDate == null || startDate.isEmpty()) return true;
                    return !expense.getCreatedAt().before(parseFrontendDateStart(startDate));
                })
                .filter(expense -> {
                    if (endDate == null || endDate.isEmpty()) return true;
                    return !expense.getCreatedAt().after(parseFrontendDateEnd(endDate));
                })
                .filter(expense -> minAmount == null || expense.getAmount().doubleValue() >= minAmount)
                .filter(expense -> maxAmount == null || expense.getAmount().doubleValue() <= maxAmount)
                .map(this::mapExpenseToDTO)
                .collect(Collectors.toList());

        double totalAmount = filteredExpenses.stream()
                .mapToDouble( ExpenseDTO::getAmountDoubleValue )
                .sum();


        Page<ExpenseDTO> expensePage = paginateList(filteredExpenses, PageRequest.of(page, pageSize));
        return new ExpensePageResponseDTO(
                expensePage.getContent(),
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.getTotalElements(),
                expensePage.getTotalPages(),
                totalAmount
        );
    }

    private Page<ExpenseDTO> paginateList(List<ExpenseDTO> expenses, Pageable pageable) {
        int total = expenses.size();
        int start = Math.min((int) pageable.getOffset(), total);
        int end = Math.min(start + pageable.getPageSize(), total);

        List<ExpenseDTO> pagedList = expenses.subList(start, end);

        return new PageImpl<>(pagedList, pageable, total);
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

    private Timestamp parseFrontendDateStart(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return Timestamp.valueOf(date.atStartOfDay());
    }

    private Timestamp parseFrontendDateEnd(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return Timestamp.valueOf(date.atTime(23, 59, 59));
    }
}
