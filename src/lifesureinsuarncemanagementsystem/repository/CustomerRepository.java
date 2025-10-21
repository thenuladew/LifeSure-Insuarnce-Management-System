package com.example.lifesureinsuarncemanagementsystem.repository;

import com.example.lifesureinsuarncemanagementsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
