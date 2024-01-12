package ru.andreybaryshnikov.otus_auth.model;

import lombok.Data;

@Data
public class LoginDto {
    private String login;
    private String password;
}
