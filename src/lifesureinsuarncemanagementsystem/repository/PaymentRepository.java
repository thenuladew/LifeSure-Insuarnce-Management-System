package com.example.lifesureinsuarncemanagementsystem.repository;

import com.example.lifesureinsuarncemanagementsystem.model.Payment;
import com.example.lifesureinsuarncemanagementsystem.model.Policy;
import com.example.lifesureinsuarncemanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations.
 * Provides CRUD operations and custom query methods for payment management.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find all payments by a specific user
     * @param user the user to find payments for
     * @return list of payments for the user
     */
    List<Payment> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find all payments by status
     * @param status the payment status
     * @return list of payments with the specified status
     */
    List<Payment> findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus status);

    /**
     * Find all payments by policy
     * @param policy the policy
     * @return list of payments for the specified policy
     */
    List<Payment> findByPolicyOrderByCreatedAtDesc(Policy policy);

    /**
     * Find payment by ID and user (for security - users can only access their own payments)
     * @param id the payment ID
     * @param user the user
     * @return optional payment if found and belongs to user
     */
    Optional<Payment> findByIdAndUser(Long id, User user);

    /**
     * Count payments by user
     * @param user the user
     * @return count of payments for the user
     */
    long countByUser(User user);

    /**
     * Count payments by status
     * @param status the payment status
     * @return count of payments with the specified status
     */
    long countByStatus(Payment.PaymentStatus status);

    /**
     * Find payments with transaction reference
     * @param transactionReference the transaction reference
     * @return list of payments with the specified transaction reference
     */
    List<Payment> findByTransactionReferenceOrderByCreatedAtDesc(String transactionReference);

    /**
     * Find all payments ordered by creation date
     * @return list of all payments ordered by creation date
     */
    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    List<Payment> findAllOrderByCreatedAtDesc();

    /**
     * Search payments by transaction reference or notes containing keyword
     * @param keyword the search keyword
     * @return list of payments containing the keyword
     */
    @Query("SELECT p FROM Payment p WHERE p.transactionReference LIKE %:keyword% OR p.notes LIKE %:keyword% ORDER BY p.createdAt DESC")
    List<Payment> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Get payment statistics
     * @return array with [total, pending, approved, rejected, processing, completed] counts
     */
    @Query("SELECT " +
           "COUNT(p), " +
           "SUM(CASE WHEN p.status = 'PENDING' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.status = 'APPROVED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.status = 'REJECTED' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.status = 'PROCESSING' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.status = 'COMPLETED' THEN 1 ELSE 0 END) " +
           "FROM Payment p")
    Object[] getPaymentStatistics();

    /**
     * Find payments by user and policy
     * @param user the user
     * @param policy the policy
     * @return list of payments for the user and policy combination
     */
    List<Payment> findByUserAndPolicyOrderByCreatedAtDesc(User user, Policy policy);

    /**
     * Find payments by payment method
     * @param paymentMethod the payment method
     * @return list of payments with the specified payment method
     */
    List<Payment> findByPaymentMethodOrderByCreatedAtDesc(String paymentMethod);
}
