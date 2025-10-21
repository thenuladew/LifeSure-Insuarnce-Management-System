package com.example.lifesureinsuarncemanagementsystem.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class BranchPerformanceForm {

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Target must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Target can have up to 12 digits and 2 decimals")
    private BigDecimal targetAmount;

    @NotNull(message = "Achieved amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Achieved must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Achieved can have up to 12 digits and 2 decimals")
    private BigDecimal achievedAmount;

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getAchievedAmount() {
        return achievedAmount;
    }

    public void setAchievedAmount(BigDecimal achievedAmount) {
        this.achievedAmount = achievedAmount;
    }
}
