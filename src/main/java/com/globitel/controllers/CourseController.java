package com.globitel.controllers;

import com.globitel.entities.*;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.ClassRepo;
import com.globitel.repos.CourseRepo;
import com.globitel.repos.DepartmentRepo;

import com.globitel.entities.Class;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.globitel.controllers.ClassController.ClassRecord;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private DepartmentRepo departmentRepo;
    @Autowired
    private ClassRepo classRepo;


    public record CourseRecord(
            Integer course_id,
            String title,
            Integer credit_hours,
            Integer registered,
            String department
    ) {
    }

    // Get All Courses
    @GetMapping("")
    public List<CourseRecord> getAllCourses() {
        return courseRepo.findAll().stream()
                .map(course -> new CourseRecord(
                        course.getID()
                        , course.getTitle()
                        , course.getCreditHours()
                        , course.getNoOfStudents()
                        , course.getDepartment().getName()))
                .collect(Collectors.toList());
    }

    // Get a single Course given ID
    @GetMapping("/{id}")
    public CourseRecord getOneCourse(@PathVariable Integer id) {
        return courseRepo.findById(id).map(course -> new CourseRecord(
                        course.getID()
                        , course.getTitle()
                        , course.getCreditHours()
                        , course.getNoOfStudents()
                        , course.getDepartment().getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
    }

    public record newCourse(
            String title,
            Integer credit_hours,
            Integer department_id
    ) {
    }

    @Transactional
    public void saveCourse(Department department, Course course){
        try {
            departmentRepo.save(department);
            courseRepo.save(course);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to save course", e);
        }
    }

    // Add a new Course
    @PostMapping("")
    public ResponseEntity<String> addCourse(@RequestBody newCourse request) {
        Course course = new Course();
        Department department = departmentRepo.findById(request.department_id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.department_id));

        department.setCourseCount(department.getCourseCount() + 1);

        course.setTitle(request.title);
        course.setDepartment(department);
        course.setCreditHours(request.credit_hours);

//        departmentRepo.save(department);
//        courseRepo.save(course);

        saveCourse(department, course);
        return ResponseEntity.status(HttpStatus.CREATED).body("Course added successfully");
    }

    public record editedCourse(
            String title,
            Integer credit_hours
    ) {
    }

    // Update existing Course given ID
    @PutMapping("/{id}")
    public ResponseEntity<String> editCourse(@RequestBody editedCourse request, @PathVariable Integer id) {
        Course toEdit = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        if (request.title != null && !request.title.isEmpty()) toEdit.setTitle(request.title);
        if (request.credit_hours != null) toEdit.setCreditHours(request.credit_hours);
//        if (request.department != null) {
//            Department department = departmentRepo.findById(request.department)
//                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with name: " + request.department));
//            toEdit.setDepartment(department);
//        }
        courseRepo.save(toEdit);

        return ResponseEntity.ok("Course updated successfully");
    }

    @Transactional
    public void deleteCourse(Department department, Course course){
        try {
            courseRepo.delete(course);
            departmentRepo.save(department);
        }
        catch (Exception e){
            throw new RuntimeException("Failed to delete course", e);
        }
    }


    // Delete existing Course given ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Integer id) {
        Course toDelete = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        Department department = toDelete.getDepartment();
        department.setCourseCount(department.getCourseCount() - 1);

//        courseRepo.delete(toDelete);
//        departmentRepo.save(department);

        deleteCourse(department, toDelete);

        return ResponseEntity.ok("Course deleted successfully");
    }


    // Get all Classes of a Course given ID
    @GetMapping("/{id}/classes")
    public List<ClassRecord> getClasses(@PathVariable Integer id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        return classRepo.findByCourse(course).stream()
                .map(clazz -> new ClassRecord(
                        clazz.getID(),
                        clazz.getReservation().getPlace().getName(),
                        clazz.getReservation().getPlace().getType(),
                        clazz.getReservation().getDays(),
                        clazz.getReservation().getTime(),
                        clazz.getReservation().getPlace().getCapacity(),
                        clazz.getRegistered(),
                        clazz.getCourse().getTitle(),
                        clazz.getCourse().getDepartment().getName()))
                .collect(Collectors.toList());
    }

}
