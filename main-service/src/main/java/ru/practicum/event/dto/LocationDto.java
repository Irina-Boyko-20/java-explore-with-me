package ru.practicum.event.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * DTO для представления географического местоположения.
 * <p>
 * Используется для передачи данных о координатах места проведения событий
 * между слоями приложения. Содержит валидационные аннотации для проверки
 * корректности географических координат.
 * </p>
 *
 * <p>
 * Применяется в DTO событий ({@link NewEventDto}, {@link UpdateEventUserRequest}, etc.)
 * для определения местоположения мероприятия. Координаты используются для
 * картографического отображения и географического поиска событий.
 * </p>
 *
 * <p>
 * Поддерживает создание объектов через паттерн Builder для удобства тестирования
 * и использования в сервисных методах.
 * </p>
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {

    /**
     * Географическая широта местоположения.
     */
    @NotNull
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    Double lat;

    /**
     * Географическая долгота местоположения.
     */
    @NotNull
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    Double lon;
}
