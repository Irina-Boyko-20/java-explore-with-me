package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.entity.Event;

import java.time.LocalDateTime;

/**
 * DTO для создания нового события.
 * <p>
 * Используется для получения данных от клиента при создании нового события.
 * Содержит валидационные аннотации для проверки корректности входящих данных
 * в соответствии с бизнес-требованиями системы.
 * </p>
 *
 * <p>
 * Применяется на уровне контроллеров для приема и первичной валидации данных
 * перед их преобразованием в сущность {@link Event}. Все поля, кроме отмеченных
 * как опциональные, являются обязательными для создания события.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {

    /**
     * Кратное описание события.
     */
    @NotBlank(message = "Event annotation must be specified")
    @Size(min = 20, max = 2000, message = "Event annotation length must be between {min} and {max} characters")
    String annotation;

    /**
     * Категория события.
     */
    @NotNull
    Long category;

    /**
     * Полное описание события.
     */
    @NotBlank(message = "Event description must be specified")
    @Size(min = 20, max = 7000, message = "Event description length must be between {min} and {max} characters")
    String description;

    /**
     * Дата и время проведения события.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    /**
     * Местоположение события.
     */
    @NotNull
    LocationDto location;

    /**
     * Флаг платности события.
     */
    Boolean paid = false;

    /**
     * Ограничение на количество участников. Значение 0 - отсутствие ограничения.
     */
    Integer participantLimit = 0;

    /**
     * Требуется ли пре-модерация заявок на участие.
     */
    Boolean requestModeration = true;

    /**
     * Заголовок события.
     */
    @NotBlank(message = "Event title must be specified")
    @Size(min = 3, max = 120, message = "Event title length must be between {min} and {max} characters")
    String title;
}
