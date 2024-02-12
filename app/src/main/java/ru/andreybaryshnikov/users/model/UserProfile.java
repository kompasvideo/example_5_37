package ru.andreybaryshnikov.users.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import jakarta.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Getter
@Setter
@ToString
@Builder
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "avatar_uri")
    private String avatar_uri;

    @Column(name = "age")
    private String age;

    public UserProfile() {
        this.id = 0L;
        this.avatar_uri = "";
        this.age = "0";
    }
}
