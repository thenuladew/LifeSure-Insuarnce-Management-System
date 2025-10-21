package com.example.lifesureinsuarncemanagementsystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Policy entity
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PolicyDTO {
    private Long id;
    private String policyName;
    private String policyType;
    private BigDecimal price;
    private BigDecimal coverageAmount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
