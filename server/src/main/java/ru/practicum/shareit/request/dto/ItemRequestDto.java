package ru.practicum.shareit.request.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.RespondItemRequest;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDto {

    private Long id;
    private String description;
    private Long requestorId;
    private List<RespondItemRequest> items;
    private String created;
}
