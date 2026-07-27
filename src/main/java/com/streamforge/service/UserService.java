package com.streamforge.service;

import com.streamforge.dto.request.UserRequest;
import com.streamforge.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long userId);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long userId, UserRequest request);

    void deleteUser(Long userId);

}