package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.PolicyDTO;
import com.example.lifesureinsuarncemanagementsystem.model.Admin;
import com.example.lifesureinsuarncemanagementsystem.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.ConstraintViolationException;
import java.util.List;

@Controller
@RequestMapping("/admin/policies")
public class PolicyAdminController {

    @Autowired
    private PolicyService policyService;

    /**
     * Check if admin is logged in
     */
    private boolean isAdminLoggedIn(HttpSession session) {
        return session.getAttribute("loggedInAdmin") != null;
    }

    /**
     * Show policy management page for admin
     */
    @GetMapping
    public String showPolicyManagement(HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login?error=Please login as admin first";
        }
        
        try {
            List<PolicyDTO> policies = policyService.getAllPolicies();
            model.addAttribute("policies", policies);
            return "admin/policy-management";
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error loading policies: " + e.getMessage());
            e.printStackTrace();
            
            // Return empty list and show error message
            model.addAttribute("policies", new java.util.ArrayList<>());
            model.addAttribute("error", "Error loading policies: " + e.getMessage());
            return "admin/policy-management";
        }
    }

    /**
     * Show add policy form
     */
    @GetMapping("/new")
    public String showAddPolicyForm(HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login?error=Please login as admin first";
        }
        
        model.addAttribute("policy", new PolicyDTO());
        return "admin/policy-form";
    }

    /**
     * Show edit policy form
     */
    @GetMapping("/edit/{id}")
    public String showEditPolicyForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login?error=Please login as admin first";
        }
        
        PolicyDTO policy = policyService.getPolicyById(id);
        if (policy == null) {
            return "redirect:/admin/policies?error=Policy not found";
        }
        model.addAttribute("policy", policy);
        return "admin/policy-form";
    }

    /**
     * Create new policy
     */
    @PostMapping
    public String createPolicy(@RequestParam String policyName,
                              @RequestParam String policyType,
                              @RequestParam String price,
                              @RequestParam String coverageAmount,
                              @RequestParam String description,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Please login as admin first");
            return "redirect:/admin/login";
        }
        
        try {
            System.out.println("Creating policy with type: " + policyType);
            
            // Validate input lengths
            if (policyName == null || policyName.trim().length() < 3 || policyName.trim().length() > 100) {
                redirectAttributes.addFlashAttribute("error", "Policy name must be between 3 and 100 characters");
                return "redirect:/admin/policies/new";
            }
            
            if (policyType == null || policyType.trim().length() < 2 || policyType.trim().length() > 50) {
                redirectAttributes.addFlashAttribute("error", "Policy type must be between 2 and 50 characters");
                return "redirect:/admin/policies/new";
            }
            
            if (description == null || description.trim().length() < 10 || description.trim().length() > 500) {
                redirectAttributes.addFlashAttribute("error", "Description must be between 10 and 500 characters");
                return "redirect:/admin/policies/new";
            }
            
            PolicyDTO policyDTO = new PolicyDTO();
            policyDTO.setPolicyName(policyName.trim());
            policyDTO.setPolicyType(policyType.trim());
            policyDTO.setPrice(new java.math.BigDecimal(price.replace(",", "")));
            policyDTO.setCoverageAmount(new java.math.BigDecimal(coverageAmount.replace(",", "")));
            policyDTO.setDescription(description.trim());
            policyDTO.setStatus("ACTIVE");
            
            System.out.println("Policy DTO created: " + policyDTO);
            
            PolicyDTO createdPolicy = policyService.createPolicy(policyDTO);
            System.out.println("Policy created successfully with ID: " + createdPolicy.getId());
            
            redirectAttributes.addFlashAttribute("success", "Policy created successfully");
            return "redirect:/admin/policies";
        } catch (ConstraintViolationException e) {
            System.err.println("Validation error creating policy: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Validation error: Please check all fields");
            return "redirect:/admin/policies/new";
        } catch (Exception e) {
            System.err.println("Error creating policy: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to create policy. Please try again.");
            return "redirect:/admin/policies/new";
        }
    }

    /**
     * Update policy
     */
    @PostMapping("/update")
    public String updatePolicy(@RequestParam Long id,
                              @RequestParam String policyName,
                              @RequestParam String policyType,
                              @RequestParam String price,
                              @RequestParam String coverageAmount,
                              @RequestParam String description,
                              @RequestParam String status,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Please login as admin first");
            return "redirect:/admin/login";
        }
        
        try {
            // Validate input lengths
            if (policyName == null || policyName.trim().length() < 3 || policyName.trim().length() > 100) {
                redirectAttributes.addFlashAttribute("error", "Policy name must be between 3 and 100 characters");
                return "redirect:/admin/policies/edit/" + id;
            }
            
            if (policyType == null || policyType.trim().length() < 2 || policyType.trim().length() > 50) {
                redirectAttributes.addFlashAttribute("error", "Policy type must be between 2 and 50 characters");
                return "redirect:/admin/policies/edit/" + id;
            }
            
            if (description == null || description.trim().length() < 10 || description.trim().length() > 500) {
                redirectAttributes.addFlashAttribute("error", "Description must be between 10 and 500 characters");
                return "redirect:/admin/policies/edit/" + id;
            }
            
            PolicyDTO policyDTO = new PolicyDTO();
            policyDTO.setId(id);
            policyDTO.setPolicyName(policyName.trim());
            policyDTO.setPolicyType(policyType.trim());
            policyDTO.setPrice(new java.math.BigDecimal(price.replace(",", "")));
            policyDTO.setCoverageAmount(new java.math.BigDecimal(coverageAmount.replace(",", "")));
            policyDTO.setDescription(description.trim());
            policyDTO.setStatus(status);
            
            policyService.updatePolicy(id, policyDTO);
            redirectAttributes.addFlashAttribute("success", "Policy updated successfully");
            return "redirect:/admin/policies";
        } catch (ConstraintViolationException e) {
            System.err.println("Validation error updating policy: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Validation error: Please check all fields");
            return "redirect:/admin/policies/edit/" + id;
        } catch (Exception e) {
            System.err.println("Error updating policy: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update policy. Please try again.");
            return "redirect:/admin/policies/edit/" + id;
        }
    }

    /**
     * Delete policy
     */
    @PostMapping("/delete/{id}")
    public String deletePolicy(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Please login as admin first");
            return "redirect:/admin/login";
        }
        
        try {
            boolean deleted = policyService.deletePolicy(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Policy deleted successfully");
                return "redirect:/admin/policies";
            } else {
                redirectAttributes.addFlashAttribute("error", "Policy not found");
                return "redirect:/admin/policies";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete policy");
            return "redirect:/admin/policies";
        }
    }

    /**
     * Search policies
     */
    @GetMapping("/search")
    public String searchPolicies(@RequestParam String keyword, HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login?error=Please login as admin first";
        }
        
        List<PolicyDTO> policies = policyService.searchPolicies(keyword);
        model.addAttribute("policies", policies);
        model.addAttribute("searchKeyword", keyword);
        return "admin/policy-management";
    }

    /**
     * Filter policies by status
     */
    @GetMapping("/filter")
    public String filterPolicies(@RequestParam String status, HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login?error=Please login as admin first";
        }
        
        List<PolicyDTO> policies = policyService.getPoliciesByStatus(status);
        model.addAttribute("policies", policies);
        model.addAttribute("filterStatus", status);
        return "admin/policy-management";
    }

    // REST API endpoints for AJAX calls
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<PolicyDTO>> getAllPoliciesAPI(HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(policyService.getAllPolicies());
        } catch (Exception e) {
            System.err.println("API Error loading policies: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Test endpoint to check if PolicyService is working
    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<String> testPolicyService(HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return ResponseEntity.status(401).body("Not logged in as admin");
        }
        try {
            List<PolicyDTO> policies = policyService.getAllPolicies();
            return ResponseEntity.ok("PolicyService is working! Found " + policies.size() + " policies.");
        } catch (Exception e) {
            System.err.println("Test Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<PolicyDTO> createPolicyAPI(@Valid @RequestBody PolicyDTO policyDTO, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }
        try {
            PolicyDTO createdPolicy = policyService.createPolicy(policyDTO);
            return ResponseEntity.ok(createdPolicy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<PolicyDTO> updatePolicyAPI(@PathVariable Long id, @Valid @RequestBody PolicyDTO policyDTO, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }
        try {
            PolicyDTO updatedPolicy = policyService.updatePolicy(id, policyDTO);
            return ResponseEntity.ok(updatedPolicy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> deletePolicyAPI(@PathVariable Long id, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return ResponseEntity.status(401).build();
        }
        try {
            System.out.println("Attempting to delete policy with ID: " + id);
            boolean deleted = policyService.deletePolicy(id);
            System.out.println("Delete result: " + deleted);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            System.err.println("Error in deletePolicyAPI: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

}
