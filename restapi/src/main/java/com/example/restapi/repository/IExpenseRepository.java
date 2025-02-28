package com.example.restapi.repository;

import com.example.restapi.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IExpenseRepository extends JpaRepository<ExpenseEntity,Long> {
}
