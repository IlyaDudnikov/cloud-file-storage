package com.ilyadudnikov.cloudfilestorage.utils;

import com.ilyadudnikov.cloudfilestorage.dto.UserRegistrationDto;
import com.ilyadudnikov.cloudfilestorage.models.User;

public interface UserMapper {
    User toUser(UserRegistrationDto registrationDto);
}
