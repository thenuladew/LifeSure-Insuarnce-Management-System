package com.example.lifesureinsuarncemanagementsystem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private int id;
    private String name;
    private String dob;
    private String nationalID;
    private String email;
    private String password;
    private String gender;
    private int age;
    private String profilePicture;
    private List<UserPhoneDTO> phones;



}