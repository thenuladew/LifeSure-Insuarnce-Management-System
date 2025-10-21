package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.PolicyDTO;
import com.example.lifesureinsuarncemanagementsystem.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<PolicyDTO>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PolicyDTO>> getActivePolicies() {
        return ResponseEntity.ok(policyService.getActivePolicies());
    }
}
