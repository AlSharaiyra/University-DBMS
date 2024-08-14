package com.globitel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "instructor")
public class Instructor {
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
//    @JsonIgnore
    private Department department;

    @OneToMany(mappedBy = "instructor")
//    @JsonIgnore
    private List<Class> classes;

    // Parameterized Constructor
    public Instructor(Integer ID, String name, String email, String phone, String address, Department department, List<Class> classes) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.department = department;
        this.classes = classes;
    }

    // No-arg Constructor
    public Instructor() {
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

    public List<Class> getClasses() {
        return classes;
    }

    public void setClasses(List<Class> classes) {
        this.classes = classes;
    }

    // equals() method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        return Objects.equals(ID, that.ID) && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(phone, that.phone) && Objects.equals(address, that.address) && Objects.equals(department, that.department) && Objects.equals(classes, that.classes);
    }

    // hashCode() method
    @Override
    public int hashCode() {
        return Objects.hash(ID, name, email, phone, address, department, classes);
    }

    // toString() method
    @Override
    public String toString() {
        return "Instructor{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", department=" + department +
                ", classes=" + classes +
                '}';
    }
}
