package com.example.lifesureinsuarncemanagementsystem.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.lifesureinsuarncemanagementsystem.model.BranchUser;
import com.example.lifesureinsuarncemanagementsystem.repository.BranchUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private BranchUserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        BranchUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
        return new User(user.getUsername(), user.getPassword(), Collections.singleton(authority));
    }
}