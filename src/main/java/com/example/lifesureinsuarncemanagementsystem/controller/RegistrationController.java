package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register"; // templates/register.html
    }

    @PostMapping
    public String registerUser(UserDTO userDTO, Model model) {
        try {
            // Validate required fields
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                model.addAttribute("error", "Email is required");
                return "register";
            }

            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                model.addAttribute("error", "Password is required");
                return "register";
            }

            // Create user
            userService.createUser(userDTO);

            model.addAttribute("success", "Registration successful! Please login.");
            model.addAttribute("userDTO", new UserDTO()); // Clear form
            return "register";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}