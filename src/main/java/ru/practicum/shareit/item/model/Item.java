package ru.practicum.shareit.item.model;


import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.user.User;


@Getter
@Setter
@Builder
@Table(name = "items")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Item {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    @Column(name = "name",nullable = false)
    private String name;
    @Column(name = "description",nullable = false)
    private String description;
    @Column(name = "available")
    private boolean available;
    @ManyToOne
    @JoinColumn(name = "owner_id",nullable = false)
    private User owner;
    @Column(name = "request")
    private String request;
}
