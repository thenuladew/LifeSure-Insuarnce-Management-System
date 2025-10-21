package com.example.lifesureinsuarncemanagementsystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserPhoneDTO {
    private int id;
    private String phoneNumber;
}