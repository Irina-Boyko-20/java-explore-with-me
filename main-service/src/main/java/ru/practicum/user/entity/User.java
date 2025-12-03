package ru.practicum.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Сущность, представляющая пользователя системы.
 * <p>
 * Пользователи являются основными участниками системы - они могут создавать события,
 * участвовать в них в качестве участников, оставлять запросы на участие и управлять
 * своими активностями.
 * </p>
 *
 * <p>
 * Каждый пользователь имеет уникальный email-адрес, который используется для
 * идентификации и аутентификации в системе.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /**
     * Электронная почта пользователя.
     */
    @Column(name = "email", nullable = false, unique = true)
    String email;

    /**
     * Имя пользователя.
     */
    @Column(name = "name", nullable = false)
    String name;
}
