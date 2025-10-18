package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.FeedbackDTO;
import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.model.Feedback;
import com.example.lifesureinsuarncemanagementsystem.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

/**
 * Controller for handling feedback-related requests.
 * Provides web page mappings for the feedback system.
 */
@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // ==================== WEB PAGE MAPPINGS ====================

    /**
     * Display feedback form for creating new feedback
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return feedback form page
     */
    @GetMapping("/form")
    public String showFeedbackForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to submit feedback.");
            return "redirect:/login";
        }
        
        FeedbackDTO feedback = new FeedbackDTO();
        feedback.setUserId((long) user.getId());
        
        model.addAttribute("feedback", feedback);
        model.addAttribute("categories", Feedback.FeedbackCategory.values());
        model.addAttribute("user", user);
        return "feedback/feedback-form";
    }

    /**
     * Display user's feedback list
     * @param userId the user ID
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return feedback list page
     */
    @GetMapping("/user/{userId}")
    public String showUserFeedbacks(@PathVariable Long userId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if user is logged in and matches the requested userId
        UserDTO loggedInUser = (UserDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to view your feedback.");
            return "redirect:/login";
        }
        
        // Check if the logged-in user matches the requested userId
        if (loggedInUser.getId() != userId.intValue()) {
            redirectAttributes.addFlashAttribute("error", "🔒 You can only view your own feedback.");
            return "redirect:/feedback/user/" + loggedInUser.getId();
        }
        
        try {
            List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksByUser(userId);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("userId", userId);
            model.addAttribute("user", loggedInUser);
            return "feedback/user-feedback-list";
        } catch (RuntimeException e) {
            // If user not found, redirect to login
            if (e.getMessage().contains("User not found")) {
                redirectAttributes.addFlashAttribute("error", "🔒 User not found. Please login again.");
                return "redirect:/login";
            }
            // For other errors, redirect to home with error message
            redirectAttributes.addFlashAttribute("error", "Error loading feedbacks: " + e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            // For unexpected errors, redirect to home
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred. Please try again.");
            return "redirect:/";
        }
    }

    /**
     * Display feedback detail view
     * @param id the feedback ID
     * @param userId the user ID
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return feedback detail page
     */
    @GetMapping("/detail/{id}")
    public String showFeedbackDetail(@PathVariable Long id, 
                                   @RequestParam Long userId, 
                                   Model model,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserDTO loggedInUser = (UserDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to view feedback details.");
            return "redirect:/login";
        }
        
        // Check if the logged-in user matches the requested userId
        if (loggedInUser.getId() != userId.intValue()) {
            redirectAttributes.addFlashAttribute("error", "🔒 You can only view your own feedback.");
            return "redirect:/feedback/user/" + loggedInUser.getId();
        }
        
        try {
            // Debug logging
            System.out.println("Loading feedback detail - ID: " + id + ", User ID: " + userId);
            System.out.println("Logged in user ID: " + loggedInUser.getId());
            
            FeedbackDTO feedback = feedbackService.getFeedbackByIdAndUser(id, userId);
            model.addAttribute("feedback", feedback);
            model.addAttribute("userId", userId);
            model.addAttribute("user", loggedInUser);
            return "feedback/feedback-detail";
        } catch (Exception e) {
            // Enhanced error logging
            System.err.println("Error loading feedback detail: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error loading feedback: " + e.getMessage());
            return "redirect:/feedback/user/" + userId;
        }
    }

    /**
     * Display feedback edit form
     * @param id the feedback ID
     * @param userId the user ID
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return feedback edit form page
     */
    @GetMapping("/edit/{id}")
    public String showEditFeedbackForm(@PathVariable Long id, 
                                     @RequestParam Long userId, 
                                     Model model,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserDTO loggedInUser = (UserDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to edit feedback.");
            return "redirect:/login";
        }
        
        // Check if the logged-in user matches the requested userId
        if (loggedInUser.getId() != userId.intValue()) {
            redirectAttributes.addFlashAttribute("error", "🔒 You can only edit your own feedback.");
            return "redirect:/feedback/user/" + loggedInUser.getId();
        }
        
        try {
            FeedbackDTO feedback = feedbackService.getFeedbackByIdAndUser(id, userId);
            model.addAttribute("feedback", feedback);
            model.addAttribute("categories", Feedback.FeedbackCategory.values());
            model.addAttribute("userId", userId);
            model.addAttribute("user", loggedInUser);
            return "feedback/feedback-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading feedback: " + e.getMessage());
            return "redirect:/feedback/user/" + userId;
        }
    }

    /**
     * Display admin feedback management page
     * @param model the model to add attributes
     * @param session the HTTP session
     * @param redirectAttributes for flash messages
     * @return admin feedback management page
     */
    @GetMapping("/admin")
    public String showAdminFeedbackManagement(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        // Check if admin is logged in
        Object admin = session.getAttribute("loggedInAdmin");
        if (admin == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Admin access required.");
            return "redirect:/admin/login";
        }
        
        try {
            List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
            Object[] stats = feedbackService.getFeedbackStatistics();
            
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("totalFeedbacks", stats[0]);
            model.addAttribute("pendingFeedbacks", stats[1]);
            model.addAttribute("inProgressFeedbacks", stats[2]);
            model.addAttribute("resolvedFeedbacks", stats[3]);
            model.addAttribute("closedFeedbacks", stats[4]);
            return "feedback/admin-feedback-management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error loading feedbacks: " + e.getMessage());
            return "redirect:/UserAdminDashboard";
        }
    }

    // ==================== FORM SUBMISSIONS ====================

    /**
     * Handle feedback form submission
     * @param feedback the feedback data
     * @param redirectAttributes for flash messages
     * @return redirect to user feedback list
     */
    @PostMapping("/submit")
    public String submitFeedback(@ModelAttribute FeedbackDTO feedback, 
                               RedirectAttributes redirectAttributes) {
        try {
            feedbackService.createFeedback(feedback);
            redirectAttributes.addFlashAttribute("success", "Feedback submitted successfully!");
            return "redirect:/feedback/user/" + feedback.getUserId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting feedback: " + e.getMessage());
            return "redirect:/feedback/form";
        }
    }

    /**
     * Handle feedback update
     * @param feedback the updated feedback data
     * @param userId the user ID
     * @param redirectAttributes for flash messages
     * @return redirect to feedback detail
     */
    @PostMapping("/update")
    public String updateFeedback(@ModelAttribute FeedbackDTO feedback, 
                               @RequestParam Long userId,
                               RedirectAttributes redirectAttributes) {
        try {
            // Debug logging
            System.out.println("Updating feedback ID: " + feedback.getId() + " for user ID: " + userId);
            System.out.println("Feedback subject: " + feedback.getSubject());
            
            feedback.setUserId(userId);
            feedbackService.updateFeedback(feedback, userId);
            redirectAttributes.addFlashAttribute("success", "Feedback updated successfully!");
            return "redirect:/feedback/user/" + userId;
        } catch (Exception e) {
            // Enhanced error logging
            System.err.println("Error updating feedback: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error updating feedback: " + e.getMessage());
            return "redirect:/feedback/edit/" + feedback.getId() + "?userId=" + userId;
        }
    }

    /**
     * Handle feedback deletion
     * @param id the feedback ID
     * @param userId the user ID
     * @param redirectAttributes for flash messages
     * @return redirect to user feedback list
     */
    @PostMapping("/delete/{id}")
    public String deleteFeedback(@PathVariable Long id, 
                               @RequestParam Long userId,
                               RedirectAttributes redirectAttributes) {
        try {
            feedbackService.deleteFeedback(id, userId);
            redirectAttributes.addFlashAttribute("success", "Feedback deleted successfully!");
            return "redirect:/feedback/user/" + userId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting feedback: " + e.getMessage());
            return "redirect:/feedback/user/" + userId;
        }
    }

    /**
     * Handle admin status update
     * @param feedbackId the feedback ID
     * @param status the new status
     * @param adminResponse the admin response
     * @param redirectAttributes for flash messages
     * @return redirect to admin feedback management
     */
    @PostMapping("/admin/update-status")
    public String updateFeedbackStatus(@RequestParam Long feedbackId,
                                     @RequestParam Feedback.FeedbackStatus status,
                                     @RequestParam(required = false) String adminResponse,
                                     RedirectAttributes redirectAttributes) {
        try {
            feedbackService.updateFeedbackStatus(feedbackId, status, adminResponse);
            redirectAttributes.addFlashAttribute("success", "Feedback status updated successfully!");
            return "redirect:/feedback/admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating feedback status: " + e.getMessage());
            return "redirect:/feedback/admin";
        }
    }

    // ==================== REST API ENDPOINTS ====================

    /**
     * Get feedback statistics for admin dashboard
     * @return feedback statistics as JSON
     */
    /**
     * Debug endpoint to check feedback data
     */
    @GetMapping("/api/debug/feedback/{id}")
    @ResponseBody
    public ResponseEntity<Object> debugFeedback(@PathVariable Long id) {
        try {
            // Get all feedbacks to see what exists
            List<FeedbackDTO> allFeedbacks = feedbackService.getAllFeedbacks();
            return ResponseEntity.ok(Map.of(
                "requestedId", id,
                "totalFeedbacks", allFeedbacks.size(),
                "feedbacks", allFeedbacks.stream().map(f -> Map.of(
                    "id", f.getId(),
                    "subject", f.getSubject(),
                    "userId", f.getUserId(),
                    "userName", f.getUserName()
                )).collect(Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/admin/stats")
    @ResponseBody
    public ResponseEntity<Object> getFeedbackStatistics() {
        try {
            Object[] stats = feedbackService.getFeedbackStatistics();
            if (stats != null && stats.length >= 5) {
                return ResponseEntity.ok(Map.of(
                    "total", stats[0] != null ? stats[0] : 0,
                    "pending", stats[1] != null ? stats[1] : 0,
                    "inProgress", stats[2] != null ? stats[2] : 0,
                    "resolved", stats[3] != null ? stats[3] : 0,
                    "closed", stats[4] != null ? stats[4] : 0
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "total", 0,
                    "pending", 0,
                    "inProgress", 0,
                    "resolved", 0,
                    "closed", 0
                ));
            }
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error fetching feedback statistics: " + e.getMessage());
            return ResponseEntity.ok(Map.of(
                "total", 0,
                "pending", 0,
                "inProgress", 0,
                "resolved", 0,
                "closed", 0
            ));
        }
    }
}
