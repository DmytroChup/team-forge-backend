package com.teamforge.backend.service;

import com.teamforge.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final RefreshTokenRepository  refreshTokenRepository;

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanupTokens() {
        log.debug("Starting scheduled cleanup of expired refresh tokens...");

        int deletedCount = refreshTokenRepository.deleteAllByExpiryDateBeforeOrRevokedTrue(Instant.now());

        if (deletedCount > 0) {
            log.info("Cleanup finished. Removed {} expired tokens.", deletedCount);
        } else {
            log.debug("Cleanup finished. No expired tokens found.");
        }
    }
}
