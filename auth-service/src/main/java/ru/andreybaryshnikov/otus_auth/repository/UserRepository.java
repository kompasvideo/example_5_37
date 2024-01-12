package ru.andreybaryshnikov.otus_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.andreybaryshnikov.otus_auth.model.User;

@EnableJpaRepositories
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
        User findByLoginAndPassword(@Param("login") String login, @Param("password") String password);
}


