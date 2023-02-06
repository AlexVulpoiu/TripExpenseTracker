package com.unibuc.fmi.tripexpensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSpendingDto {

    @NotBlank
    private List<String> users;

    @NotBlank
    private String type;

    @NotNull
    private Double amount;

    @NotNull
    private Double amountDue;

    private TripSpendingDto(TripSpendingDtoBuilder builder) {
        this.users = builder.users;
        this.type = builder.type;
        this.amount = builder.amount;
        this.amountDue = builder.amountDue;
    }

    public static class TripSpendingDtoBuilder {
        @NotBlank
        private List<String> users;

        @NotBlank
        private String type;

        @NotNull
        private Double amount;

        @NotNull
        private Double amountDue;

        public TripSpendingDtoBuilder users(List<String> users) {
            this.users = users;
            return this;
        }

        public TripSpendingDtoBuilder type(String type) {
            this.type = type;
            return this;
        }

        public TripSpendingDtoBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public TripSpendingDtoBuilder amountDue(Double amountDue) {
            this.amountDue = amountDue;
            return this;
        }

        public TripSpendingDto build() {
            return new TripSpendingDto(this);
        }
    }
}
