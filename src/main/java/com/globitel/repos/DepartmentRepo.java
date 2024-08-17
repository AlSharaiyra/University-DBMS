package com.globitel.repos;

import com.globitel.entities.Department;
import com.globitel.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface DepartmentRepo extends JpaRepository <Department, Integer>{
}
