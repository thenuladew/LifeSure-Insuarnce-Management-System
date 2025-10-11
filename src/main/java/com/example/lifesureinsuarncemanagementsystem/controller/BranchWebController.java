package com.example.lifesureinsuarncemanagementsystem.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.lifesureinsuarncemanagementsystem.dtos.BranchPerformanceForm;

import com.example.lifesureinsuarncemanagementsystem.entity.Branch;
import com.example.lifesureinsuarncemanagementsystem.entity.BranchStatus;
import com.example.lifesureinsuarncemanagementsystem.service.BranchService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/branches")
public class BranchWebController {

    @Autowired
    private BranchService branchServices;

    @GetMapping
    public String listBranches(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) BranchStatus status,
                               Model model) {
        model.addAttribute("branches", branchServices.getBranches(keyword, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        return "branches/list";
    }

    @GetMapping("/dashboard")
    public String viewPerformanceDashboard(Model model, @ModelAttribute("performanceForm") BranchPerformanceForm performanceForm) {
        populatePerformanceDashboard(model);
        return "branches/dashboard";
    }

    @GetMapping("/new")
    public String showNewBranchForm(Model model) {
        Branch branch = new Branch();
        branch.setStatus(BranchStatus.ACTIVE);
        model.addAttribute("branch", branch);
        return "branches/form";
    }

    @PostMapping
    public String createBranch(@Valid @ModelAttribute Branch branch, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("branch", branch);
            return "branches/form";
        }
        branchServices.createBranch(branch);
        return "redirect:/branches";
    }

    @PutMapping
    public String updateBranch(@Valid @ModelAttribute Branch branch, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("branch", branch);
            return "branches/form";
        }
        branchServices.updateBranch(branch.getId(), branch);
        return "redirect:/branches";
    }

    @GetMapping("/edit/{id}")
    public String showEditBranchForm(@PathVariable Long id, Model model) {
        model.addAttribute("branch", branchServices.getBranchById(id));
        return "branches/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBranch(@PathVariable Long id) {
        branchServices.deleteBranch(id);
        return "redirect:/branches";
    }

    @PostMapping("/dashboard/{id}/performance")
    public String updateBranchPerformance(@PathVariable Long id,
            @Valid @ModelAttribute("performanceForm") BranchPerformanceForm performanceForm,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            populatePerformanceDashboard(model);
            model.addAttribute("errorBranchId", id);
            model.addAttribute("performanceErrorMessage", "Please provide valid values (0 or positive, up to 2 decimals).");
            model.addAttribute("performanceForm", performanceForm);
            return "branches/dashboard";
        }

        branchServices.updateBranchPerformance(id, performanceForm.getTargetAmount(), performanceForm.getAchievedAmount());

        redirectAttributes.addFlashAttribute("successMessage", "Branch performance updated successfully.");
        return "redirect:/branches/dashboard";
    }

    @ModelAttribute("performanceForm")
    public BranchPerformanceForm performanceForm() {
        return new BranchPerformanceForm();
    }

    @ModelAttribute("statuses")
    public BranchStatus[] statuses() {
        return BranchStatus.values();
    }

    private void populatePerformanceDashboard(Model model) {
        List<Branch> branches = branchServices.getAllBranches();
        model.addAttribute("branches", branches);

        BigDecimal totalTarget = branches.stream()
                .map(Branch::getTargetAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAchieved = branches.stream()
                .map(Branch::getAchievedAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overallVariance = totalAchieved.subtract(totalTarget);
        BigDecimal achievementRate = totalTarget.compareTo(BigDecimal.ZERO) > 0
                ? totalAchieved.divide(totalTarget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        Map<Long, Integer> progressMap = branches.stream()
                .collect(Collectors.toMap(Branch::getId,
                        branch -> branch.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
                                ? branch.getAchievedAmount()
                                        .multiply(BigDecimal.valueOf(100))
                                        .divide(branch.getTargetAmount(), 0, RoundingMode.HALF_UP).intValue()
                                : 0));

        model.addAttribute("totalTarget", totalTarget);
        model.addAttribute("totalAchieved", totalAchieved);
        model.addAttribute("overallVariance", overallVariance);
        model.addAttribute("achievementRate", achievementRate.setScale(1, RoundingMode.HALF_UP));
        model.addAttribute("progressMap", progressMap);
    }
}