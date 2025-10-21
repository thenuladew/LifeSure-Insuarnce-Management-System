package com.example.lifesureinsuarncemanagementsystem.service.strategy;


import org.springframework.stereotype.Component;

@Component("strongPasswordStrategy")
public class StrongPasswordStrategy implements PasswordValidationStrategy {
    @Override
    public boolean isValid(String password) {
        int length = (password != null) ? password.length() : 0;
        System.out.println("Validating password Using Strong Password Strategy: User Entered password lenght is :" + length);

        if (password == null || password.length() < 8) return false;
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasDigit = password.matches(".*\\d.*");
        return hasUpper && hasDigit;
    }

    @Override
    public String getErrorMessage() {
        return "Password must be at least 8 characters, include an uppercase letter and a number.";
    }
}


