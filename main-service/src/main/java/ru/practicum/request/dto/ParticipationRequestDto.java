package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.entity.EventRequestStatus;
import ru.practicum.request.entity.ParticipationRequest;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для представления запроса на участие в событии.
 * <p>
 * Используется для передачи данных о запросах на участие между слоями приложения,
 * в частности - для возврата информации о запросах в API-ответах. Содержит все
 * необходимые данные для отображения информации о запросе на участие.
 * </p>
 *
 * <p>
 * Поддерживает создание объектов через паттерн Builder благодаря аннотации {@link Builder},
 * что упрощает создание DTO в тестовых сценариях и сервисных методах.
 * </p>
 *
 * <p>
 * Соответствует структуре сущности {@link ParticipationRequest} и используется
 * для сериализации/десериализации данных в REST API.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {

    /**
     * Уникальный идентификатор запроса на участие.
     */
    Long id;

    /**
     * Идентификатор события, для которого создан запрос на участие.
     */
    private Long event;

    /**
     * Идентификатор пользователя, создавшего запрос на участие.
     */
    private Long requester;

    /**
     * Статус запроса на участие.
     */
    private EventRequestStatus status;

    /**
     * Дата и время создания запроса на участие.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
