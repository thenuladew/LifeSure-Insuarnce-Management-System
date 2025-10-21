package com.example.lifesureinsuarncemanagementsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Policy entity representing insurance policies in the SecureLife system.
 * Each policy contains details like name, type, price, coverage amount, and description.
 */
@Entity
@Table(name = "policies")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Policy name is required")
    @Size(min = 3, max = 100, message = "Policy name must be between 3 and 100 characters")
    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @NotBlank(message = "Policy type is required")
    @Size(min = 2, max = 50, message = "Policy type must be between 2 and 50 characters")
    @Column(name = "policy_type", nullable = false, length = 50)
    private String policyType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Price can have up to 12 digits and 2 decimals")
    @Column(name = "price", precision = 14, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage amount must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Coverage amount can have up to 12 digits and 2 decimals")
    @Column(name = "coverage_amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal coverageAmount;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PolicyStatus status = PolicyStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Enum for policy status
     */
    public enum PolicyStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUSPENDED("Suspended");

        private final String displayName;

        PolicyStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    @PreUpdate
    private void ensureDefaults() {
        if (status == null) {
            status = PolicyStatus.ACTIVE;
        }
        if (price != null) {
            price = price.setScale(2, java.math.RoundingMode.HALF_UP);
        }
        if (coverageAmount != null) {
            coverageAmount = coverageAmount.setScale(2, java.math.RoundingMode.HALF_UP);
        }
    }

    // Helper method to parse string values to BigDecimal
    public static BigDecimal parseAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Remove commas and parse
            String cleanAmount = amountStr.replace(",", "").trim();
            return new BigDecimal(cleanAmount).setScale(2, java.math.RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
