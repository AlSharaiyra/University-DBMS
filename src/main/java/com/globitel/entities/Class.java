package com.globitel.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "class")
public class Class {
    // Class Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;
    @Column
    private Integer registered;
//    @Column
//    private Integer reservation_id;


    // Class relationships
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "reservation_id", referencedColumnName = "ID")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    // Parameterized Constructor
    public Class(Integer ID, Integer registered, Reservation reservation, Course course, List<Enrollment> enrollments, Instructor instructor) {
        this.ID = ID;
        this.registered = registered;
        this.reservation = reservation;
        this.course = course;
        this.enrollments = enrollments;
        this.instructor = instructor;
    }

    // No-arg Constructor
    public Class() {
        this.registered = 0;
    }

    // Getters & Setters
    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Integer getRegistered() {
        return registered;
    }

    public void setRegistered(Integer registered) {
        this.registered = registered;
    }

    public List<Enrollment> getClassStudents() {
        return enrollments;
    }

    public void setClassStudents(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return Objects.equals(ID, aClass.ID) && Objects.equals(registered, aClass.registered) && Objects.equals(reservation, aClass.reservation) && Objects.equals(course, aClass.course) && Objects.equals(enrollments, aClass.enrollments) && Objects.equals(instructor, aClass.instructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, registered, reservation, course, enrollments, instructor);
    }

    // toString() method
    @Override
    public String toString() {
        return "Class{" +
                "ID=" + ID +
                ", registered=" + registered +
                ", reservation=" + reservation +
                ", course=" + course +
                ", classStudents=" + enrollments +
                ", instructor=" + instructor +
                '}';
    }
}
