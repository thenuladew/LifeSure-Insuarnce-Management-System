package com.example.lifesureinsuarncemanagementsystem.repository;

import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import com.example.lifesureinsuarncemanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Feedback entity operations.
 * Provides CRUD operations and custom query methods for feedback management.
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Find all feedbacks by a specific user
     * @param user the user to find feedbacks for
     * @return list of feedbacks for the user
     */
    List<Feedback> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find all feedbacks by status
     * @param status the feedback status
     * @return list of feedbacks with the specified status
     */
    List<Feedback> findByStatusOrderByCreatedAtDesc(Feedback.FeedbackStatus status);

    /**
     * Find all feedbacks by category
     * @param category the feedback category
     * @return list of feedbacks in the specified category
     */
    List<Feedback> findByCategoryOrderByCreatedAtDesc(Feedback.FeedbackCategory category);

    /**
     * Find feedback by ID and user (for security - users can only access their own feedbacks)
     * @param id the feedback ID
     * @param user the user
     * @return optional feedback if found and belongs to user
     */
    Optional<Feedback> findByIdAndUser(Long id, User user);

    /**
     * Count feedbacks by user
     * @param user the user
     * @return count of feedbacks for the user
     */
    long countByUser(User user);

    /**
     * Count feedbacks by status
     * @param status the feedback status
     * @return count of feedbacks with the specified status
     */
    long countByStatus(Feedback.FeedbackStatus status);

    /**
     * Find feedbacks with average rating calculation
     * @return list of feedbacks with calculated average rating
     */
    @Query("SELECT f FROM Feedback f ORDER BY f.createdAt DESC")
    List<Feedback> findAllOrderByCreatedAtDesc();

    /**
     * Search feedbacks by subject or message containing keyword
     * @param keyword the search keyword
     * @return list of feedbacks containing the keyword
     */
    @Query("SELECT f FROM Feedback f WHERE f.subject LIKE %:keyword% OR f.message LIKE %:keyword% ORDER BY f.createdAt DESC")
    List<Feedback> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Get feedback statistics
     * @return array with [total, pending, in_progress, resolved, closed] counts
     */
    @Query("SELECT " +
           "COUNT(f), " +
           "SUM(CASE WHEN f.status = 'PENDING' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN f.status = 'IN_PROGRESS' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN f.status = 'RESOLVED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN f.status = 'CLOSED' THEN 1 ELSE 0 END) " +
           "FROM Feedback f")
    Object[] getFeedbackStatistics();
}
