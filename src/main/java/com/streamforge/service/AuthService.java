package com.streamforge.service;

import com.streamforge.dto.request.auth.LoginRequest;
import com.streamforge.dto.request.auth.RegisterRequest;
import com.streamforge.dto.response.LoginResponse;

public interface AuthService {


    LoginResponse register(
            RegisterRequest request
    );


    LoginResponse login(
            LoginRequest request
    );

}