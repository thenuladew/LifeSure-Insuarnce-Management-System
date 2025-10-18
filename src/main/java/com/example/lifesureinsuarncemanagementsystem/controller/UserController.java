package com.example.lifesureinsuarncemanagementsystem.controller;

import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/CreateUser")
    public UserDTO createuser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @GetMapping("/ReadUsers")
    public List<UserDTO> getUser() {
        return userService.readAllUsers();
    }

    @PutMapping("/UpdateUser")
    public UserDTO updateuser(@RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @DeleteMapping("/DeleteUser/{id}")
    public Boolean deleteuser(@PathVariable int id) {
        return userService.deleteUserById(id);
    }



}