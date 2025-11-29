package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.StatsClient;
import ru.practicum.category.entity.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventStateAction;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.entity.EventSort;
import ru.practicum.event.entity.Location;
import ru.practicum.event.entity.EventRequestStatus;
import ru.practicum.event.filterStrategy.UserFilterStrategy;
import ru.practicum.event.filterStrategy.StateFilterStrategy;
import ru.practicum.event.filterStrategy.CategoryFilterStrategy;
import ru.practicum.event.filterStrategy.DateRangeFilterStrategy;
import ru.practicum.event.filterStrategy.TextFilterStrategy;
import ru.practicum.event.filterStrategy.PaidFilterStrategy;
import ru.practicum.event.filterStrategy.EventFilterStrategy;
import ru.practicum.event.filterStrategy.AvailableFilterStrategy;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.entity.ParticipationRequest;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.entity.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Реализация сервиса для работы с событиями.
 * <p>
 * Предоставляет конкретную реализацию бизнес-логики для операций с событиями
 * на всех уровнях доступа. Использует стратегии фильтрации для реализации
 * сложных сценариев поиска, обеспечивает целостность данных и соблюдение
 * бизнес-правил при работе с событиями.
 * </p>
 *
 * <p>
 * Взаимодействует с репозиториями, сервисами, мапперами и внешними клиентами
 * для предоставления комплексной функциональности управления событиями.
 * </p>
 */
