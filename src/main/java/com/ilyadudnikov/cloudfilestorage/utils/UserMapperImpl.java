package com.ilyadudnikov.cloudfilestorage.utils;

import com.ilyadudnikov.cloudfilestorage.dto.UserRegistrationDto;
import com.ilyadudnikov.cloudfilestorage.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(UserRegistrationDto registrationDto) {
        if ( registrationDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.username( registrationDto.getUsername() );
        user.password( registrationDto.getPassword() );
        user.email( registrationDto.getEmail() );

        user.role( "ROLE_USER" );

        return user.build();
    }
}

