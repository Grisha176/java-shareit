package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UpdatedUserRequest {

    private String email;
    private String name;

    public boolean hasUsername() {
        return !(name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }


}
