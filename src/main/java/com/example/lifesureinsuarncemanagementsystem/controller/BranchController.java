package com.example.lifesureinsuarncemanagementsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lifesureinsuarncemanagementsystem.dtos.BranchPerformanceForm;
import com.example.lifesureinsuarncemanagementsystem.entity.Branch;
import com.example.lifesureinsuarncemanagementsystem.entity.BranchStatus;
import com.example.lifesureinsuarncemanagementsystem.service.BranchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    @Autowired
    private BranchService branchServices;

    @PostMapping
    public Branch createBranch(@RequestBody Branch branch){
        return branchServices.createBranch(branch);
    }

    @GetMapping
    public List<Branch> getBranches(@RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) BranchStatus status) {
        if ((keyword == null || keyword.isBlank()) && status == null) {
            return branchServices.getAllBranches();
        }
        return branchServices.getBranches(keyword, status);
    }

    @GetMapping("/{id}")
    public Branch getBranchById(@PathVariable Long id){
        return branchServices.getBranchById(id);
    }

    @PutMapping("/{id}")
    public Branch updateBranch(@PathVariable Long id, @RequestBody Branch branch){
        return branchServices.updateBranch(id, branch);
    }

    @PutMapping("/{id}/performance")
    public Branch updateBranchPerformance(@PathVariable Long id, @RequestBody @Valid BranchPerformanceForm branchPerformanceForm) {
        return branchServices.updateBranchPerformance(id, branchPerformanceForm.getTargetAmount(), branchPerformanceForm.getAchievedAmount());
    }

    @DeleteMapping("/{id}")
    public void deleteBranch(@PathVariable Long id){
        branchServices.deleteBranch(id);
    }


}
