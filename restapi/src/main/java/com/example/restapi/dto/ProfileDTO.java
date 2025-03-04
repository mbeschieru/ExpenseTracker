package com.example.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private String expenseId;
    private String email;
    private String name;
    private String password;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
