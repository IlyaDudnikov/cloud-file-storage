package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.UserRegistrationDto;
import com.ilyadudnikov.cloudfilestorage.models.User;
import com.ilyadudnikov.cloudfilestorage.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(
            "mysql:latest"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUser_ShouldSaveToDatabase() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("testUser", "password", "password", "email@test.com");
        userService.register(userRegistrationDto);

        Optional<User> registeredUser = userRepository.findByUsername(userRegistrationDto.getUsername());
        assertTrue(registeredUser.isPresent());
        assertEquals(userRegistrationDto.getUsername(), registeredUser.get().getUsername());
    }

    @Test
    void registerUser_PasswordShouldBeEncoded() {
        String password = "password";
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto("testUser2", password, password, "email2@test.com");
        userService.register(userRegistrationDto);

        Optional<User> registeredUser = userRepository.findByUsername(userRegistrationDto.getUsername());
        assertTrue(registeredUser.isPresent());

        String encodedPassword = registeredUser.get().getPassword();

        assertNotEquals(encodedPassword, password);
        assertTrue(passwordEncoder.matches(password, encodedPassword));
    }
}
