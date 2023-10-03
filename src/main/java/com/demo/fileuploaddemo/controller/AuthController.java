package com.demo.fileuploaddemo.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.fileuploaddemo.dto.AuthResponseDto;
import com.demo.fileuploaddemo.dto.LoginDto;
import com.demo.fileuploaddemo.dto.UserRegistrationDto;
import com.demo.fileuploaddemo.service.AuthService;
import com.demo.fileuploaddemo.util.BaseController;
import com.demo.fileuploaddemo.util.SuccessResponse;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {
	@Autowired
	private AuthService authService;

	@ApiOperation(value = "")
	@PostMapping("/login")
	public ResponseEntity<SuccessResponse<AuthResponseDto>> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
		String token = authService.login(loginDto);
		AuthResponseDto authResponse = new AuthResponseDto(token);
		return success(authResponse, "Token for user");
	}

	@ApiOperation(value = "")
	@PostMapping(value = { "/register", "/signup" })
	public ResponseEntity<SuccessResponse> register(@Valid @RequestBody UserRegistrationDto user) {
		return success(authService.registerUser(user), "User is registered");
	}
}
