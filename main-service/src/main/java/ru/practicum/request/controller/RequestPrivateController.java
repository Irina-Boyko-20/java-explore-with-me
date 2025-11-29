package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.ParticipationRequestService;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Приватный контроллер для работы с запросами на участие в событиях от лица пользователя.
 * <p>
 * Обеспечивает API для управления собственными запросами на участие пользователями.
 * Все endpoints требуют аутентификации и доступны только для авторизованных пользователей,
 * причем пользователь может работать только со своими запросами на участие.
 * </p>
 *
 * <p>
 * Работает с запросами на участие пользователя, предоставляя возможности:
 * </p>
 * <ul>
 * <li>Просмотр собственных запросов на участие</li>
 * <li>Создание новых запросов на участие в событиях</li>
 * <li>Отмена собственных запросов на участие</li>
 * </ul>
 *
 * <p>
 * Все методы выполняют проверку прав доступа, гарантируя что пользователь
 * работает только со своими данными.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.USERS + ApiPaths.USER_BY_ID + ApiPaths.REQUESTS)
@RequiredArgsConstructor
public class RequestPrivateController {

    /**
     * Сервис для работы с запросами на участие.
     */
    public final ParticipationRequestService requestService;

    /**
     * Возвращает список запросов на участие текущего пользователя.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @return ResponseEntity со списком DTO запросов на участие и статусом 200 (OK).
     *         Если запросы не найдены, возвращается пустой список.
     */
    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(@PathVariable Long userId) {
        return new ResponseEntity<>(requestService.get(userId), HttpStatus.OK);
    }

    /**
     * Создает новый запрос на участие в указанном событии.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param eventId идентификатор события, в котором пользователь хочет участвовать
     * @return ResponseEntity с созданным запросом на участие и статусом 201 (Created)
     * @throws ru.practicum.exception.ConflictException если пользователь уже подавал запрос на это событие,
     *         или если пользователь является инициатором события, или событие не опубликовано,
     *         или достигнут лимит участников
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     */
    @PostMapping
    public ResponseEntity<ParticipationRequestDto> addRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        return new ResponseEntity<>(requestService.add(userId, eventId), HttpStatus.CREATED);
    }

    /**
     * Отменяет собственный запрос на участие.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param requestId идентификатор запроса на участие для отмены
     * @return ResponseEntity с отмененным запросом на участие и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если запрос не найден
     * @throws ru.practicum.exception.ConflictException если запрос уже обработан (подтвержден или отклонен)
     */
    @PatchMapping(ApiPaths.REQUESTS_BY_ID + "/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable Long userId,
                                                                 @PathVariable Long requestId) {
        return new ResponseEntity<>(requestService.cancel(userId, requestId), HttpStatus.OK);
    }
}
