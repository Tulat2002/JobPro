package com.devanktus.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message = "Username khong duoc de trong")
    private String username;
    @NotBlank(message = "Password khong duoc de trong")
    private String password;

}
