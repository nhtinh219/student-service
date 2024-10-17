package com.kienlong.api.studentservice.security.repo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.kienlong.api.studentservice.entity.User;
import com.kienlong.api.studentservice.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

    @Autowired
    UserRepository repo;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testAddFirstUser() {
        User user1 = new User();
        user1.setUsername("tinhnh");
        user1.setRole("read");

        String rawPass = "nhtinh";
        String encodedPass = passwordEncoder.encode(rawPass);

        user1.setPassword(encodedPass);

        User savedUser = repo.save(user1);

        assertThat(savedUser).isNotNull();
    }

    @Test
    public void testAddSecondUser() {
        User user2 = new User();
        user2.setUsername("admin");
        user2.setRole("write");

        String rawPass = "admin";
        String encodedPass = passwordEncoder.encode(rawPass);

        user2.setPassword(encodedPass);

        User savedUser = repo.save(user2);

        assertThat(savedUser).isNotNull();
    }

    @Test
    public void testFindUserNotFound() {
        Optional<User> findByUsername = repo.findByUsername("xxx");

        assertThat(findByUsername).isNotPresent();
    }

    @Test
    public void testFindUserFound() {
        String username = "admin";
        Optional<User> findByUsername = repo.findByUsername(username);

        assertThat(findByUsername).isPresent();

        User user = findByUsername.get();
        assertThat(user.getUsername()).isEqualTo(username);
    }

}
