package ru.practicum.stats.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
     * Преобразует объект EndpointHit в объект EndpointHitDto.
     * Этот метод выполняет маппинг полей из сущности в DTO для дальнейшего использования в сервисе статистики.
     *
     * @param hit объект EndpointHit, представляющий сущность хита эндпоинта для сохранения в БД
     * @return объект DTO, содержащий данные о хите эндпоинта
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "app", source = "app")
    @Mapping(target = "uri", source = "uri")
    @Mapping(target = "ip", source = "ip")
    @Mapping(target = "timestamp", source = "timestamp")
    EndpointHitDto toEndpointHitDto(EndpointHit hit);
}
