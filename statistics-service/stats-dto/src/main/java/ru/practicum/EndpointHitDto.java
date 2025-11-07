package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * Класс, представляющий объект передачи данных (DTO) для информации о посещении эндпоинта.
 * Этот DTO используется для передачи данных о статистике посещений, включая идентификатор,
 * название приложения, URI эндпоинта, IP-адрес клиента и время посещения.
 * Поля валидируются с помощью аннотаций Jakarta Validation: строки не могут быть пустыми,
 * а время не может быть null. Время сериализуется в JSON в формате "yyyy-MM-dd HH:mm:ss".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {

    /**
     * Уникальный идентификатор.
     */
    Long id;

    /**
     * Название приложения, где произошло посещение эндпоинта.
     */
    @NotBlank
    String app;

    /**
     * URI эндпоинта, который был посещен.
     */
    @NotBlank
    String uri;

    /**
     * IP-адрес клиента, совершившего запрос к эндпоинту.
     */
    @NotBlank
    String ip;

    /**
     * Время посещения эндпоинта.
     */
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;
}
