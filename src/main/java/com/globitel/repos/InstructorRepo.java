package com.globitel.repos;

import com.globitel.entities.Department;
import com.globitel.entities.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstructorRepo extends JpaRepository<Instructor, Integer> {
    //    List<Instructor> findByDepartmentName(String department);
    List<Instructor> findByDepartment(Department department);
    Optional<Instructor> findByEmail(String email);
    Optional<Instructor> findByPhone(String phone);

}
