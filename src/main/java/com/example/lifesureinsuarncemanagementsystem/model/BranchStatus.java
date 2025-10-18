package com.example.lifesureinsuarncemanagementsystem.model;

public enum BranchStatus {
    ACTIVE,
    INACTIVE;

    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
