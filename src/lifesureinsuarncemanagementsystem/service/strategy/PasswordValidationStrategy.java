package com.example.lifesureinsuarncemanagementsystem.service.strategy;

public interface PasswordValidationStrategy {


    boolean isValid(String password);
    String getErrorMessage();
}




