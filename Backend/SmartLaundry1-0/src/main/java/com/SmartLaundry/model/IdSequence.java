package com.SmartLaundry.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "id_sequence")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdSequence {

    @Id
    @Column(name = "table_key", unique = true, nullable = false)
    private String tableKey;

    @Column(name = "next_val", nullable = false)
    private Long nextVal;

}
