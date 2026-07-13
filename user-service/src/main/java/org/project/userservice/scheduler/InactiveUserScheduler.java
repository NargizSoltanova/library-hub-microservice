package org.project.userservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.userservice.entity.UserEntity;
import org.project.userservice.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InactiveUserScheduler {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Scheduled(
            cron = "0 0 9 * * MON",
            zone = "Asia/Baku"
    )
    public void logInactiveUsers() {
        var date = LocalDateTime.now().minusDays(90);

        var inactiveUsers = userRepository.findInactiveUsers(date);

        if (inactiveUsers.isEmpty()) {
            log.info("No users inactive for more than 90 days were found");
            return;
        }

        log.info("Users inactive for more than 90 days. Count: {}", inactiveUsers.size());

        inactiveUsers.forEach(user ->{
            boolean isLogin = user.getLastLoginAt() != null;

            var lastActivityAt = isLogin
                    ? user.getLastLoginAt()
                    : user.getCreatedAt();

            log.info(
                    "Inactive user: id={}, username={}, lastLoginAt={}, type={}",
                    user.getId(),
                    user.getUsername(),
                    lastActivityAt,
                    isLogin ? "LAST_LOGIN_AT" : "CREATED_AT"
            );
        });
    }
}
