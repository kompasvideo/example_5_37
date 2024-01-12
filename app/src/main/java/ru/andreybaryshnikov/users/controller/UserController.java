package ru.andreybaryshnikov.users.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import ru.andreybaryshnikov.users.exception.UnauthorizedException;
import ru.andreybaryshnikov.users.model.User;
import ru.andreybaryshnikov.users.model.UserProfile;
import ru.andreybaryshnikov.users.model.dto.UserProfileDto;
import ru.andreybaryshnikov.users.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/users/me")
    public User me(@RequestHeader("X-UserId") String xUserId,
                   @RequestHeader("X-User") String xUser,
                   @RequestHeader("X-Email") String xEmail,
                   @RequestHeader("X-First-Name") String xFirstName,
                   @RequestHeader("X-Last-Name") String xLastName) {
        if (xUserId == null)
            throw new UnauthorizedException();
        User user = new User();
        user.setId(Long.parseLong(xUserId));
        user.setLogin(xUser);
        user.setEmail(xEmail);
        user.setFirst_name(xFirstName);
        user.setLast_name(xLastName);
        UserProfile userProfile = userService.getUserProfileById(user.getId());
        if (userProfile != null) {
            user.setAvatar_uri(userProfile.getAvatar_uri());
            user.setAge(userProfile.getAge());
        }
        return user;
    }

    @PutMapping("/users/me")
    public UserProfile updateMe(@RequestBody UserProfileDto userProfileDto,
                                @RequestHeader("X-UserId") String xUserId,
                                @RequestHeader("X-User") String xUser,
                                @RequestHeader("X-Email") String xEmail,
                                @RequestHeader("X-First-Name") String xFirstName,
                                @RequestHeader("X-Last-Name") String xLastName) {
        if (xUserId == null)
            throw new UnauthorizedException();
        User user = new User();
        user.setId(Long.parseLong(xUserId));
        user.setLogin(xUser);
        user.setEmail(xEmail);
        user.setFirst_name(xFirstName);
        user.setLast_name(xLastName);
        UserProfile userProfile = modelMapper.map(userProfileDto, UserProfile.class);
        userProfile = userService.updateUser(user.getId(), userProfile);
        return userProfile;
    }
}
