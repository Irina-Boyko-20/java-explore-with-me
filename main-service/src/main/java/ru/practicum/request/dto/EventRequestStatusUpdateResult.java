package ru.practicum.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * DTO для представления результата массового обновления статусов запросов на участие.
 * <p>
 * Используется для возврата результата операции подтверждения или отклонения
 * нескольких запросов на участие в событии. Содержит разделенные списки
 * подтвержденных и отклоненных запросов.
 * </p>
 *
 * <p>
 * Применяется в сценариях, когда инициатор события массово обрабатывает
 * запросы на участие - например, подтверждает несколько запросов одновременно
 * или отклоняет группу запросов.
 * </p>
 *
 * <p>
 * Позволяет клиенту четко видеть, какие запросы были успешно обработаны
 * и в какой статус они перешли.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateResult {

    /**
     * Список запросов на участие, которые были подтверждены.
     */
    List<ParticipationRequestDto> confirmedRequests;

    /**
     * Список запросов на участие, которые были отклонены.
     */
    List<ParticipationRequestDto> rejectedRequests;
}
