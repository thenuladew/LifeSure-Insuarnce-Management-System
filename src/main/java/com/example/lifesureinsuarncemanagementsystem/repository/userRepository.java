package com.example.lifesureinsuarncemanagementsystem.repository;

import com.example.lifesureinsuarncemanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface userRepository extends JpaRepository<User, Integer> {
    User findByNationalID(String nationalID);
    User findByEmail(String email); // ✅ Added for email login
    Optional<User> findById(int id);
}