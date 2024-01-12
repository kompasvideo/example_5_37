package ru.andreybaryshnikov.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreybaryshnikov.users.model.UserProfile;
import ru.andreybaryshnikov.users.repository.UserRepository;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserProfile updateUser(long userId, UserProfile updateUser) {
        Optional<UserProfile> userOp = userRepository.findById(userId);
        if (userOp.isPresent()) {
            UserProfile user = userOp.get();
            user.setAvatar_uri(updateUser.getAvatar_uri());
            if (!Objects.equals(updateUser.getAge(), "0"))
                user.setAge(updateUser.getAge());
            return userRepository.save(user);
        }
        return userRepository.save(updateUser);
    }

    @Override
    public UserProfile getUserProfileById(long id) {
        Optional<UserProfile> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }
}
