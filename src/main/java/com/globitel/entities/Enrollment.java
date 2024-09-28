package com.globitel.entities;

import com.globitel.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Enrollment {
    @EmbeddedId
    private EnrollmentID ID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnrollmentStatus enrollmentStatus;

    @Column(name = "grade")
    private Double grade;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "semester")
//    private Semester semester;
//    @Column
//    private String year;

    @ManyToOne
    @MapsId("studentID")
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @MapsId("classID")
    @JoinColumn(name = "class_id")
    private Class clazz;
}

