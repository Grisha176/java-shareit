package ru.practicum.shareit.item.comment;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "comments")
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column(name = "comment", nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", nullable = false)
    private User author;

    @Column(name = "created", nullable = false)
    public LocalDateTime created;
}
