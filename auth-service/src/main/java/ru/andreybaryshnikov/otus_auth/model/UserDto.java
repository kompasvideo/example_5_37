package ru.andreybaryshnikov.otus_auth.model;
import lombok.Data;

@Data
public class UserDto {
    private String login;
    private String password;
    private String email;
    private String first_name;
    private String last_name;
}
