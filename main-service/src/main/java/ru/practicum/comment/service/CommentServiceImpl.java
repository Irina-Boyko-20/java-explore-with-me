package ru.practicum.comment.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.entity.Comment;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.entity.User;
import ru.practicum.user.service.UserService;

import java.util.List;

/**
 * Сервис для управления комментариями к событиям.
 * Реализует бизнес-логику работы с комментариями: создание, получение,
 * обновление и удаление комментариев с учетом прав доступа пользователей.
 * Интегрируется с сервисами пользователей, событий и микросервисом статистики.
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper mapper;
    private final UserService userService;
    private final EventService eventService;
    private final StatsClient statsClient;

    /**
     * Получает все комментарии указанного пользователя с пагинацией.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная позиция пагинации
     * @param size   количество элементов на странице
     * @return список комментариев пользователя в формате DTO
     */
    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseDto> getAllCommentsByUser(Long userId, Integer from, Integer size) {
        userService.userExists(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findByAuthorNameId(userId, pageRequest);

        return comments.stream()
                .map(mapper::toCommentResponseDto)
                .toList();
    }

    /**
     * Получает все комментарии к указанному событию с пагинацией.
     * Увеличивает счетчик просмотров события и отправляет статистику.
     *
     * @param eventId              идентификатор события
     * @param from                 начальная позиция пагинации
     * @param size                 количество элементов на странице
     * @param httpServletRequest   HTTP запрос для сбора статистики
     * @return список комментариев к событию в формате DTO
     */
    @Transactional(readOnly = true)
    @Override
    public List<CommentResponseDto> getAllCommentsByEvent(Long eventId,
                                                          Integer from, Integer size,
                                                          HttpServletRequest httpServletRequest) {
        Event event = eventService.eventExists(eventId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findByEventId(eventId, pageRequest);
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        statsClient.saveHit(httpServletRequest);

        return comments.stream()
                .map(mapper::toCommentResponseDto)
                .toList();
    }

    /**
     * Получает комментарий по идентификатору для административных целей.
     *
     * @param commentId идентификатор комментария
     * @return комментарий в формате DTO
     */
    @Transactional(readOnly = true)
    @Override
    public CommentResponseDto getAdminComment(Long commentId) {
        Comment comment = commentExists(commentId);

        return mapper.toCommentResponseDto(comment);
    }

    /**
     * Удаляет комментарий по идентификатору.
     *
     * @param commentId идентификатор комментария для удаления
     */
    @Override
    public void deleteComment(Long commentId) {
        Comment comment = commentExists(commentId);
        commentRepository.delete(comment);
    }

    /**
     * Добавляет новый комментарий к событию от имени пользователя.
     * Увеличивает счетчик комментариев у события.
     *
     * @param userId               идентификатор пользователя-автора
     * @param eventId              идентификатор события
     * @param dto                  DTO с текстом комментария
     * @param httpServletRequest   HTTP запрос для сбора статистики
     * @return созданный комментарий в формате DTO
     */
    @Override
    public CommentResponseDto add(Long userId, Long eventId, CommentDto dto, HttpServletRequest httpServletRequest) {
        User user = userService.userExists(userId);
        Event event = eventService.eventExists(eventId);
        Comment comment = mapper.toComment(user, event, dto);

        event.setComments(event.getComments() + 1);
        eventRepository.save(event);

        return mapper.toCommentResponseDto(commentRepository.save(comment));
    }

    /**
     * Обновляет существующий комментарий пользователя.
     * Проверяет, что пользователь является автором комментария.
     *
     * @param userId        идентификатор пользователя
     * @param commentId     идентификатор комментария
     * @param newComment    DTO с новым текстом комментария
     * @return обновленный комментарий в формате DTO
     * @throws ForbiddenException если пользователь не является автором комментария
     */
    @Transactional
    @Override
    public CommentResponseDto update(Long userId, Long commentId, CommentDto newComment) {
        userService.userExists(userId);
        Comment comment = commentExists(commentId);
        if (!userId.equals(comment.getAuthorName().getId())) {
            throw new ForbiddenException("Only the author of the comment can edit");
        }

        comment.setText(newComment.getText());

        return mapper.toCommentResponseDto(commentRepository.save(comment));
    }

    /**
     * Получает конкретный комментарий пользователя по идентификатору.
     * Проверяет, что пользователь является автором комментария.
     *
     * @param userId    идентификатор пользователя
     * @param commentId идентификатор комментария
     * @return комментарий в формате DTO
     * @throws ForbiddenException если пользователь не является автором комментария
     */
    @Transactional(readOnly = true)
    @Override
    public CommentResponseDto getUserComment(Long userId, Long commentId) {
        userService.userExists(userId);
        Comment comment = commentExists(commentId);
        if (!userId.equals(comment.getAuthorName().getId())) {
            throw new ForbiddenException("Only the author of the comment or" +
                    " the administrator can view the comment with ID= %d".formatted(commentId));
        }

        return mapper.toCommentResponseDto(comment);
    }

    /**
     * Проверяет существование комментария и возвращает его сущность.
     *
     * @param commentId идентификатор комментария
     * @return сущность комментария
     * @throws NotFoundException если комментарий не найден
     */
    @Override
    public Comment commentExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=%d was not found".formatted(commentId)));
    }
}
