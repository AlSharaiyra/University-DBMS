package com.globitel.repos;

import com.globitel.Day;
import com.globitel.entities.Class;
import com.globitel.entities.Department;
import com.globitel.entities.Enrollment;
import com.globitel.entities.Instructor;
import com.globitel.entities.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface InstructorRepo extends JpaRepository<Instructor, Integer> {
    //    List<Instructor> findByDepartmentName(String department);
    List<Instructor> findByDepartment(Department department);
    Optional<Instructor> findByEmail(String email);
    Optional<Instructor> findByPhone(String phone);

}
