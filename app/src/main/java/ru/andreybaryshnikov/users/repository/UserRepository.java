package ru.andreybaryshnikov.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.andreybaryshnikov.users.model.UserProfile;

public interface UserRepository extends JpaRepository<UserProfile,Long> {
}
