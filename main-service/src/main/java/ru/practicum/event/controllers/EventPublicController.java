package ru.practicum.event.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.util.ApiPaths;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Публичный контроллер для работы с событиями.
 * <p>
 * Предоставляет API для получения информации о событиях всем пользователям
 * без необходимости аутентификации. Предназначен для отображения событий
 * на публичных страницах приложения, поиска и просмотра детальной информации.
 * </p>
 *
 * <p>
 * Обеспечивает возможности для:
 * </p>
 * <ul>
 * <li>Поиска событий с расширенной фильтрацией и сортировкой</li>
 * <li>Просмотра детальной информации о конкретном событии</li>
 * <li>Автоматического учета статистики просмотров</li>
 * </ul>
 *
 * <p>
 * Все методы учитывают статистику просмотров и доступны без ограничений
 * для любых пользователей, включая неаутентифицированных.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.EVENTS)
@RequiredArgsConstructor
public class EventPublicController {
    private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Сервис для работы с событиями.
     */
    public final EventService eventService;

    /**
     * Возвращает список событий с расширенной фильтрацией для публичного доступа.
     *
     * @param text текст для поиска в аннотации, описании и заголовке события
     * @param categories список идентификаторов категорий для фильтрации
     * @param paid флаг платности события (true - платные, false - бесплатные)
     * @param rangeStart начальная дата диапазона для фильтрации по дате проведения
     * @param rangeEnd конечная дата диапазона для фильтрации по дате проведения
     * @param onlyAvailable флаг показа только событий с доступными местами
     * @param sort критерий сортировки (EVENT_DATE - по дате, VIEWS - по просмотрам)
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @param httpServletRequest объект HTTP запроса для учета статистики просмотров
     * @return ResponseEntity со списком кратких DTO событий и статусом 200 (OK).
     *         Если события не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.BadRequestException если указан невалидный критерий сортировки
     */
    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = timeFormat) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest httpServletRequest
    ) {
        return new ResponseEntity<>(eventService.getEvents(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size,
                httpServletRequest
        ), HttpStatus.OK);
    }

    /**
     * Возвращает детальную информацию о конкретном событии.
     *
     * @param eventId идентификатор запрашиваемого события
     * @param httpServletRequest объект HTTP запроса для учета статистики просмотров
     * @return ResponseEntity с полным DTO события и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     * @throws ru.practicum.exception.ConflictException если событие не опубликовано
     */
    @GetMapping(ApiPaths.EVENT_BY_ID)
    public ResponseEntity<EventFullDto> getEventById(@PathVariable Long eventId,
                                                     HttpServletRequest httpServletRequest) {
        return new ResponseEntity<>(eventService.getEventById(eventId, httpServletRequest), HttpStatus.OK);
    }
}
