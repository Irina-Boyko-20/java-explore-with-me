package ru.practicum.comment.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.entity.Comment;

import java.util.List;

/**
 * Сервисный интерфейс для управления комментариями к событиям.
 * <p>
 * Определяет контракт для операций CRUD (Create, Read, Update, Delete)
 * с комментариями, а также для получения комментариев с учетом различных
 * критериев фильтрации и прав доступа.
 * </p>
 * <p>
 * Интерфейс разделяет методы по уровням доступа:
 * <ul>
 *   <li><strong>Публичные методы</strong> - доступны всем пользователям</li>
 *   <li><strong>Приватные методы</strong> - доступны только аутентифицированным пользователям</li>
 *   <li><strong>Административные методы</strong> - доступны только администраторам</li>
 * </ul>
 * </p>
 */
public interface CommentService {

    /**
     * Получает все комментарии указанного пользователя с пагинацией.
     * <p>
     *
     * @param userId идентификатор пользователя, чьи комментарии нужно получить
     *               (должен быть положительным числом)
     * @param from   начальный индекс (смещение) для пагинации (нумерация с 0,
     *               не должен быть отрицательным)
     * @param size   количество комментариев на странице (должен быть положительным)
     * @return список DTO комментариев пользователя, может быть пустым, если
     *         комментарии отсутствуют
     */
    List<CommentResponseDto> getAllCommentsByUser(Long userId, Integer from, Integer size);

    /**
     * Получает все комментарии к указанному событию с пагинацией.
     * <p>
     *
     * @param eventId              идентификатор события, для которого нужно
     *                             получить комментарии (должен быть положительным)
     * @param from                 начальный индекс для пагинации (не должен быть
     *                             отрицательным)
     * @param size                 количество комментариев на странице (должен быть
     *                             положительным)
     * @param httpServletRequest   HTTP-запрос для сбора статистики (например,
     *                             IP-адрес для подсчета просмотров)
     * @return список DTO комментариев к событию, может быть пустым
     * @throws ru.practicum.exception.NotFoundException если событие с указанным
     *         ID не найдено
     */
    List<CommentResponseDto> getAllCommentsByEvent(
            Long eventId,
            Integer from,
            Integer size,
            HttpServletRequest httpServletRequest
    );

    /**
     * Получает комментарий по идентификатору для административных целей.
     * <p>
     *
     * @param commentId идентификатор комментария (должен быть положительным)
     * @return DTO комментария с полной информацией
     * @throws ru.practicum.exception.NotFoundException если комментарий
     *         с указанным ID не найден
     */
    CommentResponseDto getAdminComment(Long commentId);

    /**
     * Удаляет комментарий по идентификатору.
     * <p>
     *
     * @param commentId идентификатор комментария для удаления (должен быть
     *                  положительным)
     * @throws ru.practicum.exception.NotFoundException если комментарий
     *         с указанным ID не найден
     */
    void deleteComment(Long commentId);

    /**
     * Добавляет новый комментарий к событию от имени пользователя.
     * <p>
     *
     * @param userId               идентификатор пользователя-автора (должен быть
     *                             положительным)
     * @param eventId              идентификатор события, к которому добавляется
     *                             комментарий (должен быть положительным)
     * @param dto                  DTO с текстом комментария (не должен быть {@code null},
     *                             текст должен проходить валидацию)
     * @param httpServletRequest   HTTP-запрос для сбора статистики
     * @return DTO созданного комментария
     * @throws jakarta.validation.ConstraintViolationException если параметры не
     *         проходят валидацию
     * @throws ru.practicum.exception.NotFoundException если пользователь или событие не найдены
     */
    CommentResponseDto add(Long userId, Long eventId, CommentDto dto, HttpServletRequest httpServletRequest);

    /**
     * Обновляет существующий комментарий пользователя.
     * <p>
     *
     * @param userId        идентификатор пользователя, выполняющего обновление
     *                      (должен быть положительным)
     * @param commentId     идентификатор комментария для обновления (должен быть
     *                      положительным)
     * @param newComment    DTO с новым текстом комментария (не должен быть {@code null},
     *                      текст должен проходить валидацию)
     * @return DTO обновленного комментария
     * @throws jakarta.validation.ConstraintViolationException если параметры не
     *         проходят валидацию
     * @throws ru.practicum.exception.NotFoundException если комментарий не найден
     */
    CommentResponseDto update(Long userId, Long commentId, CommentDto newComment);

    /**
     * Получает конкретный комментарий пользователя по идентификатору.
     * <p>
     *
     * @param userId    идентификатор пользователя (должен быть положительным)
     * @param commentId идентификатор комментария (должен быть положительным)
     * @return DTO комментария
     * @throws jakarta.validation.ConstraintViolationException если параметры не
     *         проходят валидацию
     * @throws ru.practicum.exception.NotFoundException если комментарий не найден
     */
    CommentResponseDto getUserComment(Long userId, Long commentId);

    /**
     * Проверяет существование комментария и возвращает его сущность.
     * <p>
     *
     * @param commentId идентификатор комментария для проверки (должен быть положительным)
     * @return сущность комментария, если он существует
     * @throws ru.practicum.exception.NotFoundException если комментарий не найден
     */
    Comment commentExists(Long commentId);
}
