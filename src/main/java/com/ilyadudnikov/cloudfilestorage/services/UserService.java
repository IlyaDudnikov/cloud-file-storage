package com.ilyadudnikov.cloudfilestorage.services;

import com.ilyadudnikov.cloudfilestorage.dto.UserRegistrationDto;
import com.ilyadudnikov.cloudfilestorage.exeptions.PasswordsDoNotMatchException;
import com.ilyadudnikov.cloudfilestorage.exeptions.UserWithThisNameAlreadyExistsException;
import com.ilyadudnikov.cloudfilestorage.models.User;
import com.ilyadudnikov.cloudfilestorage.repositories.UserRepository;
import com.ilyadudnikov.cloudfilestorage.utils.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, UserMapper userMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void register(UserRegistrationDto userDto) throws UserWithThisNameAlreadyExistsException {
        Optional<User> foundUser = userRepository.findByUsername(userDto.getUsername());

        if (foundUser.isPresent()) {
            throw new UserWithThisNameAlreadyExistsException("User with this name already exists");
        }

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }

        User user = userMapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}
