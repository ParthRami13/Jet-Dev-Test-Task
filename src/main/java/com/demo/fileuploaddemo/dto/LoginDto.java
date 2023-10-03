package com.demo.fileuploaddemo.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
public class LoginDto {
    @Email
    @NotEmpty(message = "Email should not be null or empty")
    private String email;

    @NotEmpty(message = "Password should not be null or empty")
    private String password;
}