@Service
@Slf4j //возможно потом убрать или дополнить логами
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    public final EventRepository eventRepository;
    public final ParticipationRequestRepository requestRepository;
    public final CategoryService categoryService;
    public final UserService userService;
    public final EventMapper eventMapper;
    public final ParticipationRequestMapper requestMapper;
    private final UserFilterStrategy userFilterStrategy;
    private final StateFilterStrategy stateFilterStrategy;
    private final CategoryFilterStrategy categoryFilterStrategy;
    private final DateRangeFilterStrategy dateRangeFilterStrategy;
    private final TextFilterStrategy textFilterStrategy;
    private final PaidFilterStrategy paidFilterStrategy;
    private final AvailableFilterStrategy onlyAvailableFilterStrategy;
    private final StatsClient statsClient;

    /**
     * Возвращает список событий для административного интерфейса с расширенной фильтрацией.
     * <p>
     * Использует комбинацию стратегий фильтрации для построения сложных запросов
     * к базе данных. Поддерживает фильтрацию по пользователям, состояниям, категориям
     * и временному диапазону с пагинацией и сортировкой по дате создания.
     * </p>
     *
     * @param users список идентификаторов пользователей-инициаторов для фильтрации
     * @param states список состояний событий для фильтрации
     * @param categories список идентификаторов категорий для фильтрации
     * @param rangeStart начальная дата диапазона для фильтрации по дате проведения
     * @param rangeEnd конечная дата диапазона для фильтрации по дате проведения
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @return список полных DTO событий, соответствующих критериям фильтрации
     * @throws BadRequestException если параметры пагинации некорректны
     */
    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users,
                                             List<String> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Integer from,
                                             Integer size) {
        if (from < 0) {
            throw new BadRequestException("Parameter 'from' must be non-negative.");
        }
        if (size <= 0) {
            throw new BadRequestException("Parameter 'size' must be positive.");
        }

        List<EventFilterStrategy> strategies = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            strategies.add(userFilterStrategy);
        }
        if (states != null && !states.isEmpty()) {
            strategies.add(stateFilterStrategy);
        }
        if (categories != null && !categories.isEmpty()) {
            strategies.add(categoryFilterStrategy);
        }

        strategies.add(dateRangeFilterStrategy);

        Specification<Event> spec = Specification.where(null);
        for (EventFilterStrategy strategy : strategies) {
            if (strategy instanceof DateRangeFilterStrategy) {
                spec = spec.and(strategy.apply(rangeStart, rangeEnd));
            } else if (strategy instanceof UserFilterStrategy) {
                spec = spec.and(strategy.apply(users));
            } else if (strategy instanceof StateFilterStrategy) {
                spec = spec.and(strategy.apply(states));
            } else if (strategy instanceof CategoryFilterStrategy) {
                spec = spec.and(strategy.apply(categories));
            }
        }

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("createdOn").descending());

        List<Event> events = eventRepository.findAll(spec, pageRequest).getContent();

        return events.stream()
                .map(eventMapper::toEventFullDto)
                .toList();
    }

    /**
     * Обновляет событие и изменяет его статус администратором.
     * <p>
     * Выполняет комплексное обновление события с проверкой бизнес-правил
     * для изменения состояния. Поддерживает публикацию и отклонение событий.
     * </p>
     *
     * @param eventId идентификатор обновляемого события
     * @param dto DTO с данными для обновления и действием по изменению состояния
     * @return полное DTO обновленного события
     * @throws NotFoundException если событие не найдено
     * @throws ConflictException если действие над состоянием невозможно
     */
    @Transactional
    @Override
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest dto) {
        Event event = eventExists(eventId);
        updateEventFields(event, dto);

        if (StringUtils.hasText(dto.getStateAction())) {
            EventStateAction stateAction = EventStateAction.valueOf(dto.getStateAction().toUpperCase());
            switch (stateAction) {
                case PUBLISH_EVENT -> {
                    if (!event.getState().equals(EventState.PENDING)) {
                        throw new ConflictException("The event cannot be published because" +
                                " it is not in the correct state: PENDING");
                    }

                    event.setPublishedOn(LocalDateTime.now());
                    event.setState(EventState.PUBLISHED);
                }
                case REJECT_EVENT -> {
                    if (event.getState().equals(EventState.PUBLISHED)) {
                        throw new ConflictException("Cannot publish the event because" +
                                " it's not in the right state: PUBLISHED");
                    }
                    event.setState(EventState.CANCELED);
                }
            }
        }

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    /**
     * Возвращает список событий для публичного доступа с расширенной фильтрацией.
     * <p>
     * Использует комбинацию стратегий фильтрации для реализации сложного поиска
     * событий. Автоматически учитывает статистику просмотров и обновляет счетчики.
     * </p>
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
     * @return список кратких DTO событий, соответствующих критериям фильтрации
     * @throws BadRequestException если указаны некорректные параметры
     */
    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         Integer from,
                                         Integer size,
                                         HttpServletRequest httpServletRequest
    ) {
        List<EventFilterStrategy> strategies = new ArrayList<>();
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("RangeStart must not be after rangeEnd");
        }

        strategies.add(dateRangeFilterStrategy);
        if (text != null && !text.isBlank()) {
            strategies.add(textFilterStrategy);
        }
        if (categories != null && !categories.isEmpty()) {
            strategies.add(categoryFilterStrategy);
        }
        if (paid != null) {
            strategies.add(paidFilterStrategy);
        }
        if (onlyAvailable) {
            strategies.add(onlyAvailableFilterStrategy);
        }

        Specification<Event> spec = Specification.where(null);
        for (EventFilterStrategy strategy : strategies) {
            if (strategy instanceof DateRangeFilterStrategy) {
                spec = spec.and(strategy.apply(rangeStart, rangeEnd));
            } else if (strategy instanceof TextFilterStrategy) {
                spec = spec.and(strategy.apply(text));
            } else if (strategy instanceof CategoryFilterStrategy) {
                spec = spec.and(strategy.apply(categories));
            } else if (strategy instanceof PaidFilterStrategy) {
                spec = spec.and(strategy.apply(paid));
            } else if (strategy instanceof AvailableFilterStrategy) {
                spec = spec.and(strategy.apply());
            }
        }

        EventSort sortEnum;
        if (sort != null) {
            sortEnum = EventSort.valueOf(sort.toUpperCase());
        } else {
            sortEnum = EventSort.EVENT_DATE;
        }

        Sort sortBy = switch (sortEnum) {
            case EVENT_DATE -> Sort.by("eventDate").ascending();
            case VIEWS -> Sort.by("views").descending();
        };

        PageRequest pageRequest = PageRequest.of(from / size, size, sortBy);

        List<Event> events = eventRepository.findAll(spec, pageRequest).getContent();

        List<EventShortDto> eventShort = events.stream()
                .map(eventMapper::toShortDto)
                .toList();

        if (!events.isEmpty()) {
            events.forEach(event -> {
                event.setViews(event.getViews() + 1);
                eventRepository.save(event);
            });
        }

        statsClient.saveHit(httpServletRequest);

        return eventShort;
    }

    /**
     * Возвращает детальную информацию о событии для публичного доступа.
     * <p>
     * Предоставляет полные данные о событии и автоматически увеличивает
     * счетчик просмотров. Доступно только для опубликованных событий.
     * </p>
     *
     * @param id идентификатор запрашиваемого события
     * @param httpServletRequest объект HTTP запроса для учета статистики
     * @return полное DTO события
     * @throws NotFoundException если событие не найдено или не опубликовано
     */
    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventById(Long id, HttpServletRequest httpServletRequest) {
        Event event = eventExists(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event must be published.");
        }

        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        statsClient.saveHit(httpServletRequest);

        return eventMapper.toEventFullDto(event);
    }

    /**
     * Возвращает список событий пользователя с пагинацией.
     * <p>
     * Предоставляет пользователю доступ ко всем его событиям независимо от состояния.
     * Использует простой поиск по идентификатору инициатора с пагинацией.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param from количество событий, которые нужно пропустить (пагинация)
     * @param size количество событий в возвращаемом наборе (пагинация)
     * @return список полных DTO событий пользователя
     * @throws NotFoundException если пользователь не найден
     */
    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getUserEvents(Long userId, Integer from, Integer size) {
        userService.userExists(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> event = eventRepository.findByInitiatorId(userId, pageable);

        return event.stream()
                .map(eventMapper::toEventFullDto)
                .toList();
    }

    /**
     * Создает новое событие от имени пользователя.
     * <p>
     * Выполняет комплексную валидацию данных и создает событие в состоянии PENDING.
     * Проверяет корректность даты события и лимита участников.
     * </p>
     *
     * @param userId идентификатор пользователя-создателя
     * @param dto DTO с данными для создания события
     * @return полное DTO созданного события
     * @throws NotFoundException если пользователь или категория не найдены
     * @throws BadRequestException если дата события или лимит участников некорректен
     */
    @Override
    public EventFullDto add(Long userId, NewEventDto dto) {
        User initiator = userService.userExists(userId);
        Category category = categoryService.categoryExists(dto.getCategory());
        Location location = new Location(dto.getLocation().getLat(), dto.getLocation().getLon());
        if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("The date and time for which the event is scheduled cannot" +
                    " be earlier than two hours from the current moment");
        }
        if (dto.getParticipantLimit() < 0) {
            throw new BadRequestException("There should not be a negative participant limit.");
        }

        Event event = Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(location)
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .initiator(initiator)
                .build();

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    /**
     * Возвращает детальную информацию о конкретном событии пользователя.
     * <p>
     * Предоставляет полные данные о событии для личного кабинета пользователя.
     * Выполняет проверку прав доступа к событию.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @return полное DTO события
     * @throws NotFoundException если событие или пользователь не найдены
     * @throws ForbiddenException если пользователь не является создателем события
     */
    @Transactional(readOnly = true)
    @Override
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        userService.userExists(userId);
        Event event = eventExists(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Only the initiator of the event can" +
                    " receive full information about the event.");
        }

        return eventMapper.toEventFullDto(event);
    }

    /**
     * Обновляет существующее событие пользователя.
     * <p>
     * Позволяет пользователю редактировать свое событие с ограничениями.
     * Проверяет состояние события, права доступа и корректность данных.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param eventId идентификатор события
     * @param request DTO с данными для обновления
     * @return полное DTO обновленного события
     * @throws NotFoundException если событие или пользователь не найдены
     * @throws ForbiddenException если пользователь не является создателем события
     * @throws ConflictException если событие опубликовано
     * @throws BadRequestException если данные некорректны
     */
    @Transactional
    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        userService.userExists(userId);
        Event event = eventExists(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Only the initiator of the event can change the event.");
        }
        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }
        if (request.getParticipantLimit() != null && request.getParticipantLimit() < 0) {
            throw new BadRequestException("There should not be a negative participant limit.");
        }

        updateEventFields(event, request);
        if (request.getStateAction() != null && !request.getStateAction().isBlank()) {
            EventStateAction stateAction = EventStateAction.valueOf(request.getStateAction().toUpperCase());
            switch (stateAction) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
                default -> throw new BadRequestException("User cannot perform action: " + request.getStateAction());
            }
        }

        event = eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    /**
     * Возвращает список запросов на участие в конкретном событии пользователя.
     * <p>
     * Предоставляет создателю события доступ ко всем запросам на участие.
     * Выполняет проверку прав доступа к событию.
     * </p>
     *
     * @param userId идентификатор пользователя-создателя события
     * @param eventId идентификатор события
     * @return список DTO запросов на участие
     * @throws NotFoundException если событие или пользователь не найдены
     * @throws ForbiddenException если пользователь не является создателем события
     */
    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getRequestsEventByUser(Long userId, Long eventId) {
        Event event = eventExists(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Only the initiator of the event can receive" +
                    " information about requests to participate in the event.");
        }

        List<ParticipationRequest> requests = requestRepository.findByEvent(eventId);

        return requests.stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    /**
     * Изменяет статусы запросов на участие в событии пользователя.
     * <p>
     * Позволяет создателю события массово подтверждать или отклонять запросы
     * с автоматической проверкой лимита участников. Обрабатывает запросы в порядке
     * поступления и автоматически отклоняет запросы при превышении лимита.
     * </p>
     *
     * @param userId идентификатор пользователя-создателя события
     * @param eventId идентификатор события
     * @param dto DTO с идентификаторами запросов и целевым статусом
     * @return результат обновления статусов с разделением на подтвержденные и отклоненные запросы
     * @throws NotFoundException если событие, пользователь или запросы не найдены
     * @throws ForbiddenException если пользователь не является создателем события
     * @throws ConflictException если превышен лимит участников или модерация отключена
     * @throws BadRequestException если статус некорректен
     */
    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateStatusEventByUser(Long userId,
                                                                  Long eventId,
                                                                  EventRequestStatusUpdateRequest dto) {
        Event event = eventExists(eventId);
        if (!userId.equals(event.getInitiator().getId())) {
            throw new ForbiddenException("Only the initiator of the event can change" +
                    " the status of applications for participation in the event.");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("The event has a 0 application limit or application" +
                    " pre-moderation is disabled. Application confirmation is not required.");
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("The limit of applications for events has been reached");
        }

        List<ParticipationRequest> eventRequests = requestRepository.findByIdIn(dto.getRequestIds());
        if (eventRequests.stream().anyMatch(req -> !req.getStatus().equals(EventRequestStatus.PENDING))) {
            throw new ConflictException("The status can only be changed in applications" +
                    " that are in the pending state.");
        }

        EventRequestStatus status;
        try {
            status = EventRequestStatus.valueOf(dto.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Incorrectly made request. Request must have status CONFIRMED or REJECTED");
        }

        long currentConfirmed = event.getConfirmedRequests();
        if (status.equals(EventRequestStatus.CONFIRMED)) {
            for (ParticipationRequest req : eventRequests) {
                if (currentConfirmed < event.getParticipantLimit()) {
                    req.setStatus(EventRequestStatus.CONFIRMED);
                    currentConfirmed++;
                } else {
                    req.setStatus(EventRequestStatus.REJECTED);
                }
            }
            event.setConfirmedRequests(currentConfirmed);
        } else {
            eventRequests.forEach(req -> req.setStatus(EventRequestStatus.REJECTED));
        }

        requestRepository.saveAll(eventRequests);
        eventRepository.save(event);
        List<ParticipationRequestDto> confirmedRequests = eventRequests.stream()
                .filter(req -> req.getStatus().equals(EventRequestStatus.CONFIRMED))
                .map(requestMapper::toRequestDto)
                .toList();
        List<ParticipationRequestDto> rejectedRequests = eventRequests.stream()
                .filter(req -> req.getStatus().equals(EventRequestStatus.REJECTED))
                .map(requestMapper::toRequestDto)
                .toList();

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    /**
     * Проверяет существование события по идентификатору.
     * <p>
     * Вспомогательный метод для валидации наличия события в базе данных.
     * Используется в других методах сервиса перед выполнением операций
     * с конкретным событием.
     * </p>
     *
     * @param eventId идентификатор события для проверки
     * @return сущность события, если найдена
     * @throws NotFoundException если событие с указанным ID не найдено
     */
    @Override
    public Event eventExists(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=%d was not found".formatted(eventId)));
    }

    /**
     * Обновляет поля события из административного DTO.
     * <p>
     * Выполняет частичное обновление полей события - изменяются только те поля,
     * которые явно указаны в DTO. Проверяет корректность даты события.
     * </p>
     *
     * @param event сущность события для обновления
     * @param request DTO с данными для обновления
     */
    private void updateEventFields(Event event, UpdateEventAdminRequest request) {
        if (StringUtils.hasText(request.getAnnotation())) {
            event.setAnnotation(request.getAnnotation());
        }
        if (Objects.nonNull(request.getCategory())) {
            event.setCategory(categoryService.categoryExists(request.getCategory()));
        }
        if (StringUtils.hasText(request.getDescription())) {
            event.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getEventDate())) {
            event.setEventDate(checkEventDates(request.getEventDate(), event.getPublishedOn()));
        }
        if (Objects.nonNull(request.getLocation())) {
            Location location = new Location(request.getLocation().getLat(), request.getLocation().getLon());
            event.setLocation(location);
        }
        if (Objects.nonNull(request.getPaid())) {
            event.setPaid(request.getPaid());
        }
        if (Objects.nonNull(request.getParticipantLimit())) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (Objects.nonNull(request.getRequestModeration())) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (StringUtils.hasText(request.getTitle())) {
            event.setTitle(request.getTitle());
        }
    }

    /**
     * Обновляет поля события из пользовательского DTO.
     * <p>
     * Выполняет частичное обновление полей события с дополнительными проверками
     * для пользовательского сценария. Проверяет корректность даты события.
     * </p>
     *
     * @param event сущность события для обновления
     * @param request DTO с данными для обновления
     */
    private void updateEventFields(Event event, UpdateEventUserRequest request) {
        if (StringUtils.hasText(request.getAnnotation())) {
            event.setAnnotation(request.getAnnotation());
        }
        if (Objects.nonNull(request.getCategory())) {
            event.setCategory(categoryService.categoryExists(request.getCategory()));
        }
        if (StringUtils.hasText(request.getDescription())) {
            event.setDescription(request.getDescription());
        }
        if (Objects.nonNull(request.getEventDate())) {
            event.setEventDate(checkEventDates(request.getEventDate(), event.getPublishedOn()));
            checkEventDates(event.getEventDate());
        }
        if (Objects.nonNull(request.getLocation())) {
            Location location = new Location(request.getLocation().getLat(), request.getLocation().getLon());
            event.setLocation(location);
        }
        if (Objects.nonNull(request.getParticipantLimit())) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (Objects.nonNull(request.getRequestModeration())) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (StringUtils.hasText(request.getTitle())) {
            event.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getPaid())) {
            event.setPaid(request.getPaid());
        }
    }

    /**
     * Проверяет корректность даты события при административном обновлении.
     * <p>
     * Убеждается, что дата события не раньше чем за 1 час до публикации
     * и не в прошлом.
     * </p>
     *
     * @param eventDate новая дата события
     * @param published дата публикации события
     * @return проверенная дата события
     * @throws BadRequestException если дата некорректна
     */
    private LocalDateTime checkEventDates(LocalDateTime eventDate, LocalDateTime published) {
        if ((published != null && eventDate.isBefore(published.minusHours(1)))
                || eventDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The start date of the event being modified" +
                    " must be no earlier than one hour from the publication date.");
        }
        return eventDate;
    }

    /**
     * Проверяет корректность даты события при пользовательском обновлении.
     * <p>
     * Убеждается, что дата события не раньше чем через 2 часа от текущего момента.
     * </p>
     *
     * @param eventDate новая дата события
     * @throws BadRequestException если дата некорректна
     */
    private void checkEventDates(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("The date and time for which the event is scheduled" +
                    " cannot be earlier than two hours from the current moment");
        }
    }
}
