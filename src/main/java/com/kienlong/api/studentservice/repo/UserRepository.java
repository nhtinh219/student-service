package com.kienlong.api.studentservice.repo;

import java.util.Optional;

import com.kienlong.api.studentservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
}
