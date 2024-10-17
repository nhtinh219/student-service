package com.kienlong.api.studentservice.security;

import com.kienlong.api.studentservice.repo.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class RefreshTokenRemovalScheduledTask {

    private final static Logger LOGGER = LoggerFactory.getLogger(RefreshTokenRemovalScheduledTask.class);

    @Autowired
    RefreshTokenRepository repository;

    @Scheduled(fixedDelayString = "${app.refresh-token.removal.interval}", initialDelay = 5000L)
    @Transactional
    public void deleteExpiredRefreshToken() {
        int numberOfTokensDeleted = repository.deleteByExpiryTime();

        LOGGER.info("Expired refresh tokens removed: " + numberOfTokensDeleted);
    }
}
