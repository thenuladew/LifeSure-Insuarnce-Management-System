package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.dtos.PolicyDTO;
import com.example.lifesureinsuarncemanagementsystem.model.Policy;
import com.example.lifesureinsuarncemanagementsystem.repository.PolicyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Initialize the service - this will trigger table creation
     */
    @PostConstruct
    public void init() {
        try {
            // This will trigger Hibernate to create the table if it doesn't exist
            policyRepository.count();
            System.out.println("PolicyService initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing PolicyService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create a new policy
     */
    public PolicyDTO createPolicy(PolicyDTO policyDTO) {
        Policy policy = new Policy();
        
        // Manually set fields instead of using ModelMapper to avoid type conversion issues
        policy.setPolicyName(policyDTO.getPolicyName());
        policy.setPolicyType(policyDTO.getPolicyType());
        policy.setPrice(policyDTO.getPrice());
        policy.setCoverageAmount(policyDTO.getCoverageAmount());
        policy.setDescription(policyDTO.getDescription());
        
        // Handle status conversion from String to Enum
        if (policyDTO.getStatus() != null && !policyDTO.getStatus().isEmpty()) {
            try {
                policy.setStatus(Policy.PolicyStatus.valueOf(policyDTO.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Default to ACTIVE if invalid status provided
                policy.setStatus(Policy.PolicyStatus.ACTIVE);
            }
        } else {
            // Default status
            policy.setStatus(Policy.PolicyStatus.ACTIVE);
        }
        
        Policy savedPolicy = policyRepository.save(policy);
        return convertToDTO(savedPolicy);
    }

    /**
     * Get all policies
     */
    public List<PolicyDTO> getAllPolicies() {
        try {
            return policyRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting all policies: " + e.getMessage());
            e.printStackTrace();
            // Return empty list instead of throwing exception
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get all active policies (for users)
     */
    public List<PolicyDTO> getActivePolicies() {
        return policyRepository.findActivePolicies().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get policy by ID
     */
    public PolicyDTO getPolicyById(Long id) {
        Optional<Policy> policyOpt = policyRepository.findById(id);
        if (policyOpt.isPresent()) {
            return convertToDTO(policyOpt.get());
        }
        return null;
    }

    /**
     * Update policy
     */
    public PolicyDTO updatePolicy(Long id, PolicyDTO policyDTO) {
        Optional<Policy> existingPolicyOpt = policyRepository.findById(id);
        if (existingPolicyOpt.isEmpty()) {
            throw new RuntimeException("Policy not found with ID: " + id);
        }

        Policy existingPolicy = existingPolicyOpt.get();
        existingPolicy.setPolicyName(policyDTO.getPolicyName());
        existingPolicy.setPolicyType(policyDTO.getPolicyType());
        existingPolicy.setPrice(policyDTO.getPrice());
        existingPolicy.setCoverageAmount(policyDTO.getCoverageAmount());
        existingPolicy.setDescription(policyDTO.getDescription());
        existingPolicy.setStatus(Policy.PolicyStatus.valueOf(policyDTO.getStatus()));

        Policy updatedPolicy = policyRepository.save(existingPolicy);
        return convertToDTO(updatedPolicy);
    }

    /**
     * Delete policy
     */
    public boolean deletePolicy(Long id) {
        try {
            if (policyRepository.existsById(id)) {
                policyRepository.deleteById(id);
                System.out.println("Policy with ID " + id + " deleted successfully");
                return true;
            } else {
                System.out.println("Policy with ID " + id + " not found");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting policy with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Search policies by keyword
     */
    public List<PolicyDTO> searchPolicies(String keyword) {
        return policyRepository.searchPolicies(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get policies by type
     */
    public List<PolicyDTO> getPoliciesByType(String policyType) {
        return policyRepository.findByPolicyType(policyType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get policies by status
     */
    public List<PolicyDTO> getPoliciesByStatus(String status) {
        Policy.PolicyStatus policyStatus = Policy.PolicyStatus.valueOf(status.toUpperCase());
        return policyRepository.findByStatus(policyStatus).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Policy entity to PolicyDTO
     */
    private PolicyDTO convertToDTO(Policy policy) {
        PolicyDTO dto = modelMapper.map(policy, PolicyDTO.class);
        dto.setStatus(policy.getStatus().name());
        return dto;
    }
}
