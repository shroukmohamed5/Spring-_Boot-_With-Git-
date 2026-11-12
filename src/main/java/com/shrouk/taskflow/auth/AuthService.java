package com.shrouk.taskflow.auth;

import com.shrouk.taskflow.auth.AuthDtos.AuthResponse;
import com.shrouk.taskflow.auth.AuthDtos.LoginRequest;
import com.shrouk.taskflow.auth.AuthDtos.RegisterRequest;
import com.shrouk.taskflow.exception.ConflictException;
import com.shrouk.taskflow.security.JwtService;
import com.shrouk.taskflow.user.User;
import com.shrouk.taskflow.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new ConflictException("Username already taken");
        }
        if (users.existsByEmail(req.email())) {
            throw new ConflictException("Email already registered");
        }
        users.save(new User(req.username(), req.email(), encoder.encode(req.password())));
        return new AuthResponse(jwt.generateToken(req.username()));
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        return new AuthResponse(jwt.generateToken(req.username()));
    }
}
