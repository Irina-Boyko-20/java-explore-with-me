package ru.practicum.comment.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Приватный контроллер для управления комментариями пользователей.
 * Предоставляет API endpoints для операций с комментариями, доступными
 * аутентифицированным пользователям: создание, редактирование и просмотр
 * собственных комментариев.
 * Все endpoints требуют аутентификации и авторизации пользователя.
 */
@RestController
@RequestMapping(ApiPaths.USERS + ApiPaths.USER_BY_ID)
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;

    /**
     * Создает новый комментарий к событию от имени пользователя.
     * Endpoint доступен только аутентифицированным пользователям для оставления
     * комментариев к опубликованным событиям.
     *
     * @param userId               идентификатор пользователя-автора
     * @param eventId              идентификатор события, к которому добавляется комментарий
     * @param dto                  DTO с текстом комментария (проходит валидацию)
     * @param httpServletRequest   HTTP запрос для сбора статистики
     * @return созданный комментарий с HTTP статусом 201 CREATED
     */
    @PostMapping(ApiPaths.EVENTS + ApiPaths.EVENT_BY_ID + ApiPaths.COMMENTS)
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @Valid @RequestBody CommentDto dto,
                                                         HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(commentService.add(userId, eventId, dto, httpServletRequest), HttpStatus.CREATED);
    }

    /**
     * Обновляет существующий комментарий пользователя.
     * Endpoint позволяет пользователю редактировать текст своего комментария.
     * Проверяет, что пользователь является автором редактируемого комментария.
     *
     * @param userId        идентификатор пользователя, выполняющего обновление
     * @param commentId     идентификатор комментария для обновления
     * @param newComment    DTO с новым текстом комментария (проходит валидацию)
     * @return обновленный комментарий с HTTP статусом 200 OK
     * @throws ForbiddenException если пользователь не является автором комментария
     */
    @PatchMapping(ApiPaths.COMMENTS + ApiPaths.COMMENTS_BY_ID)
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable Long userId,
                                                            @PathVariable Long commentId,
                                                            @Valid @RequestBody CommentDto newComment) {
        return new ResponseEntity<>(commentService.update(userId, commentId, newComment), HttpStatus.OK);
    }

    /**
     * Получает конкретный комментарий пользователя.
     * Endpoint позволяет пользователю просматривать свой отдельный комментарий.
     * Проверяет, что пользователь является автором запрашиваемого комментария.
     *
     * @param userId    идентификатор пользователя
     * @param commentId идентификатор комментария
     * @return комментарий пользователя с HTTP статусом 200 OK
     * @throws ForbiddenException если пользователь не является автором комментария
     */
    @GetMapping(ApiPaths.COMMENTS + ApiPaths.COMMENTS_BY_ID)
    public ResponseEntity<CommentResponseDto> getComment(@PathVariable Long userId, @PathVariable Long commentId) {
        return new ResponseEntity<>(commentService.getUserComment(userId, commentId), HttpStatus.OK);
    }

    /**
     * Получает все комментарии пользователя с пагинацией.
     * Endpoint позволяет пользователю просматривать историю своих комментариев.
     *
     * @param userId идентификатор пользователя
     * @param from   начальная позиция в списке (по умолчанию 0)
     * @param size   количество элементов на странице (по умолчанию 10)
     * @return список комментариев пользователя с HTTP статусом 200 OK
     */
    @GetMapping(ApiPaths.COMMENTS)
    public ResponseEntity<List<CommentResponseDto>> getAllCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(commentService.getAllCommentsByUser(userId, from, size), HttpStatus.OK);
    }
}
