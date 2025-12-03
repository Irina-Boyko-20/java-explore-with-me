package ru.practicum.event.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

/**
 * Встраиваемая сущность, представляющая географическое местоположение.
 * <p>
 * Используется для определения координат места проведения событий.
 * Сохраняется как часть сущности {@link Event} в той же таблице базы данных.
 * </p>
 *
 * <p>
 * Координаты используются для картографического отображения событий,
 * географического поиска и расчета расстояний между локациями.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
@Builder
public class Location {

    /**
     * Географическая широта местоположения.
     */
    Double lat;

    /**
     * Географическая долгота местоположения.
     */
    Double lon;
}
