package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        UserDTO user = userService.authenticate(username, password);

        if (user != null) {
            session.setAttribute("loggedInUser", user);
            return "redirect:/user/dashboard";
        } else {
            model.addAttribute("error", "Invalid Email or Password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ✅ Add these endpoints for client-side session checking
    @GetMapping("/checkAdminSession")
    @ResponseBody
    public Map<String, Boolean> checkAdminSession(HttpSession session) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("isAdmin", session.getAttribute("loggedInAdmin") != null);
        return response;
    }

    @GetMapping("/checkUserSession")
    @ResponseBody
    public Map<String, Boolean> checkUserSession(HttpSession session) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("isUser", session.getAttribute("loggedInUser") != null);
        return response;
    }
}