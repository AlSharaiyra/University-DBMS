package com.globitel.repos;

import com.globitel.entities.Class;
import com.globitel.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepo extends JpaRepository <Class, Integer> {
    List<Class> findByCourse(Course course);

}
