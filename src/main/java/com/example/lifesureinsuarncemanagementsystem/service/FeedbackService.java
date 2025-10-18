package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.dtos.FeedbackDTO;
import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import com.example.lifesureinsuarncemanagementsystem.model.User;
import com.example.lifesureinsuarncemanagementsystem.repository.FeedbackRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.userRepository;
import org.modelmapper.ModelMapper;
// ...existing code...
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ...existing code...
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing feedback operations.
 * Provides business logic for CRUD operations on feedback entities.
 */
@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final userRepository userRepository;
    private final ModelMapper modelMapper;

    public FeedbackService(FeedbackRepository feedbackRepository, 
                          userRepository userRepository, 
                          ModelMapper modelMapper) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create a new feedback
     * @param feedbackDTO the feedback data
     * @return created feedback DTO
     * @throws RuntimeException if user not found or validation fails
     */
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        // Validate input
        if (!feedbackDTO.isValid()) {
            throw new RuntimeException("Invalid feedback data. Please check all required fields.");
        }

        // Find user
        Optional<User> userOpt = userRepository.findById(feedbackDTO.getUserId().intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + feedbackDTO.getUserId());
        }

        User user = userOpt.get();

        // Create feedback entity
        Feedback feedback = new Feedback();
        feedback.setSubject(feedbackDTO.getSubject().trim());
        feedback.setMessage(feedbackDTO.getMessage().trim());
        feedback.setRating(feedbackDTO.getRating());
        feedback.setCategory(feedbackDTO.getCategory());
        feedback.setStatus(Feedback.FeedbackStatus.PENDING);
        feedback.setUser(user);

        // Save feedback
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }

    /**
     * Get all feedbacks for a specific user
     * @param userId the user ID
     * @return list of feedback DTOs
     */
    @Transactional(readOnly = true)
    public List<FeedbackDTO> getFeedbacksByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        List<Feedback> feedbacks = feedbackRepository.findByUserOrderByCreatedAtDesc(user);
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all feedbacks (admin view)
     * @return list of all feedback DTOs
     */
    @Transactional(readOnly = true)
    public List<FeedbackDTO> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAllOrderByCreatedAtDesc();
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get feedback by ID and user (for security)
     * @param feedbackId the feedback ID
     * @param userId the user ID
     * @return feedback DTO if found and belongs to user
     */
    @Transactional(readOnly = true)
    public FeedbackDTO getFeedbackByIdAndUser(Long feedbackId, Long userId) {
        // Debug logging
        System.out.println("Service: Getting feedback ID: " + feedbackId + " for user ID: " + userId);
        
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            System.err.println("Service: User not found with ID: " + userId);
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        System.out.println("Service: Found user: " + user.getName() + " (ID: " + user.getId() + ")");
        
        Optional<Feedback> feedbackOpt = feedbackRepository.findByIdAndUser(feedbackId, user);
        if (feedbackOpt.isEmpty()) {
            System.err.println("Service: Feedback not found or access denied for ID: " + feedbackId);
            throw new RuntimeException("Feedback not found or access denied for ID: " + feedbackId);
        }
        
        Feedback feedback = feedbackOpt.get();
        System.out.println("Service: Found feedback: " + feedback.getSubject() + " (ID: " + feedback.getId() + ")");
        
        return convertToDTO(feedback);
    }

    /**
     * Update feedback
     * @param feedbackDTO the updated feedback data
     * @param userId the user ID (for security)
     * @return updated feedback DTO
     */
    public FeedbackDTO updateFeedback(FeedbackDTO feedbackDTO, Long userId) {
        // Validate input
        if (!feedbackDTO.isValid()) {
            throw new RuntimeException("Invalid feedback data. Please check all required fields.");
        }

        // Find user
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();

        // Find existing feedback
        Optional<Feedback> existingFeedbackOpt = feedbackRepository.findByIdAndUser(feedbackDTO.getId(), user);
        if (existingFeedbackOpt.isEmpty()) {
            throw new RuntimeException("Feedback not found or access denied for ID: " + feedbackDTO.getId());
        }

        Feedback existingFeedback = existingFeedbackOpt.get();

        // Update fields (only allow certain fields to be updated by user)
        existingFeedback.setSubject(feedbackDTO.getSubject().trim());
        existingFeedback.setMessage(feedbackDTO.getMessage().trim());
        existingFeedback.setRating(feedbackDTO.getRating());
        existingFeedback.setCategory(feedbackDTO.getCategory());

        // Save updated feedback
        Feedback updatedFeedback = feedbackRepository.save(existingFeedback);
        return convertToDTO(updatedFeedback);
    }

    /**
     * Delete feedback
     * @param feedbackId the feedback ID
     * @param userId the user ID (for security)
     * @return true if deleted successfully
     */
    public boolean deleteFeedback(Long feedbackId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        Optional<Feedback> feedbackOpt = feedbackRepository.findByIdAndUser(feedbackId, user);
        if (feedbackOpt.isEmpty()) {
            throw new RuntimeException("Feedback not found or access denied for ID: " + feedbackId);
        }

        feedbackRepository.deleteById(feedbackId);
        return true;
    }

    /**
     * Update feedback status (admin only)
     * @param feedbackId the feedback ID
     * @param status the new status
     * @param adminResponse optional admin response (ignored - no longer stored)
     * @return updated feedback DTO
     */
    public FeedbackDTO updateFeedbackStatus(Long feedbackId, Feedback.FeedbackStatus status, String adminResponse) {
        Optional<Feedback> feedbackOpt = feedbackRepository.findById(feedbackId);
        if (feedbackOpt.isEmpty()) {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }

        Feedback feedback = feedbackOpt.get();
        feedback.setStatus(status);
        
        // Admin response functionality removed - only status is updated now

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(updatedFeedback);
    }

    /**
     * Get feedbacks by status
     * @param status the feedback status
     * @return list of feedback DTOs
     */
    @Transactional(readOnly = true)
    public List<FeedbackDTO> getFeedbacksByStatus(Feedback.FeedbackStatus status) {
        List<Feedback> feedbacks = feedbackRepository.findByStatusOrderByCreatedAtDesc(status);
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search feedbacks by keyword
     * @param keyword the search keyword
     * @return list of matching feedback DTOs
     */
    @Transactional(readOnly = true)
    public List<FeedbackDTO> searchFeedbacks(String keyword) {
        List<Feedback> feedbacks = feedbackRepository.searchByKeyword(keyword);
        return feedbacks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get feedback statistics
     * @return array with feedback counts
     */
    @Transactional(readOnly = true)
    public Object[] getFeedbackStatistics() {
        try {
            Object[] stats = feedbackRepository.getFeedbackStatistics();
            if (stats == null || stats.length < 5) {
                // Return default values if query fails or returns insufficient data
                return new Object[]{0L, 0L, 0L, 0L, 0L};
            }
            return stats;
        } catch (Exception e) {
            // Return default values if any error occurs
            return new Object[]{0L, 0L, 0L, 0L, 0L};
        }
    }

    /**
     * Delete all feedback for a specific user (used when deleting user)
     * @param userId the user ID
     * @return number of feedback records deleted
     */
    public int deleteAllFeedbackByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            return 0; // User doesn't exist, nothing to delete
        }
        
        User user = userOpt.get();
        List<Feedback> userFeedbacks = feedbackRepository.findByUserOrderByCreatedAtDesc(user);
        int count = userFeedbacks.size();
        
        // Delete all feedback for this user
        feedbackRepository.deleteAll(userFeedbacks);
        
        return count;
    }

    /**
     * Convert Feedback entity to DTO
     * @param feedback the feedback entity
     * @return feedback DTO
     */
    private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO dto = modelMapper.map(feedback, FeedbackDTO.class);
        dto.setUserId((long) feedback.getUser().getId());
        dto.setUserName(feedback.getUser().getName());
        dto.setUserEmail(feedback.getUser().getEmail());
        return dto;
    }
}
