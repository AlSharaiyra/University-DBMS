package com.globitel.controllers;

import com.globitel.entities.Department;
import com.globitel.entities.Student;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private StudentRepo studentRepo;
    @Autowired
    private InstructorRepo instructorRepo;
    @Autowired
    private CourseRepo courseRepo;

    public record DepartmentRecord(
            Integer department_id,
            String name,
            Integer student_count,
            Integer instructor_count,
            Integer course_count
    ) {
    }

    // Get All Departments
    @GetMapping("")
    public List<DepartmentRecord> getALLDepartments() {
        return departmentRepo.findAll().stream()
                .map(department -> new DepartmentRecord(
                        department.getID()
                        , department.getName()
                        , department.getStudentCount()
                        , department.getInstructorCount()
                        , department.getCourseCount()))
                .collect(Collectors.toList());
    }

    // Get a single Department given ID
    @GetMapping("/{id}")
    public DepartmentRecord getOneDepartment(@PathVariable Integer id) {
        return departmentRepo.findById(id)
                .map(department -> new DepartmentRecord(
                        department.getID()
                        , department.getName()
                        , department.getStudentCount()
                        , department.getInstructorCount()
                        , department.getCourseCount()))
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    public record newDepartment(
            String name
    ) {
    }

    // Add a new Department
    @PostMapping("")
    public ResponseEntity<String> addDepartment(@RequestBody newDepartment request) {
        Department newDep = new Department();
        newDep.setName(request.name);
        departmentRepo.save(newDep);

        return ResponseEntity.status(HttpStatus.CREATED).body("Department added successfully");
    }

    // Update existing Department given ID
    @PutMapping("/{id}")
    public ResponseEntity<String> editDepartment(@RequestBody newDepartment request, @PathVariable Integer id) {
        Department toEdit = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        if (request.name != null && !request.name.isEmpty()) toEdit.setName(request.name);
        departmentRepo.save(toEdit);

        return ResponseEntity.ok("Department updated successfully");
    }

    // Delete existing Department given ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Integer id) {
        Department toDelete = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        departmentRepo.delete(toDelete);

        return ResponseEntity.ok("Department deleted successfully");
    }

    public record StudentRecord(
            Integer student_id,
            String name,
            Integer level,
            String email,
            String phone,
            String address,
            String department,
            Integer total_credit_hours
    ) {
    }

    // Get All Students in a Department given ID
    @GetMapping("/{id}/students")
    public List<StudentRecord> getStudents(@PathVariable Integer id) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        return studentRepo.findByDepartment(department).stream()
                .map(student -> new StudentRecord(
                        student.getID()
                        , student.getName()
                        , student.getLevel()
                        , student.getEmail()
                        , student.getPhone()
                        , student.getAddress()
                        , student.getDepartment().getName()
                        , student.getTotalCreditHours()))
                .collect(Collectors.toList());
    }

    public record InstructorRecord(
            Integer instructor_id,
            String name,
            String email,
            String phone,
            String address,
            String department
    ) {
    }

    // Get All Instructors in a Department given ID
    @GetMapping("/{id}/instructors")
    public List<InstructorRecord> getInstructors(@PathVariable Integer id) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        return instructorRepo.findByDepartment(department).stream()
                .map(instructor -> new InstructorRecord(
                        instructor.getID()
                        , instructor.getName()
                        , instructor.getEmail()
                        , instructor.getPhone()
                        , instructor.getAddress()
                        , instructor.getDepartment().getName()))
                .collect(Collectors.toList());

    }

    public record CourseRecord(
            Integer course_id,
            String title,
            Integer credit_hours,
            String department
    ) {
    }

    // Get All Courses in a Department given ID
    @GetMapping("/{id}/courses")
    public List<CourseRecord> getCourses(@PathVariable Integer id) {
        Department department = departmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        return courseRepo.findByDepartment(department).stream()
                .map(course -> new CourseRecord(
                        course.getID()
                        , course.getTitle()
                        , course.getCreditHours()
                        , course.getDepartment().getName()))
                .collect(Collectors.toList());
    }
}
