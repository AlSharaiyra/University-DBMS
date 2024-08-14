package com.globitel.repos;

import com.globitel.entities.*;
import com.globitel.entities.Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepo extends JpaRepository
        <Enrollment, EnrollmentID>{
    boolean existsByStudentAndClazz(Student student, Class clazz);
    boolean existsByStudentAndClazzCourse(Student student, Course course);
    List<Enrollment> findByStudent(Student student);
    List<Enrollment> findByClazz(Class clazz);


}
