package com.example.lifesureinsuarncemanagementsystem.dtos;

import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Feedback entity.
 * Used for transferring feedback data between layers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {

    private Long id;
    private String subject;
    private String message;
    private Integer rating;
    private Feedback.FeedbackCategory category;
    private Feedback.FeedbackStatus status;
    private Long userId;
    private String userName; // For display purposes
    private String userEmail; // For display purposes
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Validation method to check if required fields are present
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return subject != null && !subject.trim().isEmpty() &&
               message != null && !message.trim().isEmpty() &&
               rating != null && rating >= 1 && rating <= 5 &&
               category != null;
    }

    /**
     * Get display name for category
     * @return category display name
     */
    public String getCategoryDisplayName() {
        return category != null ? category.getDisplayName() : "";
    }

    /**
     * Get display name for status
     * @return status display name
     */
    public String getStatusDisplayName() {
        return status != null ? status.getDisplayName() : "";
    }
}
