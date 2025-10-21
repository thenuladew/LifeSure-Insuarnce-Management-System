package com.example.lifesureinsuarncemanagementsystem.service.strategy;

import org.springframework.stereotype.Component;

@Component("basicPasswordStrategy")
public class BasicPasswordStrategy implements PasswordValidationStrategy {
    @Override
    public boolean isValid(String password) {

        int length = (password != null) ? password.length() : 0;
        System.out.println("Validating password Using Basic Password Strategy: User Entered password lenght is :" + length);

        return password != null && password.length() >= 6;
    }

    @Override
    public String getErrorMessage() {
        return "Password must be at least 6 characters long.";
    }
}



