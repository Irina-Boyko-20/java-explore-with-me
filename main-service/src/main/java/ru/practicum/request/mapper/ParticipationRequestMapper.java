package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.event.entity.EventRequestStatus;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.entity.ParticipationRequest;

/**
 * Маппер для преобразования между сущностью запроса на участие и DTO.
 * <p>
 * Обеспечивает конвертацию данных между слоями приложения:
 * из сущности {@link ParticipationRequest} в DTO {@link ParticipationRequestDto}
 * для возврата в API-ответах. Использует MapStruct для автоматической генерации
 * кода преобразования.
 * </p>
 *
 * <p>
 * Выполняет прямое отображение всех полей сущности на соответствующие поля DTO,
 * включая преобразование enum {@link EventRequestStatus} и форматирование даты
 * создания запроса.
 * </p>
 *
 * <p>
 * Автоматически регистрируется как Spring-компонент благодаря аннотации
 * {@code componentModel = "spring"}, что позволяет использовать инъекцию зависимостей.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    /**
     * Преобразует сущность запроса на участие в DTO для ответа API.
     * <p>
     * Автоматически генерируется MapStruct. Выполняет преобразование
     * всех полей сущности в соответствующие поля DTO:
     * </p>
     * <ul>
     * <li>{@code id} → {@code id} (идентификатор запроса)</li>
     * <li>{@code event} → {@code event} (идентификатор события)</li>
     * <li>{@code requester} → {@code requester} (идентификатор пользователя)</li>
     * <li>{@code status} → {@code status} (статус запроса)</li>
     * <li>{@code created} → {@code created} (дата создания с JSON-форматированием)</li>
     * </ul>
     *
     * @param request сущность запроса на участие для преобразования
     * @return DTO запроса на участие с данными для ответа API
     */
    ParticipationRequestDto toRequestDto(ParticipationRequest request);
}
