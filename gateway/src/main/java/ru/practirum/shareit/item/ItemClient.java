package ru.practirum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practirum.shareit.client.BaseClient;
import ru.practirum.shareit.item.comment.CommentDto;
import ru.practirum.shareit.item.dto.ItemDto;
import ru.practirum.shareit.item.dto.UpdateItem;

import java.util.Collections;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, UpdateItem updateItem) {
        return patch("/" + itemId, userId, updateItem);
    }

    public ResponseEntity<Object> findItemById(Long itemId, Long requesterId) {
        return get("/" + itemId, requesterId);
    }


    public ResponseEntity<Object> findAll(Long userId) {

        return get("", userId, null);
    }

    public ResponseEntity<Object> deleteById(Long itemId) {
        return delete("/" + itemId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Map<String, Object> parameters = Map.of(

                "text", text
        );

        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> createComment(Long userId, CommentDto commentDto, Long itemId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
