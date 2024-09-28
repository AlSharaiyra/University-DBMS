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
//@DiscriminatorValue("STUDENT")
public class Student {
    // Student Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column
    private String name;

    @Column(name = "academic Level")
    private Integer level;

    @Column
    private String email;
    @Column
    private String phone;
    @Column
    private String address;
    @Column
    private Integer currentCreditHours;
    @Column
    private Integer cumulativeCreditHours;
    @Column
    private Integer totalFailedHours;
    @Column
    private Double GPA;
    @Column
    private Double CGPA;

    // Student relationships
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;
}
