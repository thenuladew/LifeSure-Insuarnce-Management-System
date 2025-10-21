package com.example.lifesureinsuarncemanagementsystem;

import com.example.lifesureinsuarncemanagementsystem.service.AdminService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class LifeSureInsuarnceManagementSystemApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(LifeSureInsuarnceManagementSystemApplication.class);

    @Autowired
    private AdminService adminService;

    public static void main(String[] args) {
        SpringApplication.run(LifeSureInsuarnceManagementSystemApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... args) {
        log.info("SecureLife:http://localhost:8080");

        adminService.createDefaultAdmin();
    }
}