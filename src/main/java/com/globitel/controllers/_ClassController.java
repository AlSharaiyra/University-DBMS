package com.globitel.controllers;

import com.globitel.entities.Course;
import com.globitel.entities.Instructor;
import com.globitel.entities._Class;
import com.globitel.exceptions.ResourceNotFoundException;
import com.globitel.repos.CourseRepo;
import com.globitel.repos.InstructorRepo;
import com.globitel.repos._ClassRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.globitel.controllers.CourseController.ClassRecord;
import java.util.List;
import java.util.stream.Collectors;
import com.globitel.controllers.DepartmentController.StudentRecord;
import com.globitel.controllers.DepartmentController.InstructorRecord;

@RestController
@RequestMapping("/api/v1/classes")
public class _ClassController {
    @Autowired
    private _ClassRepo classRepo;
    @Autowired
    private CourseRepo courseRepo;
    @Autowired
    private InstructorRepo instructorRepo;

    // Get All Classes
    @GetMapping("")
    public List<ClassRecord> getAllClasses(){
        return classRepo.findAll().stream()
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

    // Get a single Class given ID
    @GetMapping("/{id}")
    public ClassRecord getOneClass(@PathVariable Integer id) {
        return classRepo.findById(id).map(_class -> new ClassRecord(
                        _class.getID()
                        , _class.getDays()
                        , _class.getTime()
                        , _class.getHall_lab()
                        , _class.getCapacity()
                        , _class.getRegistered()
                        , _class.getCourse().getTitle()
                        , _class.getCourse().getDepartment().getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + id));
    }

    public record newClass(
            String days,
            String time,
            String hall_lab,
            Integer capacity,
            Integer course_id
//            Integer instructor_id
    ){
    }

    // Add a new Class
    @PostMapping("")
    public ResponseEntity<String> addClass(@RequestBody newClass request){
        _Class toAdd = new _Class();
        Course course = courseRepo.findById(request.course_id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + request.course_id));

//        Instructor instructor = instructorRepo.findById(request.instructor_id)
//                        .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + request.instructor_id));

        course.setNoOfStudents(course.getNoOfStudents() + 1);
        toAdd.setCourse(course);
        toAdd.setDays(request.days);
        toAdd.setTime(request.time);
        toAdd.setHall_lab(request.hall_lab);
        toAdd.setCapacity(request.capacity);
//        toAdd.setInstructor(instructor);
        classRepo.save(toAdd);
        courseRepo.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).body("Class added successfully");
    }

    // Get all students who joined a class given ID
    @GetMapping("/{id}/students")
    public List<StudentRecord> getStudents(@PathVariable Integer id){
        _Class _class = classRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + id));

        return  _class.getStudents().stream()
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
    // Get the instructor who teaches a class given ID
    @GetMapping("/{id}/instructor")
    public InstructorRecord getInstructor(@PathVariable Integer id){
        _Class _class = classRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + id));

        Instructor instructor = _class.getInstructor();
        if (instructor == null) {
            throw new ResourceNotFoundException("Instructor not found for class ID: " + id);
        }

        return new InstructorRecord(
                        instructor.getID()
                        , instructor.getName()
                        , instructor.getEmail()
                        , instructor.getPhone()
                        , instructor.getAddress()
                        , instructor.getDepartment().getName());

    }

}
