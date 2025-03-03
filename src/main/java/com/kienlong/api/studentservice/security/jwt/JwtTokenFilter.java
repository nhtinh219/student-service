package com.kienlong.api.studentservice.security.jwt;

import com.kienlong.api.studentservice.entity.User;
import com.kienlong.api.studentservice.error.JwtValidationException;
import com.kienlong.api.studentservice.i18n.MessageCode;
import com.kienlong.api.studentservice.repo.UserRepository;
import com.kienlong.api.studentservice.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    JwtUtility jwtUtility;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    HandlerExceptionResolver exceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = getBearerToken(request);

        LOGGER.info("Token: " + token);

        try {
            Claims claims = jwtUtility.validateAccessToken(token, request);
            UserDetails userDetails = getUserDetails(claims);

            Locale locale = new Locale(request.getHeader("Accept-Language"));
            if (!isUserExisted(userDetails)){ // Check if user still exist or not
                String message = messageSource.getMessage(MessageCode.ERROR_BAD_CREDENTIALS.getCode(),null, locale);
                throw new JwtValidationException(message);
            }

            setAuthenticationContext(userDetails, request);

            // Continue filter chain
            filterChain.doFilter(request, response);

            clearAuthenticationContext();
        } catch (JwtValidationException e) {
            LOGGER.error(e.getMessage(), e);

            exceptionResolver.resolveException(request, response, null, e);
        }
    }

    private boolean isUserExisted(UserDetails userDetails) {
        Optional<User> userInDB = userRepository.findByUsername(userDetails.getUsername());
        return userInDB.isPresent();
    }

    private void clearAuthenticationContext() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(Claims claims) {
        String subject = (String) claims.get(Claims.SUBJECT);
        //Subject format example: 1,username
        String[] array = subject.split(",");

        Integer userId = Integer.valueOf(array[0]);
        String username = array[1];

        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        String role = (String) claims.get("role");
        user.setRole(role);

        LOGGER.info("User parse from JWT: " + user.getId() + ", " + user.getUsername() + ", " + user.getRole());

        return new CustomUserDetails(user);
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        LOGGER.info("Authorization Header: " + header);

        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer");
    }

    private String getBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        String[] array = header.split(" ");
        if (array.length == 2) {
            return array[1];
        }
        return null;
    }
}
