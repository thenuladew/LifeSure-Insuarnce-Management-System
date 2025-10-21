package com.example.lifesureinsuarncemanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage() {
        return "index"; // maps to templates/home.html
    }

    @GetMapping("/aboutus")
    public String aboutUsPage() {
        return "aboutus";
    }

    @GetMapping("/servicesproduct")
    public String productPage() {
        return "Services";
    }


    }


