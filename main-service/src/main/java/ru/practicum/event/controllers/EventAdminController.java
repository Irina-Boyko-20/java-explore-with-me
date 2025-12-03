package ru.practicum.event.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.util.ApiPaths;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для административных операций с событиями.
 * <p>
 * Обеспечивает API для управления событиями администраторами системы.
 * Все endpoints требуют соответствующих прав доступа и доступны только
 * аутентифицированным пользователям с ролью администратора.
 * </p>
 *
 * <p>
 * Работает с событиями системы, предоставляя возможности:
 * </p>
 * <ul>
 * <li>Расширенный поиск событий с множественными фильтрами</li>
 * <li>Обновление событий и изменение их статуса (публикация/отклонение)</li>
 * </ul>
 *
 * <p>
 * Поддерживает сложную фильтрацию событий по различным критериям, что позволяет
 * администраторам эффективно управлять модерацией и мониторингом событий.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.ADMIN + ApiPaths.EVENTS)
@RequiredArgsConstructor
@Slf4j
public class EventAdminController {
    private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Сервис для работы с событиями.
     */
    public final EventService eventService;

    /**
     * Возвращает список событий с расширенной фильтрацией для администраторов.
     *
     * @param users список идентификаторов пользователей-инициаторов для фильтрации
     * @param states список состояний событий для фильтрации (PENDING, PUBLISHED, CANCELED)
     * @param categories список идентификаторов категорий для фильтрации
     * @param rangeStart начальная дата диапазона для фильтрации по дате проведения
     * @param rangeEnd конечная дата диапазона для фильтрации по дате проведения
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @return ResponseEntity со списком DTO событий и статусом 200 (OK).
     *         Если события не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.BadRequestException если указаны невалидные состояния событий
     */
    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return new ResponseEntity<>(
                eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size),
                HttpStatus.OK
        );
    }

    /**
     * Обновляет событие и изменяет его статус (публикация/отклонение).
     *
     * @param eventId идентификатор обновляемого события
     * @param dto DTO с данными для обновления и действием по изменению состояния
     * @return ResponseEntity с обновленным событием и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если событие с указанным ID не найдено
     * @throws ru.practicum.exception.ConflictException если действие над состоянием невозможно
     *         (например, попытка опубликовать уже опубликованное событие)
     */
    @PatchMapping(ApiPaths.EVENT_BY_ID)
    public ResponseEntity<EventFullDto> updateEventAndStatus(@PathVariable Long eventId,
                                                             @Valid @RequestBody UpdateEventAdminRequest dto) {
        return new ResponseEntity<>(eventService.updateAdminEvent(eventId, dto), HttpStatus.OK);
    }
}
