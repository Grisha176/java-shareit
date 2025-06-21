package ru.practirum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateItem {

    private String name;
    private String description;
    private Boolean available;

}
