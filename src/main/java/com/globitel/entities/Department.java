package com.globitel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "department")
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

//    @OneToMany(mappedBy = "department")
//    private List<Place> rooms;

    // Parameterized Constructor


    public Department(Integer ID, String name, Integer planHours, Integer studentCount, Integer instructorCount, Integer courseCount, List<Student> students, List<Instructor> instructors, List<Course> courses) {
        this.ID = ID;
        this.name = name;
        this.planHours = planHours;
        this.studentCount = studentCount;
        this.instructorCount = instructorCount;
        this.courseCount = courseCount;
        this.students = students;
        this.instructors = instructors;
        this.courses = courses;
    }

    // No-arg Constructor, initializing studentCount & instructorCount & courseCount to 0
    public Department() {
        this.studentCount = 0;
        this.instructorCount = 0;
        this.courseCount = 0;
    }

    // Getters & Setters
    public Integer getInstructorCount() {
        return instructorCount;
    }

    public void setInstructorCount(Integer instructorCount) {
        this.instructorCount = instructorCount;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<Instructor> instructors) {
        this.instructors = instructors;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public Integer getPlanHours() {
        return planHours;
    }

    public void setPlanHours(Integer planHours) {
        this.planHours = planHours;
    }

    // equals() and hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(ID, that.ID) && Objects.equals(name, that.name) && Objects.equals(planHours, that.planHours) && Objects.equals(studentCount, that.studentCount) && Objects.equals(instructorCount, that.instructorCount) && Objects.equals(courseCount, that.courseCount) && Objects.equals(students, that.students) && Objects.equals(instructors, that.instructors) && Objects.equals(courses, that.courses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, name, planHours, studentCount, instructorCount, courseCount, students, instructors, courses);
    }

    @Override
    public String toString() {
        return "Department{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", planHours=" + planHours +
                ", studentCount=" + studentCount +
                ", instructorCount=" + instructorCount +
                ", courseCount=" + courseCount +
                ", students=" + students +
                ", instructors=" + instructors +
                ", courses=" + courses +
                '}';
    }
}
