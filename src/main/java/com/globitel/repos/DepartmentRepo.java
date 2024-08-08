package com.globitel.repos;

import com.globitel.entities.Department;
import com.globitel.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface DepartmentRepo extends JpaRepository <Department, Integer>{
}
