package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.PolicyDTO;
import com.example.lifesureinsuarncemanagementsystem.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/policies")
public class PolicyUserController {

    @Autowired
    private PolicyService policyService;

    /**
     * Show all policies for users (only active policies)
     */
    @GetMapping
    public String showAllPolicies(Model model) {
        List<PolicyDTO> policies = policyService.getActivePolicies();
        model.addAttribute("policies", policies);
        return "user/policies-list";
    }

    /**
     * Search policies
     */
    @GetMapping("/search")
    public String searchPolicies(@RequestParam String keyword, Model model) {
        List<PolicyDTO> policies = policyService.searchPolicies(keyword);
        // Filter only active policies for users
        policies = policies.stream()
                .filter(policy -> "ACTIVE".equals(policy.getStatus()))
                .toList();
        model.addAttribute("policies", policies);
        model.addAttribute("searchKeyword", keyword);
        return "user/policies-list";
    }

    /**
     * Filter policies by type
     */
    @GetMapping("/filter")
    public String filterPoliciesByType(@RequestParam String type, Model model) {
        List<PolicyDTO> policies = policyService.getPoliciesByType(type);
        // Filter only active policies for users
        policies = policies.stream()
                .filter(policy -> "ACTIVE".equals(policy.getStatus()))
                .toList();
        model.addAttribute("policies", policies);
        model.addAttribute("filterType", type);
        return "user/policies-list";
    }

    /**
     * View policy details
     */
    @GetMapping("/view/{id}")
    public String viewPolicyDetails(@PathVariable Long id, Model model) {
        PolicyDTO policy = policyService.getPolicyById(id);
        if (policy == null || !"ACTIVE".equals(policy.getStatus())) {
            return "redirect:/user/policies?error=Policy not found or not available";
        }
        model.addAttribute("policy", policy);
        return "user/policy-details";
    }

    // REST API endpoints for AJAX calls
    @GetMapping("/api")
    @ResponseBody
    public List<PolicyDTO> getActivePoliciesAPI() {
        return policyService.getActivePolicies();
    }

    @GetMapping("/api/search")
    @ResponseBody
    public List<PolicyDTO> searchPoliciesAPI(@RequestParam String keyword) {
        List<PolicyDTO> policies = policyService.searchPolicies(keyword);
        // Filter only active policies for users
        return policies.stream()
                .filter(policy -> "ACTIVE".equals(policy.getStatus()))
                .toList();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public PolicyDTO getPolicyByIdAPI(@PathVariable Long id) {
        PolicyDTO policy = policyService.getPolicyById(id);
        if (policy != null && "ACTIVE".equals(policy.getStatus())) {
            return policy;
        }
        return null;
    }
}
