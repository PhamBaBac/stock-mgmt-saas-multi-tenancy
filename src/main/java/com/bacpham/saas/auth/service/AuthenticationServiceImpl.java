package com.bacpham.saas.auth.service;

import com.bacpham.saas.auth.requests.LoginRequest;
import com.bacpham.saas.auth.responses.LoginResponse;
import com.bacpham.saas.entities.User;
import com.bacpham.saas.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    @Override
    public LoginResponse login(final LoginRequest request) {
        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        final User user = (User) authentication.getPrincipal();

        final String token = this.jwtTokenService.generateAccessToken(user.getTenantId(),
                user.getId(),
                user.getRole()
                        .name());
        final String tokenType = "Bearer";

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType(tokenType)
                .build();
    }
}
