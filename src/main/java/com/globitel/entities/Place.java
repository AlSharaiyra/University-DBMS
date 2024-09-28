package com.globitel.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;
    @Column
    private String type;
    @Column
    private String name;
    @Column
    private Integer capacity;

//    @ManyToOne
//    @JoinColumn(name = "department_id")
//    private Department department;

    @OneToMany(mappedBy = "place")
    private List<Reservation> reservations;
}
