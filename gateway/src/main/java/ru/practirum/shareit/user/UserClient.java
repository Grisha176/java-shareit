package ru.practirum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practirum.shareit.client.BaseClient;
import ru.practirum.shareit.user.dto.NewUserRequest;
import ru.practirum.shareit.user.dto.UpdateUserRequest;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder restBuilder) {
        super(
                restBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(NewUserRequest newUserRequest) {
        return post("", newUserRequest);
    }

    public ResponseEntity<Object> findById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> findAll() {
        return get("/");
    }

    public ResponseEntity<Object> update(Long userId, UpdateUserRequest userDto) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> deleteById(Long userId) {
        return delete("/" + userId);
    }
}
