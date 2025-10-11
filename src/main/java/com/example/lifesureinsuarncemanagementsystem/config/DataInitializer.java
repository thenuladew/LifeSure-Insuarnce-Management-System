package com.example.lifesureinsuarncemanagementsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.lifesureinsuarncemanagementsystem.entity.BranchUser;
import com.example.lifesureinsuarncemanagementsystem.entity.Role;
import com.example.lifesureinsuarncemanagementsystem.entity.BranchStatus;
import com.example.lifesureinsuarncemanagementsystem.repository.BranchUserRepository;
import com.example.lifesureinsuarncemanagementsystem.repository.BranchRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(BranchUserRepository userRepo, BranchRepository branchRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                BranchUser admin = new BranchUser();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin"));
                admin.setEmail("admin@lifesure.local");
                admin.setRole(Role.HEAD_MANAGER);
                userRepo.save(admin);
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
