package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.event.dto.EventCommentDto;
import ru.practicum.event.entity.Event;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.entity.User;
import ru.practicum.user.mapper.UserMapper;

import java.time.LocalDateTime;

/**
 * Маппер для преобразования между сущностью комментария и DTO объектами.
 * <p>
 * Предоставляет методы для конвертации данных комментария между слоями приложения:
 * из сущности {@link Comment} в DTO для ответов API и из DTO в сущность для сохранения.
 * </p>
 * <p>
 * Реализован как интерфейс MapStruct с использованием статических методов,
 * что позволяет выполнять кастомный маппинг с явной логикой преобразования.
 * Регистрируется как Spring-компонент для возможности инъекции зависимостей.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Преобразует сущность комментария в DTO для ответа API.
     * <p>
     * Выполняет полное преобразование сущности {@link Comment} в {@link CommentResponseDto},
     * включая вложенные преобразования автора и события через соответствующие мапперы.
     * </p>
     * <p>
     * Процесс преобразования:
     * <ol>
     *   <li>Преобразует автора комментария из {@link User} в {@link UserShortDto}
     *       с помощью {@link UserMapper#toUserForEventShotDto(User)}</li>
     *   <li>Преобразует событие из {@link Event} в {@link EventCommentDto}
     *       с помощью {@link EventMapper#toEventCommentDto(Event)}</li>
     *   <li>Создает {@link CommentResponseDto} с использованием всех преобразованных данных</li>
     * </ol>
     * </p>
     *
     * @param comment сущность комментария для преобразования (не должна быть {@code null})
     * @return DTO комментария с полной информацией для ответа API
     * @throws NullPointerException если {@code comment}, {@code comment.getAuthorName()}
     *         или {@code comment.getEvent()} являются {@code null}
     */
    static CommentResponseDto toCommentResponseDto(Comment comment) {
        UserShortDto authorName = UserMapper.toUserForEventShotDto(comment.getAuthorName());
        EventCommentDto commentEvent = EventMapper.toEventCommentDto(comment.getEvent());
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                authorName,
                commentEvent,
                comment.getCreated());
    }

    /**
     * Создает сущность комментария из DTO и связанных сущностей.
     * <p>
     * Используется при создании нового комментария. Собирает сущность {@link Comment}
     * из DTO запроса {@link CommentDto} и связанных сущностей пользователя и события.
     * </p>
     * <p>
     * Особенности создания:
     * <ul>
     *   <li>ID устанавливается как {@code null} - будет сгенерирован базой данных</li>
     *   <li>Текст комментария берется из {@link CommentDto#getText()}</li>
     *   <li>Автор и событие устанавливаются из переданных сущностей</li>
     *   <li>Дата создания устанавливается как текущее время ({@link LocalDateTime#now()})</li>
     * </ul>
     * </p>
     * <p>
     * Этот метод предполагает, что пользователь и событие уже прошли валидацию
     * и существуют в системе.
     * </p>
     *
     * @param user пользователь-автор комментария (не должен быть {@code null})
     * @param event событие, к которому относится комментарий (не должен быть {@code null})
     * @param commentDto DTO с текстом комментария (не должен быть {@code null})
     * @return новая сущность комментария, готовая для сохранения в базе данных
     * @throws NullPointerException если любой из параметров является {@code null}
     */
    static Comment toComment(User user, Event event, CommentDto commentDto) {
        return new Comment(null, event, commentDto.getText(), user, LocalDateTime.now());
    }
}
