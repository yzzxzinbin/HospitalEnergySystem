package com.hospital.energymgmt.service;

import com.hospital.energymgmt.model.Role;
import com.hospital.energymgmt.model.User;
import com.hospital.energymgmt.repository.RoleRepository;
import com.hospital.energymgmt.repository.UserRepository;
import com.hospital.energymgmt.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService { // 实现 UserDetailsService

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository; // 注入 RoleRepository

    // Method to convert User entity to UserDto
    private UserDto convertToDto(User user) {
        if (user == null) {
            return null; // 或者根据需要抛出异常
        }
        return new UserDto(user);
    }

    // Method to convert UserDto to User entity
    private User convertToEntity(UserDto userDto) {
        if (userDto == null) {
            return null; // 或者根据需要抛出异常
        }
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        // 处理角色转换
        if (userDto.getRole() != null && !userDto.getRole().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            // 假设 UserDto 中的 role 是一个以逗号分隔的角色名称字符串
            String[] roleNames = userDto.getRole().split(",");
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName.trim())
                        .orElseGet(() -> {
                            Role newRole = new Role(roleName.trim());
                            // 如果希望在找不到角色时创建新角色，则需要保存它
                            // return roleRepository.save(newRole); 
                            // 或者，如果角色必须预先存在，则抛出异常或记录错误
                            // throw new RuntimeException("Role not found: " + roleName.trim());
                            return newRole; // 如果角色不存在，则创建一个新的 Role 对象（未持久化）
                                           // 或者根据业务逻辑决定是否持久化新角色
                        });
                roles.add(role);
            }
            user.setRoles(roles);
        }
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
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Long id, UserDto userDto) { // Changed parameter and return type to UserDto
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        // Update fields from DTO
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        // Handle password update carefully - only if provided and usually re-encoded
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            // Add password encoding logic if necessary
            user.setPassword(userDto.getPassword());
        }
        // Handle role update
        if (userDto.getRole() != null) {
            Set<Role> roles = new HashSet<>();
            String[] roleNames = userDto.getRole().split(",");
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName.trim())
                        .orElseGet(() -> {
                            Role newRole = new Role(roleName.trim());
                            // Decide if new roles should be created and saved
                            // return roleRepository.save(newRole);
                            return newRole; // Or throw exception if role must exist
                        });
                roles.add(role);
            }
            user.setRoles(roles);
        }
        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // 实现 UserDetailsService 接口的方法
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>()); // Customize authorities based on roles
    }
}