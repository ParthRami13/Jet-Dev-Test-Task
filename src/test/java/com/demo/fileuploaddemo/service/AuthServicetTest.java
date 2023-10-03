package com.demo.fileuploaddemo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.demo.fileuploaddemo.dto.LoginDto;
import com.demo.fileuploaddemo.dto.UserRegistrationDto;
import com.demo.fileuploaddemo.entity.Role;
import com.demo.fileuploaddemo.entity.User;
import com.demo.fileuploaddemo.exception.InvalidLoginException;
import com.demo.fileuploaddemo.exception.RecordAlreadyExistException;
import com.demo.fileuploaddemo.repository.RoleRepository;
import com.demo.fileuploaddemo.repository.UserRepository;

@SpringBootTest
public class AuthServicetTest {
	
	@Autowired
	ApplicationContext applicationContext;
	
	@MockBean
	AuthenticationManager authenticationManager;
	
	@MockBean
	UserRepository userRepository;
	
	@MockBean
	RoleRepository roleRepository;
	
	@Autowired
	private AuthService authService;
	
	
	@Test
	public void testLoginSuccess() {
		LoginDto loginDto = new LoginDto();
		loginDto.setEmail("test@gmail.com");
		loginDto.setPassword("password");
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_Adnin"));
    
		Authentication authToken = new UsernamePasswordAuthenticationToken(
            loginDto.getEmail(),
            loginDto.getPassword(),
            authorities);
		
		when(authenticationManager.authenticate(any())).thenReturn(authToken);
		
		String token = authService.login(loginDto);
		
		assertNotNull(token);
	}
	
	@Test
	public void testInvalidLogin() {
		LoginDto loginDto = new LoginDto();
		loginDto.setEmail("test@gmail.com");
		loginDto.setPassword("wrong_password");
		
		
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));
		
		assertThrows(InvalidLoginException.class, () -> authService.login(loginDto));
		
	}
	
	@Test
	public void testRegisterUserSuccess() {
		UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
		userRegistrationDto.setEmail("parth@gmail.com");
		userRegistrationDto.setName("Parth");
		userRegistrationDto.setPassword("Parth4Test!");
		userRegistrationDto.setRole("ROLE_Admin");
		
		Role userRole = new Role();
		userRole.setName("ROLE_Admin");
		
		User expectedUser = new User();
		expectedUser.setId(1L);
		expectedUser.setEmail(userRegistrationDto.getEmail());
		expectedUser.setName(userRegistrationDto.getName());
		
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		when(roleRepository.findByName(anyString())).thenReturn(Optional.of(userRole));
		
		when(userRepository.save(any(User.class))).thenReturn(expectedUser);
		
		User actualUser = authService.registerUser(userRegistrationDto);
		
		assertEquals(expectedUser.getId(), actualUser.getId());
	}
	
	@Test
	public void testRegisterUserInvalidRole() {
		UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
		userRegistrationDto.setEmail("parth@gmail.com");
		userRegistrationDto.setName("Parth");
		userRegistrationDto.setPassword("Parth4Test!");
		userRegistrationDto.setRole("ROLE_Admin");
		
		Role userRole = new Role();
		userRole.setName("ROLE_Admin");
		
		User expectedUser = new User();
		expectedUser.setId(1L);
		expectedUser.setEmail(userRegistrationDto.getEmail());
		expectedUser.setName(userRegistrationDto.getName());
		
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		
		when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
		
		assertThrows(RuntimeException.class, () -> authService.registerUser(userRegistrationDto));
	}
	
	@Test
	public void testRegisterUserExist() {
		UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
		userRegistrationDto.setEmail("parth@gmail.com");
		userRegistrationDto.setName("Parth");
		userRegistrationDto.setPassword("Parth4Test!");
		userRegistrationDto.setRole("ROLE_Admin");
		
		Role userRole = new Role();
		userRole.setName("ROLE_Admin");
		
		User expectedUser = new User();
		expectedUser.setId(1L);
		expectedUser.setEmail(userRegistrationDto.getEmail());
		expectedUser.setName(userRegistrationDto.getName());
		
		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(expectedUser));
		
		assertThrows(RecordAlreadyExistException.class, () -> authService.registerUser(userRegistrationDto));
	}
	

}
