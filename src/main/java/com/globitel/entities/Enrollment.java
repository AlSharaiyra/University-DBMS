package com.globitel.entities;

import com.globitel.Status;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "enrollment")
public class Enrollment {
    @EmbeddedId
    private EnrollmentID ID;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

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


    public Enrollment() {
    }

    public Enrollment(EnrollmentID ID, Student student, Class clazz, Status status, Double grade) {
        this.ID = ID;
        this.student = student;
        this.clazz = clazz;
        this.status = status;
        this.grade = grade;
    }

    public EnrollmentID getID() {
        return ID;
    }

    public void setID(EnrollmentID ID) {
        this.ID = ID;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(ID, that.ID) && Objects.equals(student, that.student) && Objects.equals(clazz, that.clazz) && status == that.status && Objects.equals(grade, that.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, student, clazz, status, grade);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "ID=" + ID +
                ", student=" + student +
                ", clazz=" + clazz +
                ", status=" + status +
                ", grade=" + grade +
                '}';
    }
}

