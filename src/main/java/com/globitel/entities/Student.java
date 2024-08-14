package com.globitel.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "student")
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

    // Student relationships
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;

    // Parameterized Constructor
    public Student(Integer ID, String name, Integer level, String email, String phone, String address, Integer currentCreditHours, Integer cumulativeCreditHours, Department department, List<Enrollment> enrollments) {
        this.ID = ID;
        this.name = name;
        this.level = level;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.currentCreditHours = currentCreditHours;
        this.cumulativeCreditHours = cumulativeCreditHours;
        this.department = department;
        this.enrollments = enrollments;
    }

    // No-arg Constructor
    public Student() {
        this.currentCreditHours = 0;
        this.cumulativeCreditHours = 0;
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

    public Integer getCurrentCreditHours() {
        return currentCreditHours;
    }

    public void setCurrentCreditHours(Integer currentCreditHours) {
        this.currentCreditHours = currentCreditHours;
    }

    public Integer getCumulativeCreditHours() {
        return cumulativeCreditHours;
    }

    public void setCumulativeCreditHours(Integer cumulativeCreditHours) {
        this.cumulativeCreditHours = cumulativeCreditHours;
    }

    // equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(ID, student.ID) && Objects.equals(name, student.name) && Objects.equals(level, student.level) && Objects.equals(email, student.email) && Objects.equals(phone, student.phone) && Objects.equals(address, student.address) && Objects.equals(currentCreditHours, student.currentCreditHours) && Objects.equals(cumulativeCreditHours, student.cumulativeCreditHours) && Objects.equals(department, student.department) && Objects.equals(enrollments, student.enrollments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, name, level, email, phone, address, currentCreditHours, cumulativeCreditHours, department, enrollments);
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
                ", currentCreditHours=" + currentCreditHours +
                ", cumulativeCreditHours=" + cumulativeCreditHours +
                ", department=" + department +
                ", classStudents=" + enrollments +
                '}';
    }
}
