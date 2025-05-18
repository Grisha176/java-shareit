package ru.practicum.shareit.item.model;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class Item {

    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long ownerId;
    private String request;

}
