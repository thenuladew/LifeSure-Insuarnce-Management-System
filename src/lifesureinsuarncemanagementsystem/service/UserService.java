package com.example.lifesureinsuarncemanagementsystem.service;

import com.example.lifesureinsuarncemanagementsystem.dtos.UserDTO;
import com.example.lifesureinsuarncemanagementsystem.dtos.UserPhoneDTO;
import com.example.lifesureinsuarncemanagementsystem.model.User;
import com.example.lifesureinsuarncemanagementsystem.model.UserPhone;
import com.example.lifesureinsuarncemanagementsystem.repository.userRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private userRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private FeedbackService feedbackService;


    public UserDTO createUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        if (userDTO.getPhones() != null) {
            List<UserPhone> phones = userDTO.getPhones().stream()
                    .map(phoneDTO -> {
                        UserPhone up = new UserPhone();
                        up.setPhoneNumber(phoneDTO.getPhoneNumber());
                        up.setUser(user);
                        return up;
                    })
                    .collect(Collectors.toList());
            user.setPhones(phones);
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }


    public List<UserDTO> readAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        List<UserPhoneDTO> phoneDTOs = user.getPhones().stream()
                .map(phone -> {
                    UserPhoneDTO upDTO = new UserPhoneDTO();
                    upDTO.setId(phone.getId());
                    upDTO.setPhoneNumber(phone.getPhoneNumber());
                    return upDTO;
                })
                .collect(Collectors.toList());
        dto.setPhones(phoneDTOs);
        return dto;
    }

    public UserDTO updateUser(UserDTO userDTO) {
        Optional<User> existingUserOpt = userRepository.findById(userDTO.getId());
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userDTO.getId());
        }

        User existingUser = existingUserOpt.get();

        existingUser.setName(userDTO.getName());
        existingUser.setDob(userDTO.getDob());
        existingUser.setNationalID(userDTO.getNationalID());
        existingUser.setEmail(userDTO.getEmail());

        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            existingUser.setPassword(userDTO.getPassword());
        }

        existingUser.setGender(userDTO.getGender());
        existingUser.setAge(userDTO.getAge());
        existingUser.setProfilePicture(userDTO.getProfilePicture());

        existingUser.getPhones().clear();

        if (userDTO.getPhones() != null) {
            List<UserPhone> newPhones = userDTO.getPhones().stream()
                    .map(phoneDTO -> {
                        UserPhone up = new UserPhone();
                        up.setPhoneNumber(phoneDTO.getPhoneNumber());
                        up.setUser(existingUser);
                        return up;
                    })
                    .collect(Collectors.toList());
            existingUser.getPhones().addAll(newPhones);
        }

        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    public Boolean deleteUserById(int id) {
        if (userRepository.existsById(id)) {
            try {
                // First, delete all feedback associated with this user
                // This prevents foreign key constraint violations
                feedbackService.deleteAllFeedbackByUser((long) id);
                
                // Then delete the user
                userRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                System.err.println("Error deleting user: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    // ✅ UPDATED: Authenticate by EMAIL
    public UserDTO authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (!user.getPassword().equals(password)) {
            return null;
        }
        return convertToDTO(user);
    }

    public void updateProfilePicture(int userId, String profilePicturePath) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setProfilePicture(profilePicturePath);
            userRepository.save(user);
        }
    }

    public UserDTO getUserById(int id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            return convertToDTO(userOpt.get());
        }
        return null;
    }
}