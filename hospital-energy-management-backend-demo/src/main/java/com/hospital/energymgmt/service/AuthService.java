package com.hospital.energymgmt.service;

import com.hospital.energymgmt.dto.LoginRequestDto; // 修改为 LoginRequestDto
import com.hospital.energymgmt.dto.LoginResponseDto;
import com.hospital.energymgmt.model.User;
import com.hospital.energymgmt.repository.UserRepository;
import com.hospital.energymgmt.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService; // 确保 UserService 已注入

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) throws Exception { // 修改参数类型为 LoginRequestDto
        authenticate(loginRequestDto.getUsername(), loginRequestDto.getPassword());

        final UserDetails userDetails = userService.loadUserByUsername(loginRequestDto.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        // User user = userRepository.findByUsername(loginRequestDto.getUsername()); // 可以直接使用 userDetails.getUsername()
        return new LoginResponseDto(token, userDetails.getUsername()); 
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}