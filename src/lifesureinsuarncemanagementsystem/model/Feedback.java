package com.example.lifesureinsuarncemanagementsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Feedback entity representing customer feedback in the SecureLife system.
 * Each feedback is associated with a user and contains rating, subject, and message.
 */
@Entity
@Table(name = "feedbacks")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 star rating

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FeedbackCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FeedbackStatus status = FeedbackStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Enum for feedback categories
     */
    public enum FeedbackCategory {
        GENERAL("General"),
        CLAIM_PROCESS("Claim Process"),
        POLICY_MANAGEMENT("Policy Management"),
        CUSTOMER_SERVICE("Customer Service"),
        WEBSITE_ISSUES("Website Issues"),
        PREMIUM_PAYMENT("Premium Payment"),
        OTHER("Other");

        private final String displayName;

        FeedbackCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enum for feedback status
     */
    public enum FeedbackStatus {
        PENDING("Pending Review"),
        IN_PROGRESS("In Progress"),
        RESOLVED("Resolved"),
        CLOSED("Closed");

        private final String displayName;

        FeedbackStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
