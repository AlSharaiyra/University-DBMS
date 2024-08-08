package com.globitel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "student")
public class Student {
    // Student Attributes
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column private String name;

    @Column(name = "academic Level")
    private Integer level;

    @Column private String email;
    @Column private String phone;
    @Column private String address;
    @Column private Integer totalCreditHours;

    // Student relationships
    @ManyToOne
//    @JsonIgnore
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(mappedBy = "students")
    private List<_Class> classes;

    @ManyToMany(mappedBy = "students")
    private List<Course> courses;

    // Parameterized Constructor
    public Student(Integer ID, String name, Integer level, String email, String phone, String address, Department department, List<_Class> classes, Integer totalCreditHours) {
        this.ID = ID;
        this.name = name;
        this.level = level;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.department = department;
        this.classes = classes;
        this.totalCreditHours = totalCreditHours;
    }

    // No-arg Constructor
    public Student() {
        this.totalCreditHours = 0;
    }

    // Getters & Setters
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Integer getTotalCreditHours() {
        return totalCreditHours;
    }

    public void setTotalCreditHours(Integer totalCreditHours) {
        this.totalCreditHours = totalCreditHours;
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(ID, student.ID) && Objects.equals(name, student.name) && Objects.equals(level, student.level) && Objects.equals(email, student.email) && Objects.equals(phone, student.phone) && Objects.equals(address, student.address) && Objects.equals(totalCreditHours, student.totalCreditHours) && Objects.equals(department, student.department) && Objects.equals(classes, student.classes);
    }

    // hashCode() method
    @Override
    public int hashCode() {
        return Objects.hash(ID, name, level, email, phone, address, totalCreditHours, department, classes);
    }

    // toString() method
    @Override
    public String toString() {
        return "Student{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", totalCreditHours=" + totalCreditHours +
                ", department=" + department +
                ", classes=" + classes +
                '}';
    }
}
