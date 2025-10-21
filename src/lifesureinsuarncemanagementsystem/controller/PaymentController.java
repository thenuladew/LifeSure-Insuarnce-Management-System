package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.PaymentDTO;
import com.example.lifesureinsuarncemanagementsystem.dtos.PolicyDTO;
import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.model.Payment;
import com.example.lifesureinsuarncemanagementsystem.service.PaymentService;
import com.example.lifesureinsuarncemanagementsystem.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

/**
 * Controller for handling payment-related requests.
 * Provides web page mappings for the payment system.
 */
@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PolicyService policyService;

    // ==================== WEB PAGE MAPPINGS ====================

    /**
     * Display payment form for creating new payment
     * @param policyId the policy ID to purchase
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return payment form page
     */
    @GetMapping("/form")
    public String showPaymentForm(@RequestParam(required = false) Long policyId,
                                 Model model, 
                                 HttpSession session, 
                                 RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to make a payment.");
            return "redirect:/login";
        }
        
        PaymentDTO payment = new PaymentDTO();
        payment.setUserId((long) user.getId());
        
        // If policyId is provided, set it
        if (policyId != null) {
            payment.setPolicyId(policyId);
        }
        
        // Get all available policies for selection
        List<PolicyDTO> policies = policyService.getAllPolicies();
        
        model.addAttribute("payment", payment);
        model.addAttribute("policies", policies);
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
        model.addAttribute("user", user);
        return "payment/payment-form";
    }

    /**
     * Display user's payment list
     * @param userId the user ID
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return payment list page
     */
    @GetMapping("/user/{userId}")
    public String showUserPayments(@PathVariable Long userId, 
                                  Model model, 
                                  HttpSession session, 
                                  RedirectAttributes redirectAttributes) {
        // Check if user is logged in and matches the requested userId
        UserDTO loggedInUser = (UserDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to view your payments.");
            return "redirect:/login";
        }
        
        // Check if the logged-in user matches the requested userId
        if (loggedInUser.getId() != userId.intValue()) {
            redirectAttributes.addFlashAttribute("error", "🔒 You can only view your own payments.");
            return "redirect:/payment/user/" + loggedInUser.getId();
        }
        
        try {
            List<PaymentDTO> payments = paymentService.getPaymentsByUser(userId);
            model.addAttribute("payments", payments);
            model.addAttribute("userId", userId);
            model.addAttribute("user", loggedInUser);
            return "payment/user-payment-list";
        } catch (RuntimeException e) {
            // If user not found, redirect to login
            if (e.getMessage().contains("User not found")) {
                redirectAttributes.addFlashAttribute("error", "🔒 User not found. Please login again.");
                return "redirect:/login";
            }
            // For other errors, redirect to home with error message
            redirectAttributes.addFlashAttribute("error", "Error loading payments: " + e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            // For unexpected errors, redirect to home
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again.");
            return "redirect:/";
        }
    }

    /**
     * Display payment detail view
     * @param id the payment ID
     * @param userId the user ID
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return payment detail page
     */
    @GetMapping("/detail/{id}")
    public String showPaymentDetail(@PathVariable Long id, 
                                   @RequestParam Long userId, 
                                   Model model,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserDTO loggedInUser = (UserDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to view payment details.");
            return "redirect:/login";
        }
        
        // Check if the logged-in user matches the requested userId
        if (loggedInUser.getId() != userId.intValue()) {
            redirectAttributes.addFlashAttribute("error", "🔒 You can only view your own payments.");
            return "redirect:/payment/user/" + loggedInUser.getId();
        }
        
        try {
            PaymentDTO payment = paymentService.getPaymentByIdAndUser(id, userId);
            model.addAttribute("payment", payment);
            model.addAttribute("userId", userId);
            model.addAttribute("user", loggedInUser);
            return "payment/payment-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading payment: " + e.getMessage());
            return "redirect:/payment/user/" + userId;
        }
    }

    /**
     * Display payment edit form
     * @param id the payment ID
     * @param userId the user ID
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return payment edit form page
     */
    @GetMapping("/edit/{id}")
    public String showEditPaymentForm(@PathVariable Long id, 
                                     @RequestParam Long userId, 
                                     Model model,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserDTO loggedInUser = (UserDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to edit payment.");
            return "redirect:/login";
        }
        
        // Check if the logged-in user matches the requested userId
        if (loggedInUser.getId() != userId.intValue()) {
            redirectAttributes.addFlashAttribute("error", "🔒 You can only edit your own payments.");
            return "redirect:/payment/user/" + loggedInUser.getId();
        }
        
        try {
            PaymentDTO payment = paymentService.getPaymentByIdAndUser(id, userId);
            
            // Only allow editing if payment is pending
            if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
                redirectAttributes.addFlashAttribute("error", "Cannot edit payment that is not in pending status.");
                return "redirect:/payment/user/" + userId;
            }
            
            // Get all available policies for selection
            List<PolicyDTO> policies = policyService.getAllPolicies();
            
            model.addAttribute("payment", payment);
            model.addAttribute("policies", policies);
            model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
            model.addAttribute("userId", userId);
            model.addAttribute("user", loggedInUser);
            return "payment/payment-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading payment: " + e.getMessage());
            return "redirect:/payment/user/" + userId;
        }
    }

    /**
     * Display admin payment management page
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return admin payment management page
     */
    @GetMapping("/admin")
    public String showAdminPaymentManagement(Model model, 
                                            HttpSession session, 
                                            RedirectAttributes redirectAttributes) {
        // Check if admin is logged in
        Object admin = session.getAttribute("loggedInAdmin");
        if (admin == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Admin access required.");
            return "redirect:/admin/login";
        }
        
        try {
            List<PaymentDTO> payments = paymentService.getAllPayments();
            Object[] stats = paymentService.getPaymentStatistics();
            
            model.addAttribute("payments", payments);
            model.addAttribute("totalPayments", stats[0]);
            model.addAttribute("pendingPayments", stats[1]);
            model.addAttribute("approvedPayments", stats[2]);
            model.addAttribute("rejectedPayments", stats[3]);
            model.addAttribute("processingPayments", stats[4]);
            model.addAttribute("completedPayments", stats[5]);
            return "payment/admin-payment-management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading payments: " + e.getMessage());
            return "redirect:/UserAdminDashboard";
        }
    }

    // ==================== FORM SUBMISSIONS ====================

    /**
     * Handle payment form submission
     * @param payment the payment data
     * @param paymentSlipFile the uploaded payment slip file
     * @param redirectAttributes for flash messages
     * @return redirect to user payment list
     */
    @PostMapping("/submit")
    public String submitPayment(@ModelAttribute PaymentDTO payment,
                               @RequestParam("paymentSlipFile") MultipartFile paymentSlipFile,
                               RedirectAttributes redirectAttributes) {
        try {
            paymentService.createPayment(payment, paymentSlipFile);
            redirectAttributes.addFlashAttribute("success", "Payment submitted successfully! Please wait for admin approval.");
            return "redirect:/payment/user/" + payment.getUserId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting payment: " + e.getMessage());
            return "redirect:/payment/form?policyId=" + payment.getPolicyId();
        }
    }

    /**
     * Handle payment update
     * @param payment the updated payment data
     * @param userId the user ID
     * @param paymentSlipFile the new payment slip file (optional)
     * @param redirectAttributes for flash messages
     * @return redirect to payment detail
     */
    @PostMapping("/update")
    public String updatePayment(@ModelAttribute PaymentDTO payment,
                               @RequestParam Long userId,
                               @RequestParam(value = "paymentSlipFile", required = false) MultipartFile paymentSlipFile,
                               RedirectAttributes redirectAttributes) {
        try {
            payment.setUserId(userId);
            paymentService.updatePayment(payment, userId, paymentSlipFile);
            redirectAttributes.addFlashAttribute("success", "Payment updated successfully!");
            return "redirect:/payment/user/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating payment: " + e.getMessage());
            return "redirect:/payment/edit/" + payment.getId() + "?userId=" + userId;
        }
    }

    /**
     * Handle payment deletion
     * @param id the payment ID
     * @param userId the user ID
     * @param redirectAttributes for flash messages
     * @return redirect to user payment list
     */
    @PostMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id, 
                               @RequestParam Long userId,
                               RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id, userId);
            redirectAttributes.addFlashAttribute("success", "Payment deleted successfully!");
            return "redirect:/payment/user/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting payment: " + e.getMessage());
            return "redirect:/payment/user/" + userId;
        }
    }

    /**
     * Handle admin status update
     * @param paymentId the payment ID
     * @param status the new status
     * @param adminNotes the admin notes
     * @param redirectAttributes for flash messages
     * @return redirect to admin payment management
     */
    @PostMapping("/admin/update-status")
    public String updatePaymentStatus(@RequestParam Long paymentId,
                                     @RequestParam Payment.PaymentStatus status,
                                     @RequestParam(required = false) String adminNotes,
                                     RedirectAttributes redirectAttributes) {
        try {
            paymentService.updatePaymentStatus(paymentId, status, adminNotes);
            redirectAttributes.addFlashAttribute("success", "Payment status updated successfully!");
            return "redirect:/payment/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating payment status: " + e.getMessage());
            return "redirect:/payment/admin";
        }
    }

    // ==================== REST API ENDPOINTS ====================

    /**
     * Get payment statistics for admin dashboard
     * @return payment statistics as JSON
     */
    @GetMapping("/api/admin/stats")
    @ResponseBody
    public ResponseEntity<Object> getPaymentStatistics() {
        try {
            Object[] stats = paymentService.getPaymentStatistics();
            if (stats != null && stats.length >= 6) {
                return ResponseEntity.ok(Map.of(
                    "total", stats[0] != null ? stats[0] : 0,
                    "pending", stats[1] != null ? stats[1] : 0,
                    "approved", stats[2] != null ? stats[2] : 0,
                    "rejected", stats[3] != null ? stats[3] : 0,
                    "processing", stats[4] != null ? stats[4] : 0,
                    "completed", stats[5] != null ? stats[5] : 0
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "total", 0,
                    "pending", 0,
                    "approved", 0,
                    "rejected", 0,
                    "processing", 0,
                    "completed", 0
                ));
            }
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error fetching payment statistics: " + e.getMessage());
            return ResponseEntity.ok(Map.of(
                "total", 0,
                "pending", 0,
                "approved", 0,
                "rejected", 0,
                "processing", 0,
                "completed", 0
            ));
        }
    }

    /**
     * Download payment slip file
     * @param paymentId the payment ID
     * @param userId the user ID
     * @param session the HTTP session
     * @return file download response
     */
    @GetMapping("/download-slip/{paymentId}")
    public ResponseEntity<byte[]> downloadPaymentSlip(@PathVariable Long paymentId,
                                                       @RequestParam Long userId,
                                                       HttpSession session) {
        try {
            // Check if user is logged in
            UserDTO loggedInUser = (UserDTO) session.getAttribute("loggedInUser");
            if (loggedInUser == null || loggedInUser.getId() != userId.intValue()) {
                return ResponseEntity.notFound().build();
            }

            PaymentDTO payment = paymentService.getPaymentByIdAndUser(paymentId, userId);
            
            if (payment == null || payment.getPaymentSlipPath() == null || payment.getPaymentSlipFilename() == null) {
                return ResponseEntity.notFound().build();
            }

            // Read file from the uploads directory
            java.nio.file.Path filePath = java.nio.file.Paths.get(payment.getPaymentSlipPath());
            if (!java.nio.file.Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = java.nio.file.Files.readAllBytes(filePath);
            
            // Determine content type based on file extension
            String contentType = "application/octet-stream";
            String filename = payment.getPaymentSlipFilename();
            if (filename != null) {
                String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
                switch (extension) {
                    case "jpg":
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    case "png":
                        contentType = "image/png";
                        break;
                    case "pdf":
                        contentType = "application/pdf";
                        break;
                    case "gif":
                        contentType = "image/gif";
                        break;
                    case "bmp":
                        contentType = "image/bmp";
                        break;
                    default:
                        contentType = "application/octet-stream";
                        break;
                }
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Type", contentType)
                    .body(fileContent);
                    
        } catch (Exception e) {
            System.err.println("Error downloading payment slip: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Download payment slip file for admin
     * @param paymentId the payment ID
     * @param session the HTTP session
     * @return file download response
     */
    @GetMapping("/admin/download-slip/{paymentId}")
    public ResponseEntity<byte[]> downloadPaymentSlipForAdmin(@PathVariable Long paymentId,
                                                               HttpSession session) {
        try {
            // Check if admin is logged in
            Object admin = session.getAttribute("loggedInAdmin");
            if (admin == null) {
                return ResponseEntity.notFound().build();
            }

            PaymentDTO payment = paymentService.getPaymentById(paymentId);
            
            if (payment == null || payment.getPaymentSlipPath() == null || payment.getPaymentSlipFilename() == null) {
                return ResponseEntity.notFound().build();
            }

            // Read file from the uploads directory
            java.nio.file.Path filePath = java.nio.file.Paths.get(payment.getPaymentSlipPath());
            if (!java.nio.file.Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = java.nio.file.Files.readAllBytes(filePath);
            
            // Determine content type based on file extension
            String contentType = "application/octet-stream";
            String filename = payment.getPaymentSlipFilename();
            if (filename != null) {
                String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
                switch (extension) {
                    case "jpg":
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    case "png":
                        contentType = "image/png";
                        break;
                    case "pdf":
                        contentType = "application/pdf";
                        break;
                    case "gif":
                        contentType = "image/gif";
                        break;
                    case "bmp":
                        contentType = "image/bmp";
                        break;
                    default:
                        contentType = "application/octet-stream";
                        break;
                }
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Type", contentType)
                    .body(fileContent);
                    
        } catch (Exception e) {
            System.err.println("Error downloading payment slip: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
