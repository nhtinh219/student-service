package com.kienlong.api.studentservice.controller;

import java.util.List;

import com.kienlong.api.studentservice.entity.Student;
import com.kienlong.api.studentservice.repo.StudentRepository;
import jakarta.annotation.security.RolesAllowed;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/students")
@Validated
@EnableMethodSecurity
public class StudentApiController {

    @Autowired
    StudentRepository repo;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam
                                  @Min(value = 10, message = "Page size min is 10")
                                  @Max(value = 50, message = "Page size max is 50") Integer pageSize,
                                  @Positive(message = "Page number must greater than zero") Integer pageNum) {
        System.out.println("Page size = " + pageSize);
        System.out.println("Page number = " + pageNum);

        List<Student> listStudents = repo.findAll();
        if (listStudents.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(listStudents, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Student> add(@RequestBody Student student) {

        repo.save(student);

        return new ResponseEntity<Student>(student, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Student> replace(@RequestBody Student student) {

        if (repo.existsById(student.getId())) {

            repo.save(student);

            return new ResponseEntity<Student>(student, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<?> delete(@PathVariable @Positive Integer id) {

        if (repo.existsById(id)) {

            repo.deleteById(id);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
