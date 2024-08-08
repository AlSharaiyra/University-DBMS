package com.globitel.repos;

import com.globitel.entities.Department;
import com.globitel.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, Integer> {
    //    List<Student> findByDepartmentName(String department);
    List<Student> findByDepartment(Department department);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByPhone(String phone);

}
