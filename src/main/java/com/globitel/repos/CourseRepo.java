package com.globitel.repos;

import com.globitel.entities.Course;
import com.globitel.entities.Department;
import com.globitel.entities.Instructor;
import com.globitel.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends JpaRepository<Course, Integer> {
    List<Course> findByDepartment(Department department);
//    List<Course> findByInstructor(Instructor instructor);


}
