package ru.andreybaryshnikov.users.service;

import ru.andreybaryshnikov.users.model.UserProfile;

public interface UserService {
    UserProfile updateUser(long userId, UserProfile updateUser);
    UserProfile getUserProfileById(long id);
}
