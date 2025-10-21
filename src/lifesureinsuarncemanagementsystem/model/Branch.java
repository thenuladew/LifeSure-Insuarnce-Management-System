package com.example.lifesureinsuarncemanagementsystem.model;

import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Branch code is required")
    //@Pattern(regexp = "^BR\\d{3}$", message = "Branch code must be in format BR followed by 3 digits (e.g. BR001)")
    @Column(name = "b_code", unique = true, nullable = false, length = 50)
    private String bCode;  // Branch Code (e.g., BR001)

    @NotBlank(message = "Branch name is required")
    @Size(min = 3, max = 100, message = "Branch name must be between 3 and 100 characters")
    @Column(name = "b_name", nullable = false, length = 100)
    private String bName;  // Branch Name

    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address cannot exceed 200 characters")
    @Column(name = "b_address", nullable = true, length = 200)
    private String bAddress;  // Branch Address

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    @Column(name = "b_phone", nullable = false, length = 20)
    private String bPhone;  // Branch Contact Number

    @NotBlank(message = "Manager name is required")
    @Size(min = 2, max = 100, message = "Manager name must be between 2 and 100 characters")
    @Column(name = "manager_name", nullable = false, length = 100)
    private String managerName;  // Manager of the Branch

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;  // Branch official email

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;  // Auto generated timestamp

    @DecimalMin(value = "0.0", inclusive = true, message = "Target amount must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Target amount can have up to 12 digits and 2 decimals")
    @Column(name = "target_amount", precision = 14, scale = 2)
    private BigDecimal targetAmount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Achieved amount must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Achieved amount can have up to 12 digits and 2 decimals")
    @Column(name = "achieved_amount", precision = 14, scale = 2)
    private BigDecimal achievedAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BranchStatus status = BranchStatus.ACTIVE;

    public Branch() {}

    public Branch(Long id, String bCode, String bName, String bAddress, String bPhone, String managerName, String email, LocalDateTime createdAt) {
        this.id = id;
        this.bCode = bCode;
        this.bName = bName;
        this.bAddress = bAddress;
        this.bPhone = bPhone;
        this.managerName = managerName;
        this.email = email;
        this.createdAt = createdAt;
        this.targetAmount = BigDecimal.ZERO;
        this.achievedAmount = BigDecimal.ZERO;
        this.status = BranchStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public String getbCode() { return bCode; }
    public void setbCode(String bCode) { this.bCode = bCode; }

    public String getbName() { return bName; }
    public void setbName(String bName) { this.bName = bName; }

    public String getbAddress() { return bAddress; }
    public void setbAddress(String bAddress) { this.bAddress = bAddress; }

    public String getbPhone() { return bPhone; }
    public void setbPhone(String bPhone) { this.bPhone = bPhone; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BigDecimal getTargetAmount() {
        return targetAmount != null ? targetAmount : BigDecimal.ZERO;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = normalizeAmount(targetAmount);
    }

    public BigDecimal getAchievedAmount() {
        return achievedAmount != null ? achievedAmount : BigDecimal.ZERO;
    }

    public void setAchievedAmount(BigDecimal achievedAmount) {
        this.achievedAmount = normalizeAmount(achievedAmount);
    }

    public BranchStatus getStatus() {
        return status;
    }

    public void setStatus(BranchStatus status) {
        this.status = status != null ? status : BranchStatus.ACTIVE;
    }

    @PrePersist
    @PreUpdate
    private void ensureDefaults() {
        if (status == null) {
            status = BranchStatus.ACTIVE;
        }
        targetAmount = normalizeAmount(targetAmount);
        achievedAmount = normalizeAmount(achievedAmount);
    }

    @Transient
    public int getAchievementPercentage() {
        if (getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return getAchievedAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(getTargetAmount(), 0, RoundingMode.HALF_UP)
                .intValue();
    }

    private BigDecimal normalizeAmount(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}