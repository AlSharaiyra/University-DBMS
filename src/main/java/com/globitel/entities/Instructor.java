package com.globitel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@DiscriminatorValue("INSTRUCTOR")
public class Instructor{
    // Instructor Attributes
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column private String name;
    @Column private String email;
    @Column private String phone;
    @Column private String address;

    // Instructor relationships
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "instructor")
    private List<Class> classes;
}
