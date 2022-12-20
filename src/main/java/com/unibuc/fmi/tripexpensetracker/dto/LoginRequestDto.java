package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
@Getter
@Setter
@Builder
public class LoginRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
