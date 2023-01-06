package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndividualSpendingRequestDto {

    @NotNull
    private Integer amount;

    @NotBlank
    private String type;
}
