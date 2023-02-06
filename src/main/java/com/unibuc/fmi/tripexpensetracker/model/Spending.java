package com.unibuc.fmi.tripexpensetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "spendings")
public class Spending {

    public static final String TYPE_INDIVIDUAL = "individual";
    public static final String TYPE_GROUP = "group";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer amount;

    @NotBlank
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserTrip userTrip;

    @JsonIgnore
    @OneToMany(mappedBy = "spending", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SpendingGroup> participants;
}
