package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.dtos.PaymentDTO;
import com.example.lifesureinsuarncemanagementsystem.model.Payment;
import com.example.lifesureinsuarncemanagementsystem.model.Policy;
import com.example.lifesureinsuarncemanagementsystem.model.User;
import com.example.lifesureinsuarncemanagementsystem.repository.PaymentRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.PolicyRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.userRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing payment operations.
 * Provides business logic for CRUD operations on payment entities.
 */
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final userRepository userRepository;
    private final PolicyRepository policyRepository;
    private final ModelMapper modelMapper;
    private final String uploadDir = "uploads/payment-slips/";

    public PaymentService(PaymentRepository paymentRepository, 
                          userRepository userRepository,
                          PolicyRepository policyRepository,
                          ModelMapper modelMapper) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.policyRepository = policyRepository;
        this.modelMapper = modelMapper;
        
        // Create upload directory if it doesn't exist
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
        }
    }

    /**
     * Create a new payment
     * @param paymentDTO the payment data
     * @param paymentSlipFile the uploaded payment slip file
     * @return created payment DTO
     * @throws RuntimeException if user/policy not found or validation fails
     */
    public PaymentDTO createPayment(PaymentDTO paymentDTO, MultipartFile paymentSlipFile) {
        // Validate input
        if (!paymentDTO.isValid()) {
            throw new RuntimeException("Invalid payment data. Please check all required fields.");
        }

        // Find user
        Optional<User> userOpt = userRepository.findById(paymentDTO.getUserId().intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + paymentDTO.getUserId());
        }

        // Find policy
        Optional<Policy> policyOpt = policyRepository.findById(paymentDTO.getPolicyId());
        if (policyOpt.isEmpty()) {
            throw new RuntimeException("Policy not found with ID: " + paymentDTO.getPolicyId());
        }

        User user = userOpt.get();
        Policy policy = policyOpt.get();

        // Create payment entity
        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setTransactionReference(paymentDTO.getTransactionReference());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setNotes(paymentDTO.getNotes());
        payment.setUser(user);
        payment.setPolicy(policy);

        // Handle file upload
        if (paymentSlipFile != null && !paymentSlipFile.isEmpty()) {
            try {
                String filename = savePaymentSlipFile(paymentSlipFile);
                payment.setPaymentSlipFilename(filename);
                payment.setPaymentSlipPath(uploadDir + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload payment slip: " + e.getMessage());
            }
        }

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    /**
     * Get all payments for a specific user
     * @param userId the user ID
     * @return list of payment DTOs
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        List<Payment> payments = paymentRepository.findByUserOrderByCreatedAtDesc(user);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all payments (admin view)
     * @return list of all payment DTOs
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAllOrderByCreatedAtDesc();
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payment by ID and user (for security)
     * @param paymentId the payment ID
     * @param userId the user ID
     * @return payment DTO if found and belongs to user
     */
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByIdAndUser(Long paymentId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        Optional<Payment> paymentOpt = paymentRepository.findByIdAndUser(paymentId, user);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found or access denied for ID: " + paymentId);
        }

        return convertToDTO(paymentOpt.get());
    }

    /**
     * Get payment by ID (admin access)
     * @param paymentId the payment ID
     * @return payment DTO if found
     */
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }

        return convertToDTO(paymentOpt.get());
    }

    /**
     * Update payment
     * @param paymentDTO the updated payment data
     * @param userId the user ID (for security)
     * @param paymentSlipFile the new payment slip file (optional)
     * @return updated payment DTO
     */
    public PaymentDTO updatePayment(PaymentDTO paymentDTO, Long userId, MultipartFile paymentSlipFile) {
        // Validate input
        if (!paymentDTO.isValid()) {
            throw new RuntimeException("Invalid payment data. Please check all required fields.");
        }

        // Find user
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();

        // Find existing payment
        Optional<Payment> existingPaymentOpt = paymentRepository.findByIdAndUser(paymentDTO.getId(), user);
        if (existingPaymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found or access denied for ID: " + paymentDTO.getId());
        }

        Payment existingPayment = existingPaymentOpt.get();

        // Only allow updates if payment is still pending
        if (existingPayment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("Cannot update payment that is not in pending status.");
        }

        // Update fields
        existingPayment.setAmount(paymentDTO.getAmount());
        existingPayment.setPaymentMethod(paymentDTO.getPaymentMethod());
        existingPayment.setTransactionReference(paymentDTO.getTransactionReference());
        existingPayment.setNotes(paymentDTO.getNotes());

        // Handle new file upload if provided
        if (paymentSlipFile != null && !paymentSlipFile.isEmpty()) {
            try {
                // Delete old file if exists
                if (existingPayment.getPaymentSlipPath() != null) {
                    deletePaymentSlipFile(existingPayment.getPaymentSlipPath());
                }
                
                String filename = savePaymentSlipFile(paymentSlipFile);
                existingPayment.setPaymentSlipFilename(filename);
                existingPayment.setPaymentSlipPath(uploadDir + filename);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload payment slip: " + e.getMessage());
            }
        }

        // Save updated payment
        Payment updatedPayment = paymentRepository.save(existingPayment);
        return convertToDTO(updatedPayment);
    }

    /**
     * Delete payment
     * @param paymentId the payment ID
     * @param userId the user ID (for security)
     * @return true if deleted successfully
     */
    public boolean deletePayment(Long paymentId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        Optional<Payment> paymentOpt = paymentRepository.findByIdAndUser(paymentId, user);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found or access denied for ID: " + paymentId);
        }

        Payment payment = paymentOpt.get();
        
        // Only allow deletion if payment is still pending
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("Cannot delete payment that is not in pending status.");
        }

        // Delete associated file if exists
        if (payment.getPaymentSlipPath() != null) {
            deletePaymentSlipFile(payment.getPaymentSlipPath());
        }

        paymentRepository.deleteById(paymentId);
        return true;
    }

    /**
     * Update payment status (admin only)
     * @param paymentId the payment ID
     * @param status the new status
     * @param adminNotes optional admin notes
     * @return updated payment DTO
     */
    public PaymentDTO updatePaymentStatus(Long paymentId, Payment.PaymentStatus status, String adminNotes) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + paymentId);
        }

        Payment payment = paymentOpt.get();
        payment.setStatus(status);
        
        // Add admin notes if provided
        if (adminNotes != null && !adminNotes.trim().isEmpty()) {
            String existingNotes = payment.getNotes() != null ? payment.getNotes() : "";
            payment.setNotes(existingNotes + "\n[Admin Note]: " + adminNotes.trim());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToDTO(updatedPayment);
    }

    /**
     * Get payments by status
     * @param status the payment status
     * @return list of payment DTOs
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByStatusOrderByCreatedAtDesc(status);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search payments by keyword
     * @param keyword the search keyword
     * @return list of matching payment DTOs
     */
    @Transactional(readOnly = true)
    public List<PaymentDTO> searchPayments(String keyword) {
        List<Payment> payments = paymentRepository.searchByKeyword(keyword);
        return payments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get payment statistics
     * @return array with payment counts
     */
    @Transactional(readOnly = true)
    public Object[] getPaymentStatistics() {
        try {
            Object[] stats = paymentRepository.getPaymentStatistics();
            if (stats == null || stats.length < 6) {
                // Return default values if query fails or returns insufficient data
                return new Object[]{0L, 0L, 0L, 0L, 0L, 0L};
            }
            return stats;
        } catch (Exception e) {
            // Return default values if any error occurs
            return new Object[]{0L, 0L, 0L, 0L, 0L, 0L};
        }
    }

    /**
     * Delete all payments for a specific user (used when deleting user)
     * @param userId the user ID
     * @return number of payment records deleted
     */
    public int deleteAllPaymentsByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId.intValue());
        if (userOpt.isEmpty()) {
            return 0; // User doesn't exist, nothing to delete
        }
        
        User user = userOpt.get();
        List<Payment> userPayments = paymentRepository.findByUserOrderByCreatedAtDesc(user);
        int count = userPayments.size();
        
        // Delete associated files
        for (Payment payment : userPayments) {
            if (payment.getPaymentSlipPath() != null) {
                deletePaymentSlipFile(payment.getPaymentSlipPath());
            }
        }
        
        // Delete all payments for this user
        paymentRepository.deleteAll(userPayments);
        
        return count;
    }

    /**
     * Save payment slip file
     * @param file the uploaded file
     * @return the saved filename
     * @throws IOException if file save fails
     */
    private String savePaymentSlipFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(uploadDir + filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return filename;
    }

    /**
     * Delete payment slip file
     * @param filePath the file path to delete
     */
    private void deletePaymentSlipFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete payment slip file: " + e.getMessage());
        }
    }

    /**
     * Convert Payment entity to DTO
     * @param payment the payment entity
     * @return payment DTO
     */
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = modelMapper.map(payment, PaymentDTO.class);
        dto.setUserId((long) payment.getUser().getId());
        dto.setUserName(payment.getUser().getName());
        dto.setUserEmail(payment.getUser().getEmail());
        dto.setPolicyId(payment.getPolicy().getId());
        dto.setPolicyName(payment.getPolicy().getPolicyName());
        dto.setPolicyType(payment.getPolicy().getPolicyType());
        return dto;
    }
}
