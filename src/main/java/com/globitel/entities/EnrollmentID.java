package com.globitel.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class EnrollmentID implements Serializable {
    @Column(name = "class_id")
    private Integer classID;

    @Column(name = "student_id")
    private Integer studentID;
}
