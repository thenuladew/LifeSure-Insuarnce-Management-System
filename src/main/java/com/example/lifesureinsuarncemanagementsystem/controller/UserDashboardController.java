package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserDashboardController {

    private final UserService userService;

    public UserDashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/dashboard")
    public String showDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if user is logged in
        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to access your dashboard.");
            return "redirect:/login";
        }

        // Get fresh user data from database
        UserDTO freshUser = userService.getUserById(user.getId());
        if (freshUser != null) {
            session.setAttribute("loggedInUser", freshUser);
            model.addAttribute("user", freshUser);
        } else {
            model.addAttribute("user", user);
        }

        return "user-dashboard";
    }

    @GetMapping("/user/feedback")
    public String showFeedback(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if admin is logged in
        Object admin = session.getAttribute("loggedInAdmin");
        if (admin != null) {
            redirectAttributes.addFlashAttribute("error", "⛔ Admins cannot access user pages. Redirecting to admin dashboard.");
            return "redirect:/UserAdminDashboard";
        }

        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to access this page.");
            return "redirect:/login";
        }
        
        // Redirect to the new feedback system
        return "redirect:/feedback/user/" + user.getId();
    }

    @GetMapping("/user/payment")
    public String showPayment(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if admin is logged in
        Object admin = session.getAttribute("loggedInAdmin");
        if (admin != null) {
            redirectAttributes.addFlashAttribute("error", "⛔ Admins cannot access user pages. Redirecting to admin dashboard.");
            return "redirect:/UserAdminDashboard";
        }

        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to access this page.");
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user-payment";
    }

    @GetMapping("/user/claims")
    public String showClaims(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if admin is logged in
        Object admin = session.getAttribute("loggedInAdmin");
        if (admin != null) {
            redirectAttributes.addFlashAttribute("error", "⛔ Admins cannot access user pages. Redirecting to admin dashboard.");
            return "redirect:/UserAdminDashboard";
        }

        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "🔒 Please login to access this page.");
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user-claims";
    }

    @PostMapping("/user/refreshSession")
    public ResponseEntity<UserDTO> refreshSession(@RequestBody UserDTO userDTO, HttpSession session) {
        if (userDTO == null || userDTO.getId() == 0) {
            return ResponseEntity.badRequest().build();
        }

        UserDTO freshUser = userService.getUserById(userDTO.getId());
        UserDTO sessionUser = freshUser != null ? freshUser : userDTO;
        session.setAttribute("loggedInUser", sessionUser);
        return ResponseEntity.ok(sessionUser);
    }
}