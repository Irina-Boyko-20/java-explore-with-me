package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.category.entity.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.event.dto.EventCommentDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;
import ru.practicum.user.mapper.UserMapper;

/**
 * Маппер для преобразования между сущностью события и DTO.
 * <p>
 * Обеспечивает конвертацию данных между слоями приложения:
 * из сущности {@link Event} в различные DTO представления для возврата в API.
 * Использует MapStruct для автоматической генерации кода преобразования.
 * </p>
 *
 * <p>
 * Содержит как методы, генерируемые MapStruct автоматически, так и статические
 * методы для ручного маппинга в специфических сценариях, требующих кастомной логики.
 * </p>
 *
 * <p>
 * Автоматически регистрируется как Spring-компонент благодаря аннотации
 * {@code componentModel = "spring"}, что позволяет использовать инъекцию зависимостей.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    /**
     * Преобразует сущность события в краткое DTO представление.
     * <p>
     * Автоматически генерируется MapStruct. Выполняет преобразование
     * сущности {@link Event} в {@link EventShortDto} для использования
     * в списках событий, подборках и других сценариях, где не требуется
     * полная информация о событии.
     * </p>
     *
     * @param event сущность события для преобразования
     * @return краткое DTO события для использования в списках и предпросмотрах
     */
    EventShortDto toShortDto(Event event);

    /**
     * Преобразует сущность события в полное DTO представление.
     * <p>
     * Автоматически генерируется MapStruct. Выполняет преобразование
     * сущности {@link Event} в {@link EventFullDto} для использования
     * на детальных страницах события и в административных интерфейсах.
     * </p>
     *
     * @param event сущность события для преобразования
     * @return полное DTO события со всеми атрибутами и мета-данными
     */
    EventFullDto toEventFullDto(Event event);

    /**
     * Преобразует сущность события в краткое DTO представление с ручным маппингом.
     * <p>
     * Специализированный метод для создания краткого DTO события с явным
     * указанием логики преобразования. Используется в сценариях, где
     * требуется кастомная логика маппинга или когда автоматическое
     * преобразование MapStruct недостаточно.
     * </p>
     * <p>
     * Особенности реализации:
     * </p>
     * <ul>
     * <li>Использует {@link CategoryMapper#toCategoryForEventShotDto(Category)}
     *     для преобразования категории</li>
     * <li>Использует {@link UserMapper#toUserForEventShotDto(User)}
     *     для преобразования инициатора</li>
     * <li>Устанавливает значение 0 для поля views (требует отдельного расчета)</li>
     * <li>Игнорирует поле локации для краткого представления</li>
     * </ul>
     *
     * @param event сущность события для преобразования
     * @return краткое DTO события с ручным маппингом полей
     */
    static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryForEventShotDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                UserMapper.toUserForEventShotDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                0L
        );
    }

    /**
     * Преобразует сущность события в DTO для комментариев с ручным маппингом.
     * <p>
     * Специализированный метод для создания {@link EventCommentDto} - DTO события,
     * предназначенного для использования в контексте комментариев или там,
     * где требуется минимальная информация о событии без статистических данных.
     * </p>
     * <p>
     * Особенности преобразования:
     * </p>
     * <ul>
     * <li>Использует {@link CategoryMapper#toCategoryForEventShotDto(Category)}
     *     для преобразования категории в соответствующее DTO представление</li>
     * <li>Использует {@link UserMapper#toUserForEventShotDto(User)}
     *     для преобразования инициатора в краткое DTO пользователя</li>
     * <li>Исключает статистические поля (confirmedRequests, views), так как они
     *     не требуются в контексте комментариев</li>
     * <li>Сохраняет только базовую информацию о событии: идентификатор, аннотацию,
     *     категорию, дату проведения, инициатора, платность и заголовок</li>
     * </ul>
     *
     * @param event сущность события для преобразования
     * @return DTO события для использования в контексте комментариев
     */
    EventCommentDto toEventCommentDto(Event event);
}
