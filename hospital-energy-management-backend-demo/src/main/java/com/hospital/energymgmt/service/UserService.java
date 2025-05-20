package com.hospital.energymgmt.service;

import com.hospital.energymgmt.model.User;
import com.hospital.energymgmt.repository.UserRepository;
import com.hospital.energymgmt.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Method to convert User entity to UserDto
    private UserDto convertToDto(User user) {
        if (user == null) return null;
        return new UserDto(user);
    }

    // Method to convert UserDto to User entity
    private User convertToEntity(UserDto userDto) {
        if (userDto == null) return null;
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // Assuming password needs to be set from DTO
        // Role conversion needs to be handled carefully based on how roles are managed.
        // For simplicity, if UserDto.role is a String, you might need to fetch Role entity by name.
        // This part is highly dependent on your Role entity and how you want to manage roles.
        // Example: if (userDto.getRole() != null) { /* fetch/set Role entities */ }
        return user;
    }

    public UserDto createUser(UserDto userDto) { // Changed parameter and return type to UserDto
        User user = convertToEntity(userDto);
        // Password encoding should happen here before saving if not handled elsewhere
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto getUserById(Long id) { // Changed return type to UserDto
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(this::convertToDto).orElse(null); // Or throw exception
    }

    public List<UserDto> getAllUsers() { // Changed return type to List<UserDto>
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    public UserDto updateUser(Long id, UserDto userDto) { // Changed parameter and return type to UserDto
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        // Update fields from DTO
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        // Handle password update carefully - only if provided and usually re-encoded
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            // user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        // Handle role update
        // if (userDto.getRole() != null) { /* update roles */ }
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}