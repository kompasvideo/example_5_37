package ru.andreybaryshnikov.otus_auth.service;

import jakarta.servlet.http.HttpServletResponse;
import ru.andreybaryshnikov.otus_auth.model.LoginDto;
import ru.andreybaryshnikov.otus_auth.model.User;
import ru.andreybaryshnikov.otus_auth.model.UserDto;

import java.util.Map;

public interface AuthService {
    User register(UserDto userDto);
    String login(LoginDto loginDto, HttpServletResponse response);
    Map<String, User> sessions();
    User auth(String sessionId, HttpServletResponse response);
    void logout(HttpServletResponse response);
}
