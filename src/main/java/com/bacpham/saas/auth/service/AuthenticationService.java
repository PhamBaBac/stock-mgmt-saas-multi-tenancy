package com.bacpham.saas.auth.service;

import com.bacpham.saas.auth.requests.LoginRequest;
import com.bacpham.saas.auth.responses.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(final LoginRequest request);
}