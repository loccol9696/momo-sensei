package com.example.be.mapper;

import com.example.be.dto.response.UserResponse;
import com.example.be.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
