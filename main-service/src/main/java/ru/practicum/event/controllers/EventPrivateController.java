package ru.practicum.event.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Приватный контроллер для работы с событиями от лица пользователя.
 * <p>
 * Обеспечивает API для управления собственными событиями пользователями.
 * Все endpoints требуют аутентификации и доступны только для авторизованных пользователей,
 * причем пользователь может работать только со своими событиями и связанными с ними запросами.
 * </p>
 *
 * <p>
 * Работает с событиями пользователя, предоставляя возможности:
 * </p>
 * <ul>
 * <li>Просмотр собственных событий</li>
 * <li>Создание новых событий</li>
 * <li>Просмотр и редактирование конкретного события</li>
 * <li>Управление запросами на участие в своих событиях</li>
 * <li>Изменение статусов запросов на участие</li>
 * </ul>
 *
 * <p>
 * Все методы выполняют проверку прав доступа, гарантируя что пользователь
 * работает только со своими данными.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.USERS + ApiPaths.USER_BY_ID + ApiPaths.EVENTS)
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {

    /**
     * Сервис для работы с событиями.
     */
    public final EventService eventService;

    /**
     * Возвращает список событий текущего пользователя с пагинацией.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @return ResponseEntity со списком DTO событий пользователя и статусом 200 (OK).
     *         Если события не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.NotFoundException если пользователь не найден
     */
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(eventService.getUserEvents(userId, from, size), HttpStatus.OK);
    }

    /**
     * Создает новое событие от имени пользователя.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param dto DTO с данными для создания события
     * @return ResponseEntity с созданным событием и статусом 201 (Created)
     * @throws ru.practicum.exception.NotFoundException если пользователь или категория не найдены
     * @throws ru.practicum.exception.ConflictException если дата события менее чем через 2 часа
     */
    @PostMapping
    public ResponseEntity<EventFullDto> addEvent(@PathVariable Long userId,
                                                 @Valid @RequestBody NewEventDto dto) {
        return new ResponseEntity<>(eventService.add(userId, dto), HttpStatus.CREATED);
    }

    /**
     * Возвращает детальную информацию о конкретном событии пользователя.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param eventId идентификатор запрашиваемого события
     * @return ResponseEntity с DTO события и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     */
    @GetMapping(ApiPaths.EVENT_BY_ID)
    public ResponseEntity<EventFullDto> getEventByUser(@PathVariable Long userId,
                                                       @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.getUserEventById(userId, eventId), HttpStatus.OK);
    }

    /**
     * Обновляет существующее событие пользователя.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param eventId идентификатор обновляемого события
     * @param dto DTO с данными для обновления
     * @return ResponseEntity с обновленным событием и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     * @throws ru.practicum.exception.ConflictException если событие опубликовано или дата менее чем через 2 часа
     */
    @PatchMapping(ApiPaths.EVENT_BY_ID)
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequest dto) {
        return new ResponseEntity<>(eventService.updateUserEvent(userId, eventId, dto), HttpStatus.OK);
    }

    /**
     * Возвращает список запросов на участие в конкретном событии пользователя.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param eventId идентификатор события
     * @return ResponseEntity со списком DTO запросов на участие и статусом 200 (OK).
     *         Если запросы не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     */
    @GetMapping(ApiPaths.EVENT_BY_ID + ApiPaths.REQUESTS)
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByUser(@PathVariable Long userId,
                                                                           @PathVariable Long eventId) {
        return new ResponseEntity<>(eventService.getRequestsEventByUser(userId, eventId), HttpStatus.OK);
    }

    /**
     * Изменяет статусы запросов на участие в событии пользователя.
     *
     * @param userId идентификатор текущего пользователя (из path variable)
     * @param eventId идентификатор события
     * @param dto DTO с идентификаторами запросов и целевым статусом
     * @return ResponseEntity с результатом обновления статусов и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если событие, пользователь или запросы не найдены
     * @throws ru.practicum.exception.ConflictException если превышен лимит участников
     */
    @PatchMapping(ApiPaths.EVENT_BY_ID + ApiPaths.REQUESTS)
    public ResponseEntity<EventRequestStatusUpdateResult> updateStatusRequestsEventByUser(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest dto
    ) {
        return new ResponseEntity<>(eventService.updateStatusEventByUser(userId, eventId, dto), HttpStatus.OK);
    }
}
