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
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.NotFoundException;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Публичный контроллер для просмотра комментариев к событиям.
 * Предоставляет API endpoint для получения комментариев к конкретному событию.
 * Доступен всем пользователям без необходимости аутентификации.
 * Включает сбор статистики просмотров через HTTP запрос.
 */
@RestController
@RequestMapping(ApiPaths.EVENTS + ApiPaths.EVENT_BY_ID + ApiPaths.COMMENTS)
@RequiredArgsConstructor
public class CommentPublicController {
    private final CommentService commentService;

    /**
     * Получает все комментарии к указанному событию с пагинацией.
     * Публичный endpoint, доступный всем пользователям для просмотра комментариев
     * к опубликованным событиям. Увеличивает счетчик просмотров события и отправляет
     * статистику в микросервис статистики.
     *
     * @param eventId              идентификатор события
     * @param from                 начальная позиция в списке (по умолчанию 0)
     * @param size                 количество элементов на странице (по умолчанию 10)
     * @param httpServletRequest   HTTP запрос для сбора статистики просмотров
     * @return список комментариев к событию с HTTP статусом 200 OK
     * @throws NotFoundException если событие с указанным ID не найдено
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> getAllComments(@PathVariable Long eventId,
                                                                   @RequestParam(defaultValue = "0") Integer from,
                                                                   @RequestParam(defaultValue = "10") Integer size,
                                                                   HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(
                commentService.getAllCommentsByEvent(eventId, from, size, httpServletRequest),
                HttpStatus.OK
        );
    }
}
