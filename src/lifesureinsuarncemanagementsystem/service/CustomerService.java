package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.entity.Customer;
import com.example.lifesureinsuarncemanagementsystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
