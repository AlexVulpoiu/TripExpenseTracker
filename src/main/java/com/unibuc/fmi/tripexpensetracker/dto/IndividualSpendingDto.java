package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualSpendingDto {

    @NotBlank
    private String tripName;

    @NotBlank
    private String type;

    @NotNull
    private Double amount;

    @NotNull
    private Double amountDue;

    private IndividualSpendingDto(IndividualSpendingDtoBuilder builder) {
        this.tripName = builder.tripName;
        this.type = builder.type;
        this.amount = builder.amount;
        this.amountDue = builder.amountDue;
    }

    public static class IndividualSpendingDtoBuilder {
        @NotBlank
        private String tripName;

        @NotBlank
        private String type;

        @NotNull
        private Double amount;

        @NotNull
        private Double amountDue;

        public IndividualSpendingDtoBuilder tripName(String tripName) {
            this.tripName = tripName;
            return this;
        }

        public IndividualSpendingDtoBuilder type(String type) {
            this.type = type;
            return this;
        }

        public IndividualSpendingDtoBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public IndividualSpendingDtoBuilder amountDue(Double amountDue) {
            this.amountDue = amountDue;
            return this;
        }

        public IndividualSpendingDto build() {
            return new IndividualSpendingDto(this);
        }
    }
}
