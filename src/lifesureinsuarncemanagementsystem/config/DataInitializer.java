package com.example.lifesureinsuarncemanagementsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.lifesureinsuarncemanagementsystem.model.Admin;
import com.example.lifesureinsuarncemanagementsystem.model.BranchUser;
import com.example.lifesureinsuarncemanagementsystem.model.Role;
import com.example.lifesureinsuarncemanagementsystem.model.BranchStatus;
import com.example.lifesureinsuarncemanagementsystem.repository.AdminRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.BranchUserRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.BranchRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(BranchUserRepository userRepo, BranchRepository branchRepository,
                          AdminRepository adminRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                BranchUser admin = new BranchUser();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin"));
                admin.setEmail("admin@lifesure.local");
                admin.setRole(Role.HEAD_MANAGER);
                userRepo.save(admin);
            }

            // Create default admin for agent management
            if (adminRepository.count() == 0) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                adminRepository.save(admin);
                System.out.println("✅ Default admin created: username='admin', password='admin123'");
            }

            branchRepository.findAll().stream()
                .filter(branch -> branch.getStatus() == null)
                .forEach(branch -> {
                    branch.setStatus(BranchStatus.ACTIVE);
                    branchRepository.save(branch);
                });
        };
    }
}
