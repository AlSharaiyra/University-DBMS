package com.globitel.controllers;

import com.globitel.entities.*;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.CourseRepo;
import com.globitel.repos.DepartmentRepo;
import com.globitel.repos._ClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globitel.controllers.DepartmentController.CourseRecord;

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
    private _ClassRepo _classRepo;

    // Get All Courses
    @GetMapping("")
    public List<CourseRecord> getAllCourses() {
        return courseRepo.findAll().stream()
                .map(course -> new CourseRecord(
                        course.getID()
                        , course.getTitle()
                        , course.getCreditHours(),
                        course.getDepartment().getName()))
                .collect(Collectors.toList());
    }

    // Get a single Course given ID
    @GetMapping("/{id}")
    public CourseRecord getOneCourse(@PathVariable Integer id) {
        return courseRepo.findById(id).map(course -> new CourseRecord(
                        course.getID()
                        , course.getTitle()
                        , course.getCreditHours(),
                        course.getDepartment().getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
    }

    public record newCourse(
            String title,
            Integer creditHours,
            Integer department_id
    ) {
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
        course.setCreditHours(request.creditHours);

        departmentRepo.save(department);
        courseRepo.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).body("Course added successfully");
    }

    public record editedCourse(
            String title,
            Integer creditHours
    ) {
    }

    // Update existing Course given ID
    @PutMapping("/{id}")
    public ResponseEntity<String> editCourse(@RequestBody editedCourse request, @PathVariable Integer id) {
        Course toEdit = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        if (request.title != null && !request.title.isEmpty()) toEdit.setTitle(request.title);
        if (request.creditHours != null) toEdit.setCreditHours(request.creditHours);
//        if (request.department != null) {
//            Department department = departmentRepo.findById(request.department)
//                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with name: " + request.department));
//            toEdit.setDepartment(department);
//        }
        courseRepo.save(toEdit);

        return ResponseEntity.ok("Course updated successfully");
    }

    // Delete existing Course given ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Integer id) {
        Course toDelete = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));
        Department department = toDelete.getDepartment();
        department.setCourseCount(department.getCourseCount() - 1);

        courseRepo.delete(toDelete);
        departmentRepo.save(department);

        return ResponseEntity.ok("Course deleted successfully");
    }

    public record ClassRecord(
            Integer class_id,
            String days,
            String time,
            String hall_lab,
            Integer capacity,
            Integer registered,
            String course,
            String department
    ) {
    }

    // Get all Classes of a Course given ID
    @GetMapping("/{id}/classes")
    public List<ClassRecord> getClasses(@PathVariable Integer id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + id));

        return _classRepo.findByCourse(course).stream()
                .map(_class -> new ClassRecord(
                        _class.getID()
                        , _class.getDays()
                        , _class.getTime()
                        , _class.getHall_lab()
                        , _class.getCapacity()
                        , _class.getRegistered()
                        , _class.getCourse().getTitle()
                        , _class.getCourse().getDepartment().getName()))
                .collect(Collectors.toList());
    }

}
