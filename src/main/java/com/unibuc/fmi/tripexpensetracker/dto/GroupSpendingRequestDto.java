package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupSpendingRequestDto {

    @NotNull
    private Integer amount;

    @NotNull
    private List<Long> users;

}
