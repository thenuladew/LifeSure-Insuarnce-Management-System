package com.example.lifesureinsuarncemanagementsystem.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.lifesureinsuarncemanagementsystem.entity.Branch;
import com.example.lifesureinsuarncemanagementsystem.entity.BranchStatus;
import com.example.lifesureinsuarncemanagementsystem.repository.BranchRepository;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public Branch createBranch(Branch branch){
        if (branch.getStatus() == null) {
            branch.setStatus(BranchStatus.ACTIVE);
        }
        return branchRepository.save(branch);
    }

    public List<Branch> getAllBranches(){
        return branchRepository.findAll();
    }

    public List<Branch> getBranches(String keyword, BranchStatus status) {
        String normalizedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return branchRepository.searchBranches(normalizedKeyword, status);
    }

    public List<Branch> getBranchesByStatus(BranchStatus status) {
        if (status == null) {
            return getAllBranches();
        }
        return branchRepository.findByStatus(status);
    }

    public Branch getBranchById(Long id){
        return branchRepository.findById(id).orElseThrow(() -> new RuntimeException("Branch Not Found!"));
    }

    public Branch updateBranch(Long id, Branch branchDetails){
        Branch branch = getBranchById(id);
        branch.setbCode(branchDetails.getbCode());
        branch.setbName(branchDetails.getbName());
        branch.setbAddress(branchDetails.getbAddress());
        branch.setbPhone(branchDetails.getbPhone());
        branch.setManagerName(branchDetails.getManagerName());
        branch.setEmail(branchDetails.getEmail());
        branch.setTargetAmount(branchDetails.getTargetAmount());
        branch.setAchievedAmount(branchDetails.getAchievedAmount());
        branch.setStatus(branchDetails.getStatus());
        return branchRepository.save(branch);

    }

    public void deleteBranch(Long id){
        branchRepository.deleteById(id);
    }

    public Branch updateBranchPerformance(Long id, BigDecimal targetAmount, BigDecimal achievedAmount) {
        Branch branch = getBranchById(id);
        branch.setTargetAmount(targetAmount);
        branch.setAchievedAmount(achievedAmount);
        return branchRepository.save(branch);
    }

}
