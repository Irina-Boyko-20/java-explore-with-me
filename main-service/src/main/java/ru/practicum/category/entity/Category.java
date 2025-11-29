package ru.practicum.category.entity;

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
import ru.practicum.event.entity.Event;

/**
 * Сущность, представляющая категорию событий.
 * <p>
 * Категории используются для классификации событий по тематикам.
 * Каждая категория имеет уникальное название, что позволяет однозначно
 * идентифицировать тематическую направленность событий.
 * </p>
 *
 * <p>
 * Связана с событиями ({@link Event}) через отношение один-ко-многим,
 * где одно событие принадлежит одной категории, а в одной категории
 * может находиться множество событий.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "categories")
public class Category {

    /**
     * Уникальный идентификатор категории.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /**
     * Название категории.
     */
    @Column(name = "name", nullable = false, unique = true)
    String name;
}
