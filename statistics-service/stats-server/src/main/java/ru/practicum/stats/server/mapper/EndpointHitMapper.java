package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

/**
 * Интерфейс для маппинга объектов EndpointHitDto в EndpointHit.
 * Использует MapStruct для автоматической генерации реализации,
 * интегрированной с Spring (componentModel = "spring").
 */
@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    /**
     * Преобразует объект EndpointHitDto в объект EndpointHit.
     * Этот метод выполняет маппинг полей из DTO в сущность для дальнейшего использования в сервисе статистики.
     *
     * @param hitDto объект DTO, содержащий данные о хите эндпоинта
     * @return объект EndpointHit, представляющий сущность хита эндпоинта для сохранения в БД
     */
    EndpointHit toEndpointHit(EndpointHitDto hitDto);
}
