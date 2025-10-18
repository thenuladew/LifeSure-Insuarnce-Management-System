package com.example.lifesureinsuarncemanagementsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lifesureinsuarncemanagementsystem.model.BranchUser;

public interface BranchUserRepository extends JpaRepository<BranchUser, Long> {
    Optional<BranchUser> findByUsername(String username);

}
