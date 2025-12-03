package ru.practicum.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * DTO для обновления статусов запросов на участие в событии.
 * <p>
 * Используется для получения данных от клиента при массовом обновлении
 * статусов запросов на участие. Позволяет инициатору события подтверждать
 * или отклонять несколько запросов одновременно.
 * </p>
 *
 * <p>
 * Применяется в сценариях модерации запросов на участие, когда создатель события
 * управляет списком участников своего события.
 * </p>
 *
 * <p>
 * Содержит валидацию на уровне бизнес-логики для проверки допустимости
 * запрашиваемых изменений статусов.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {

    /**
     * Список идентификаторов запросов на участие для обновления.
     */
    List<Long> requestIds;

    /**
     * Новый статус для указанных запросов на участие.
     */
    String status;
}
