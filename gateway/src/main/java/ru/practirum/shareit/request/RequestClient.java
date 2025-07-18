package ru.practirum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practirum.shareit.client.BaseClient;
import ru.practirum.shareit.request.dto.NewItemRequest;


@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, NewItemRequest requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> findUserRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findAllRequests(Long userId) {

        return get("", userId);
    }

    public ResponseEntity<Object> findRequestById(Long requestId) {
        return get("/" + requestId);
    }
}
