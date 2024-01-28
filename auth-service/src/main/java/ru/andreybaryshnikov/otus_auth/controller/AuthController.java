package ru.andreybaryshnikov.otus_auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.otus_auth.model.LoginDto;
import ru.andreybaryshnikov.otus_auth.model.User;
import ru.andreybaryshnikov.otus_auth.model.UserDto;
import ru.andreybaryshnikov.otus_auth.repository.UserRepository;
import ru.andreybaryshnikov.otus_auth.service.AuthService;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public User register(@RequestBody UserDto userDto) {
        return authService.register(userDto);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        return authService.login(loginDto, response);
    }

    @GetMapping("/sessions")
    public Map<String, User> sessions() {
        return authService.sessions();
    }

    @GetMapping("/signin")
    public String signin() {
        return "{\"message\": \"Please go to login and provide Login/Password\"}";
    }

    @GetMapping("/auth")
    public User auth(@CookieValue(value = "session_id", required = false) String sessionId,
                     HttpServletResponse response) {
        return authService.auth(sessionId, response);
    }

    @RequestMapping("/logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }
}