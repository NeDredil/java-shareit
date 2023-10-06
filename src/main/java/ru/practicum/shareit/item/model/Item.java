package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "request_id")
    private Long request;

}