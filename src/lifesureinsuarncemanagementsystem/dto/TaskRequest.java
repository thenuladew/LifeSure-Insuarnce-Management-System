package com.example.lifesureinsuarncemanagementsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private Long agentId;
    private Long customerId;
    private Long policyId;
    private String status;
    private LocalDateTime dueDate;
}
