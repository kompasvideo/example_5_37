package ru.andreybaryshnikov.otus_auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Getter
@Setter
@ToString
@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "auth_user")
public class User {

    @NonNull
    @Column(name = "login")
    private String login;

    @NonNull
    @Column(name = "password")
    private String password;

    @NonNull
    @Column(name = "email")
    private String email;

    @NonNull
    @Column(name = "first_name")
    private String first_name;

    @NonNull
    @Column(name = "last_name")
    private String last_name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

}
