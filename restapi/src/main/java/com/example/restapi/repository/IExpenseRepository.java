package com.example.restapi.repository;

import com.example.restapi.dto.ExpenseDTO;
import com.example.restapi.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IExpenseRepository extends JpaRepository<ExpenseEntity,Long> {
    Optional<ExpenseEntity> findByExpenseId(String expenseId);

    List<ExpenseEntity> findByOwnerId(Long id);

    Optional<ExpenseEntity> findByOwnerIdAndExpenseId(Long id , String expenseId);
}
