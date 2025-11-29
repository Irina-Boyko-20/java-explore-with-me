package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.entity.Event;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с событиями.
 * <p>
 * Предоставляет комплексную бизнес-логику для операций с событиями на всех уровнях доступа:
 * административном, пользовательском и публичном. Обеспечивает валидацию данных,
 * проверку прав доступа, учет статистики и обработку исключительных ситуаций.
 * </p>
 *
 * <p>
 * Служит единой точкой входа для всех операций с событиями, инкапсулируя
 * сложную бизнес-логику и гарантируя целостность данных событий.
 * </p>
 */
public interface EventService {

    /**
     * Возвращает список событий для административного интерфейса с расширенной фильтрацией.
     *
     * @param users список идентификаторов пользователей-инициаторов для фильтрации
     * @param states список состояний событий для фильтрации (PENDING, PUBLISHED, CANCELED)
     * @param categories список идентификаторов категорий для фильтрации
     * @param rangeStart начальная дата диапазона для фильтрации по дате проведения
     * @param rangeEnd конечная дата диапазона для фильтрации по дате проведения
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @return список полных DTO событий, соответствующих критериям фильтрации.
     *         Если события не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.BadRequestException если указаны невалидные состояния событий
     */
    List<EventFullDto> getAdminEvents(List<Long> users,
                                      List<String> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from,
                                      Integer size
    );

    /**
     * Обновляет событие и изменяет его статус администратором.
     *
     * @param eventId идентификатор обновляемого события
     * @param dto DTO с данными для обновления и действием по изменению состояния
     * @return полное DTO обновленного события
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     * @throws ru.practicum.exception.ConflictException если действие над состоянием невозможно
     *         или дата события менее чем через 1 час от публикации
     */
    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest dto);

    /**
     * Возвращает список событий для публичного доступа с расширенной фильтрацией.
     *
     * @param text текст для поиска в аннотации, описании и заголовке события
     * @param categories список идентификаторов категорий для фильтрации
     * @param paid флаг платности события
     * @param rangeStart начальная дата диапазона для фильтрации по дате проведения
     * @param rangeEnd конечная дата диапазона для фильтрации по дате проведения
     * @param onlyAvailable флаг показа только событий с доступными местами
     * @param sort критерий сортировки (EVENT_DATE, VIEWS)
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @param httpServletRequest объект HTTP запроса для учета статистики
     * @return список кратких DTO событий, соответствующих критериям фильтрации.
     *         Если события не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.BadRequestException если указан невалидный критерий сортировки
     */
    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  String sort,
                                  Integer from,
                                  Integer size,
                                  HttpServletRequest httpServletRequest
    );

    /**
     * Возвращает детальную информацию о событии для публичного доступа.
     *
     * @param eventId идентификатор запрашиваемого события
     * @param httpServletRequest объект HTTP запроса для учета статистики
     * @return полное DTO события
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     * @throws ru.practicum.exception.ConflictException если событие не опубликовано
     */
    EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest);


    /**
     * Возвращает список событий пользователя с пагинацией.
     *
     * @param userId идентификатор пользователя
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @return список полных DTO событий пользователя.
     *         Если события не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.NotFoundException если пользователь не найден
     */
    List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size);

    /**
     * Создает новое событие от имени пользователя.
     *
     * @param userId идентификатор пользователя-создателя
     * @param dto DTO с данными для создания события
     * @return полное DTO созданного события
     * @throws ru.practicum.exception.NotFoundException если пользователь или категория не найдены
     * @throws ru.practicum.exception.ConflictException если дата события менее чем через 2 часа
     */
    EventFullDto add(Long userId, NewEventDto dto);

    /**
     * Возвращает детальную информацию о конкретном событии пользователя.
     *
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @return полное DTO события
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     */
    EventFullDto getUserEventById(Long userId, Long eventId);

    /**
     * Обновляет существующее событие пользователя.
     *
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @param dto DTO с данными для обновления
     * @return полное DTO обновленного события
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     * @throws ru.practicum.exception.ConflictException если событие опубликовано или дата менее чем через 2 часа
     */
    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

    /**
     * Возвращает список запросов на участие в конкретном событии пользователя.
     *
     * @param userId идентификатор пользователя-создателя события
     * @param eventId идентификатор события
     * @return список DTO запросов на участие.
     *         Если запросы не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     */
    List<ParticipationRequestDto> getRequestsEventByUser(Long userId, Long eventId);

    /**
     * Изменяет статусы запросов на участие в событии пользователя.
     *
     * @param userId идентификатор пользователя-создателя события
     * @param eventId идентификатор события
     * @param dto DTO с идентификаторами запросов и целевым статусом
     * @return результат обновления статусов с разделением на подтвержденные и отклоненные запросы
     * @throws ru.practicum.exception.NotFoundException если событие, пользователь или запросы не найдены
     * @throws ru.practicum.exception.ConflictException если превышен лимит участников
     */
    EventRequestStatusUpdateResult updateStatusEventByUser(Long userId,
                                                           Long eventId,
                                                           EventRequestStatusUpdateRequest dto);

    /**
     * Проверяет существование события по идентификатору.
     *
     * @param eventId идентификатор события для проверки
     * @return сущность события, если найдена
     * @throws ru.practicum.exception.NotFoundException если событие с указанным ID не найдено
     */
    Event eventExists(Long eventId);
}
