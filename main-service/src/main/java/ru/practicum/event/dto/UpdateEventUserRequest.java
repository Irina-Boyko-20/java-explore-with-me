package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * DTO для обновления события пользователем (инициатором события).
 * <p>
 * Используется для получения данных от клиента при обновлении существующего события
 * его создателем. Поддерживает частичное обновление (PATCH-семантика) - обновляются
 * только те поля, которые явно указаны в запросе.
 * </p>
 *
 * <p>
 * Применяется пользователями для редактирования своих событий. Имеет ограничения
 * по сравнению с административным обновлением - некоторые изменения возможны
 * только до публикации события.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {

    /**
     * Новое краткое описание события.
     */
    @Size(min = 20, max = 2000, message = "Event annotation length must be between {min} and {max} characters")
    String annotation;

    /**
     * Новая категория события.
     */
    Long category;

    /**
     * Новое полное описание события.
     */
    @Size(min = 20, max = 7000, message = "Event description length must be between {min} and {max} characters")
    String description;

    /**
     * Новая дата и время проведения события.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    /**
     * Новое местоположение события.
     */
    LocationDto location;

    /**
     * Новый флаг платности события.
     */
    Boolean paid;

    /**
     * Новое ограничение на количество участников.
     */
    Integer participantLimit;

    /**
     * Новый флаг модерации заявок на участие.
     */
    Boolean requestModeration;

    /**
     * Действие по изменению состояния события.
     */
    String stateAction;

    /**
     * Новый заголовок события.
     */
    @Size(min = 3, max = 120, message = "Event title length must be between {min} and {max} characters")
    String title;
}
