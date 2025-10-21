package com.example.lifesureinsuarncemanagementsystem.dtos;

import com.example.lifesureinsuarncemanagementsystem.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Payment entity.
 * Used for transferring payment data between layers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private Long id;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionReference;
    private String paymentSlipFilename;
    private String paymentSlipPath;
    private Payment.PaymentStatus status;
    private String notes;
    private Long userId;
    private Long policyId;
    private String userName; // For display purposes
    private String userEmail; // For display purposes
    private String policyName; // For display purposes
    private String policyType; // For display purposes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Validation method to check if required fields are present
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0 &&
               paymentMethod != null && !paymentMethod.trim().isEmpty() &&
               userId != null && policyId != null;
    }

    /**
     * Get display name for status
     * @return status display name
     */
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "";
    }

    /**
     * Get display name for payment method
     * @return payment method display name
     */
    public String getPaymentMethodDisplayName() {
        if (paymentMethod == null) return "";
        
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase());
            return method.getDisplayName();
        } catch (IllegalArgumentException e) {
            return paymentMethod; // Return original if not found in enum
        }
    }

    /**
     * Format amount for display
     * @return formatted amount string
     */
    public String getFormattedAmount() {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }
}
