package com.globitel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.globitel.Semester;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "class")
public class _Class {
    // _Class Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;
    @Column
    private String days;
    @Column
    private String time;
    @Column(name = "hall/lab")
    private String hall_lab;
    @Column
    private Integer capacity;
    private Integer registered;
//    @Enumerated(EnumType.STRING)
//    @Column(name = "semester")
//    private Semester semester;
//    @Column
//    private String year;

    // _Class relationships
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

//    @ManyToMany
//    @JoinTable(
//            name = "class_instructor",
//            joinColumns = @JoinColumn(name = "class_id"),
//            inverseJoinColumns = @JoinColumn(name = "instructor_id")
//    )
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @ManyToMany
    @JoinTable(
            name = "class_student",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;

    // Parameterized Constructor
    public _Class(Integer ID, String days, String time, String hall_lab, Integer capacity, Integer registered, Course course, Instructor instructor, List<Student> students) {
        this.ID = ID;
        this.days = days;
        this.time = time;
        this.hall_lab = hall_lab;
        this.capacity = capacity;
        this.registered = registered;
        this.course = course;
        this.instructor = instructor;
        this.students = students;
    }


    // No-arg Constructor
    public _Class() {
        this.registered = 0;
    }

    // Getters & Setters
    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHall_lab() {
        return hall_lab;
    }

    public void setHall_lab(String hall_lab) {
        this.hall_lab = hall_lab;
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

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getRegistered() {
        return registered;
    }

    public void setRegistered(Integer registered) {
        this.registered = registered;
    }

    //    public Semester getSemester() {
//        return semester;
//    }
//
//    public void setSemester(Semester semester) {
//        this.semester = semester;
//    }

    // equals() method


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        _Class aClass = (_Class) o;
        return Objects.equals(ID, aClass.ID) && Objects.equals(days, aClass.days) && Objects.equals(time, aClass.time) && Objects.equals(hall_lab, aClass.hall_lab) && Objects.equals(capacity, aClass.capacity) && Objects.equals(registered, aClass.registered) && Objects.equals(course, aClass.course) && Objects.equals(instructor, aClass.instructor) && Objects.equals(students, aClass.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, days, time, hall_lab, capacity, registered, course, instructor, students);
    }

    // toString() method

    @Override
    public String toString() {
        return "_Class{" +
                "ID=" + ID +
                ", days='" + days + '\'' +
                ", time='" + time + '\'' +
                ", hall_lab='" + hall_lab + '\'' +
                ", capacity=" + capacity +
                ", registered=" + registered +
                ", course=" + course +
                ", instructor=" + instructor +
                ", students=" + students +
                '}';
    }
}
