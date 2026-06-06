package ru.practicum.comment.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Административный контроллер для управления комментариями.
 * Предоставляет API endpoints для административных операций с комментариями:
 * получение комментариев пользователей и событий, просмотр и удаление комментариев.
 * Все endpoints доступны только пользователям с административными правами.
 */
@RestController
@RequestMapping(ApiPaths.ADMIN + ApiPaths.COMMENTS)
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    /**
     * Получает все комментарии указанного пользователя с пагинацией.
     * Endpoint предназначен для административного просмотра комментариев пользователя.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная позиция в списке (по умолчанию 0)
     * @param size   количество элементов на странице (по умолчанию 10)
     * @return список комментариев пользователя с HTTP статусом 200 OK
     */
    @GetMapping(ApiPaths.USERS + ApiPaths.USER_BY_ID)
    public ResponseEntity<List<CommentResponseDto>> getAllCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(commentService.getAllCommentsByUser(userId, from, size), HttpStatus.OK);
    }

    /**
     * Получает все комментарии к указанному событию с пагинацией.
     * Endpoint доступен публично, но требует отправки HTTP запроса для сбора статистики.
     *
     * @param eventId              идентификатор события
     * @param from                 начальная позиция в списке (по умолчанию 0)
     * @param size                 количество элементов на странице (по умолчанию 10)
     * @param httpServletRequest   HTTP запрос для сбора статистики просмотров
     * @return список комментариев к событию с HTTP статусом 200 OK
     */
    @GetMapping(ApiPaths.EVENTS + ApiPaths.EVENT_BY_ID)
    public ResponseEntity<List<CommentResponseDto>> getAllCommentsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest
    ) {
        return new ResponseEntity<>(
                commentService.getAllCommentsByEvent(eventId, from, size, httpServletRequest),
                HttpStatus.OK
        );
    }

    /**
     * Получает конкретный комментарий по его идентификатору.
     * Административный endpoint для просмотра отдельного комментария.
     *
     * @param commentId идентификатор комментария
     * @return комментарий с HTTP статусом 200 OK
     */
    @GetMapping(ApiPaths.COMMENTS_BY_ID)
    public ResponseEntity<CommentResponseDto> getCommentById(@PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.getAdminComment(commentId), HttpStatus.OK);
    }

    /**
     * Удаляет комментарий по его идентификатору.
     * Административный endpoint для удаления комментариев (модерация контента).
     *
     * @param commentId идентификатор комментария для удаления
     */
    @DeleteMapping(ApiPaths.COMMENTS_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
