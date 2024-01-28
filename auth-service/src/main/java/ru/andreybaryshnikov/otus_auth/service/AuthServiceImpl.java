package ru.andreybaryshnikov.otus_auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.andreybaryshnikov.otus_auth.exception.UnauthorizedException;
import ru.andreybaryshnikov.otus_auth.model.*;
import ru.andreybaryshnikov.otus_auth.repository.UserRepository;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    @Value("${billing.uri}")
    private String url;

    @Value("${notification.uri}")
    private String urlNotific;
    private final long initMoney = 1000;
    private static final Map<String, User> sessions = new HashMap<>();
    private static final Random random = new Random();


    public User register(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        User user2 = userRepository.save(user);
        UUID uuid = UUID.randomUUID();
        addAccountToBillingAndAddMoneyToAccount(user2, String.valueOf(uuid));
        sendMessageToNotificafion(uuid.toString(), user2.getId(), "+",
            initMoney, initMoney);
        return user2;
    }

    public String login(LoginDto loginDto, HttpServletResponse response) {
        User userInfo = getUserByCredentials(loginDto);
        if (userInfo == null)
            throw new UnauthorizedException();
        String sessionId = createSession(userInfo);
        response.addCookie(new Cookie("session_id", sessionId));
        return "{\"IDtoken\": \"" + sessionId + "\"}";
    }

    public Map<String, User> sessions() {
        return sessions;
    }

    public User auth(String sessionId,
                     HttpServletResponse response) {
        if (sessionId != null && sessions.containsKey(sessionId)) {
            User data = sessions.get(sessionId);
            response.setHeader("X-UserId", String.valueOf(data.getId()));
            response.setHeader("X-User", data.getLogin());
            response.setHeader("X-Email", data.getEmail());
            response.setHeader("X-First-Name", data.getFirst_name());
            response.setHeader("X-Last-Name", data.getLast_name());
            response.setStatus(HttpStatus.OK.value());
            return data;
        }
        throw new UnauthorizedException();
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("session_id", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private void addAccountToBillingAndAddMoneyToAccount(User user, String uuid) {
        log.info("-- 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 -- " + url);
        URI uri = null;
        try {
            log.info("-- 2 --");
            uri = new URI(url);
            log.info("-- 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
        }
        log.info("-- 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- 3 -- X-Request-Id - " + uuid);
        headers.add("X-Request-Id", uuid);
        log.info("-- 3 -- X-UserId - " + user.getId());
        headers.add("X-UserId", String.valueOf(user.getId()));
        PlusMoney plusMoney = new PlusMoney( 1000, 1000);
        RequestEntity<PlusMoney> requestEntity = new RequestEntity<>(plusMoney, headers, HttpMethod.POST, uri);
        log.info("-- 4 --");
        log.info("-- 4 --" + requestEntity.getBody());
        rt.exchange(requestEntity, String.class);
        log.info("-- 5 --");
    }

    private User getUserByCredentials(LoginDto loginDto) {
        return userRepository.findByLoginAndPassword(loginDto.getLogin(), loginDto.getPassword());
    }

    private String createSession(User userInfo) {
        String sessionId = generateSessionId(40);
        sessions.put(sessionId, userInfo);
        return sessionId;
    }

    private String generateSessionId(int size) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void sendMessageToNotificafion(String xRequestId, long userId, String operation,
                                           double count, double total) {
        log.info("-- 1 --");
        RestTemplate rt = new RestTemplate();
        log.info("-- 1 -- " + urlNotific);
        URI uri = null;
        try {
            log.info("-- 2 --");
            uri = new URI(urlNotific);
            log.info("-- 2 --" + uri.toURL());
        } catch (URISyntaxException e) {
            log.info("URISyntaxException - " + e);
        } catch (MalformedURLException e) {
            log.info("MalformedURLException - " + e);
        }
        log.info("-- 3 --");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/json");
        log.info("-- 3 -- X-Request-Id - " + xRequestId);
        headers.add("X-Request-Id", xRequestId);
        log.info("-- 3 -- X-UserId - " + userId);
        headers.add("X-UserId", String.valueOf(userId));
        Money money = new Money(LocalDateTime.now(), operation, count, total);
        RequestEntity<Money> requestEntity = new RequestEntity<>(money, headers, HttpMethod.POST, uri);
        log.info("-- 4 --");
        log.info("-- 4 -- " + money);
        rt.exchange(requestEntity, String.class);
        log.info("-- 5 --");
    }
}
