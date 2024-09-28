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
public class Department {
    // Department attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column
    private String name;

    @Column
    private Integer planHours;

    @Column(name = "students count")
    private Integer studentCount;

    @Column(name = "instructors count")
    private Integer instructorCount;

    @Column(name = "courses count")
    private Integer courseCount;

    // Department relationships
    @OneToMany(mappedBy = "department")
    private List<Student> students;

    @OneToMany(mappedBy = "department")
    private List<Instructor> instructors;

    @OneToMany(mappedBy = "department")
    private List<Course> courses;

}
