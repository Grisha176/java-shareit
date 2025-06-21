package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    @Column(name = "text")
    private String description;
    @Column(name = "requestor_id")
    private Long requestorId;
    @Column(name = "created_time")
    private LocalDateTime createdTime = LocalDateTime.now();
    @OneToMany(mappedBy = "request")
    List<Item> items;

}
