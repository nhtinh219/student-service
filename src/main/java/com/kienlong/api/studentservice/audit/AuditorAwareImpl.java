package com.kienlong.api.studentservice.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditorAwareImpl.class);
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        LOGGER.info("Username: " + authentication.getName());

        return Optional.of(authentication.getName());
    }
}
