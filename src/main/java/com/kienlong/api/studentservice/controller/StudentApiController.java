package com.kienlong.api.studentservice.controller;

import java.util.List;

import com.kienlong.api.studentservice.entity.Student;
import com.kienlong.api.studentservice.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/students")
@Validated
@EnableMethodSecurity
public class StudentApiController {

    @Autowired
    StudentService studentService;

    @GetMapping
    public ResponseEntity<?> list() {

        List<Student> listStudents = studentService.listStudent();
        if (listStudents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(listStudents, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> get(@PathVariable("id") @Positive Integer id) {
        Student student = studentService.getStudent(id);

        if (student != null) {
            return new ResponseEntity<>(student, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Student> add(@RequestBody Student student) {
        Student addedStudent = studentService.addStudent(student);

        return new ResponseEntity<>(addedStudent, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Student> replace(@RequestBody Student student) {

        Student updatedStudent = studentService.updateStudent(student);

        if (updatedStudent != null) {
            return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<?> delete(@PathVariable("id") @Positive Integer id) {

        if (studentService.deleteStudent(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
