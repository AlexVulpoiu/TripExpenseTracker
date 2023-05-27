package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String email;
}
