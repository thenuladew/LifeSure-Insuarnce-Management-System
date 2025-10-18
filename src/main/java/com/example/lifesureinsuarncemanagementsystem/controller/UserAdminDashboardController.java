package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserAdminDashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/UserAdminDashboard")
    public String showUserManagementPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // Check if admin is logged in
        Object admin = session.getAttribute("loggedInAdmin");
        if (admin == null) {
            redirectAttributes.addFlashAttribute("error", "👑 Please login as admin to access this page.");
            return "redirect:/admin/login";
        }

        // Get all users for admin dashboard
        List<UserDTO> users = userService.readAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("user", new UserDTO());

        return "UserAdminDashboard";
    }
}