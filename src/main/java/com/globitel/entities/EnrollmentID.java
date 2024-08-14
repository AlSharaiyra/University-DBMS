package com.globitel.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EnrollmentID implements Serializable {
    @Column(name = "class_id")
    private Integer classID;

    @Column(name = "student_id")
    private Integer studentID;

    public EnrollmentID() {
    }

    public EnrollmentID(Integer classID, Integer studentID) {
        this.classID = classID;
        this.studentID = studentID;
    }

    public Integer getClassID() {
        return classID;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public Integer getStudentID() {
        return studentID;
    }

    public void setStudentID(Integer studentID) {
        this.studentID = studentID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentID that = (EnrollmentID) o;
        return Objects.equals(classID, that.classID) && Objects.equals(studentID, that.studentID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(classID, studentID);
    }

    @Override
    public String toString() {
        return "ClassStudentID{" +
                "classID=" + classID +
                ", studentID=" + studentID +
                '}';
    }
}
