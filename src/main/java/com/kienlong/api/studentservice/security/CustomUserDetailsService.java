package com.kienlong.api.studentservice.security;

import java.util.Optional;

import com.kienlong.api.studentservice.entity.User;
import com.kienlong.api.studentservice.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> findByUsername = userRepo.findByUsername(username);

        if (findByUsername.isEmpty()) {
            throw new UsernameNotFoundException("No user found with given user name");
        }
        return new CustomUserDetails(findByUsername.get());
    }

}
