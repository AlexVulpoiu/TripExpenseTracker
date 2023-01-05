package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    @NotBlank
    private String username;

    @NotBlank
    private String email;
}
