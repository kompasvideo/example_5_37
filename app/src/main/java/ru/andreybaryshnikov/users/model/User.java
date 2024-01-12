package ru.andreybaryshnikov.users.model;

import lombok.*;

@Data
public class User {
    private long id;
    private String login;
    private String password;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar_uri;
    private String age;
}
