package com.unibuc.fmi.tripexpensetracker.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(	name = "spending_groups")
public class SpendingGroup {
    @EmbeddedId
    private SpendingGroupId id;

    @MapsId("userId")
    @ManyToOne(fetch= FetchType.LAZY)
    private User user;

    @MapsId("spendingId")
    @ManyToOne(fetch= FetchType.LAZY)
    private Spending spending;
}
