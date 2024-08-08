package com.globitel.repos;

import com.globitel.entities.Course;
import com.globitel.entities.Department;
import com.globitel.entities.Instructor;
import com.globitel.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepo extends JpaRepository<Course, Integer> {
    List<Course> findByDepartment(Department department);
//    List<Course> findByInstructor(Instructor instructor);


}
