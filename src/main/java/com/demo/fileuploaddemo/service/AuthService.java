package com.demo.fileuploaddemo.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.demo.fileuploaddemo.dto.LoginDto;
import com.demo.fileuploaddemo.dto.UserRegistrationDto;
import com.demo.fileuploaddemo.entity.Role;
import com.demo.fileuploaddemo.entity.User;
import com.demo.fileuploaddemo.exception.InvalidLoginException;
import com.demo.fileuploaddemo.exception.RecordAlreadyExistException;
import com.demo.fileuploaddemo.repository.RoleRepository;
import com.demo.fileuploaddemo.repository.UserRepository;
import com.demo.fileuploaddemo.security.JwtTokenProvider;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public String login(LoginDto loginDto) throws InvalidLoginException {
    	validateLoginDto(loginDto);
    	String token = "";
    	try {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        token = jwtTokenProvider.generateToken(authentication);
    	} catch(Exception e) {
    		throw new InvalidLoginException("Invalid login credential, please try again"); 
    	}
    	return token;
    }

    public User registerUser(UserRegistrationDto userDto) {
        boolean isUserExist = userRepository.findByEmail(userDto.getEmail()).isPresent();

        if(isUserExist){
            throw new RecordAlreadyExistException("User already exist");
        }

        Role userRole = roleRepository.findByName(userDto.getRole().toUpperCase()).orElseThrow(()->new RuntimeException("Invalid role"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setName(userDto.getName());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User getUserByUserName(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(()->new RuntimeException("User not found"));
    }
    
    private void validateLoginDto(LoginDto loginDto) throws InvalidLoginException {
    	if(StringUtils.isEmpty(loginDto.getEmail()) || StringUtils.isEmpty(loginDto.getPassword())) {
    		throw new InvalidLoginException("Invalid login credential, please try again");
    	}
    }
}
