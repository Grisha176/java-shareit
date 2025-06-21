package ru.practicum.shareit.user;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

}
