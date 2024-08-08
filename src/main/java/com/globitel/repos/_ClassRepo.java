package com.globitel.repos;

import com.globitel.entities.Course;
import com.globitel.entities.Department;
import com.globitel.entities.Instructor;
import com.globitel.entities._Class;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface _ClassRepo extends JpaRepository <_Class, Integer> {
    List<_Class> findByCourse(Course course);

}
