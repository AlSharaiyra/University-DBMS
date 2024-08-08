package com.globitel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "course")
public class Course {
    // Course Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column
    private String title;

    @Column
    private Integer creditHours;

    @Column
    private Integer noOfStudents;

    // Course relationships
    @ManyToOne
//    @JsonIgnore
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "course")
//    @JsonIgnore
    private List<_Class> classes;

    @ManyToMany
    @JoinTable(
            name = "course_student",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students;


    // Parameterized Constructor
    public Course(Integer ID, String title, Integer creditHours, Integer noOfStudents, Department department, List<_Class> classes) {
        this.ID = ID;
        this.title = title;
        this.creditHours = creditHours;
        this.noOfStudents = noOfStudents;
        this.department = department;
        this.classes = classes;
    }

    // No-arg Constructor
    public Course() {
        this.noOfStudents = 0;
    }

    // Getters & Setters
    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(Integer creditHours) {
        this.creditHours = creditHours;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<_Class> getClasses() {
        return classes;
    }

    public void setClasses(List<_Class> classes) {
        this.classes = classes;
    }

    public Integer getNoOfStudents() {
        return noOfStudents;
    }

    public void setNoOfStudents(Integer noOfStudents) {
        this.noOfStudents = noOfStudents;
    }

    // equals() method


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(ID, course.ID) && Objects.equals(title, course.title) && Objects.equals(creditHours, course.creditHours) && Objects.equals(noOfStudents, course.noOfStudents) && Objects.equals(department, course.department) && Objects.equals(classes, course.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, title, creditHours, noOfStudents, department, classes);
    }

    // toString() method

    @Override
    public String toString() {
        return "Course{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", creditHours=" + creditHours +
                ", noOfStudents=" + noOfStudents +
                ", department=" + department +
                ", classes=" + classes +
                '}';
    }
}
