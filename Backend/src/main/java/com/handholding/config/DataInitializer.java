package com.handholding.config;

import com.handholding.entity.AuthUser;
import com.handholding.repository.AuthUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner seedAdmin(
            AuthUserRepository repository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String username = System.getenv("ADMIN_USERNAME");
            String password = System.getenv("ADMIN_PASSWORD");

            if (username == null || username.isBlank()
                    || password == null || password.length() < 8) {

                log.info(
                        "ADMIN_USERNAME / ADMIN_PASSWORD not set; "
                                + "skipping admin provisioning."
                );
                return;
            }

            if (repository.findByUsername(username) != null) {
                log.info("Admin user '{}' already exists.", username);
                return;
            }

            AuthUser admin = new AuthUser();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole("ADMIN");

            repository.save(admin);

            log.info("Provisioned admin user '{}'.", username);
        };
    }
}