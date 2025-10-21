package com.example.lifesureinsuarncemanagementsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity representing payment transactions in the SecureLife system.
 * Each payment is associated with a user and policy, and contains payment slip information.
 */
@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", precision = 14, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "payment_slip_filename", length = 255)
    private String paymentSlipFilename;

    @Column(name = "payment_slip_path", length = 500)
    private String paymentSlipPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum for payment status
     */
    public enum PaymentStatus {
        PENDING("Pending Review"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        PROCESSING("Processing"),
        COMPLETED("Completed");

        private final String displayName;

        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enum for payment methods
     */
    public enum PaymentMethod {
        BANK_TRANSFER("Bank Transfer"),
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        MOBILE_PAYMENT("Mobile Payment"),
        CASH("Cash"),
        CHECK("Check"),
        OTHER("Other");

        private final String displayName;

        PaymentMethod(String displayName) {
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
            status = PaymentStatus.PENDING;
        }
        if (amount != null) {
            amount = amount.setScale(2, java.math.RoundingMode.HALF_UP);
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
