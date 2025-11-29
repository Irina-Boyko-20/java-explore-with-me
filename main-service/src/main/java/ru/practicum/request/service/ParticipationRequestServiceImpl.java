package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventRequestStatus;
import ru.practicum.event.entity.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.entity.ParticipationRequest;
import ru.practicum.request.mapper.ParticipationRequestMapper;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для работы с запросами на участие в событиях.
 * <p>
 * Предоставляет конкретную реализацию бизнес-логики для операций с запросами на участие:
 * получение, создание и отмена запросов. Обеспечивает комплексную валидацию данных,
 * проверку прав доступа и соблюдение бизнес-правил при работе с запросами на участие.
 * </p>
 *
 * <p>
 * Взаимодействует с репозиториями {@link ParticipationRequestRepository} и {@link EventRepository}
 * для доступа к данным, сервисами {@link UserService} и {@link EventService} для валидации
 * и маппером {@link ParticipationRequestMapper} для преобразования между сущностями и DTO.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventService eventService;
    private final ParticipationRequestMapper mapper;

    /**
     * Возвращает список запросов на участие текущего пользователя.
     * <p>
     * Предоставляет полную историю запросов на участие пользователя во всех событиях.
     * Выполняет проверку существования пользователя перед получением данных.
     * </p>
     *
     * @param userId идентификатор пользователя, для которого запрашиваются данные
     * @return список DTO запросов на участие пользователя.
     *         Если запросы не найдены, возвращается пустой список.
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> get(Long userId) {
        userService.userExists(userId);
        List<ParticipationRequest> requests = requestRepository.findByRequester(userId);

        return requests.stream()
                .map(mapper::toRequestDto)
                .toList();
    }

    /**
     * Создает новый запрос на участие в указанном событии.
     * <p>
     * Позволяет пользователю выразить желание участвовать в событии другого пользователя.
     * Выполняет комплексную проверку возможности участия перед созданием запроса,
     * включая проверку дублирующих запросов, прав доступа, статуса события и лимитов участников.
     * </p>
     *
     * @param userId идентификатор пользователя, создающего запрос
     * @param eventId идентификатор события, в котором пользователь хочет участвовать
     * @return DTO созданного запроса на участие
     * @throws ru.practicum.exception.NotFoundException если пользователь или событие не найдены
     * @throws ru.practicum.exception.ConflictException если пользователь уже подавал запрос на это событие,
     *         или если пользователь является инициатором события, или событие не опубликовано,
     *         или достигнут лимит участников
     */
    @Override
    public ParticipationRequestDto add(Long userId, Long eventId) {
        userService.userExists(userId);
        Event event = eventService.eventExists(eventId);

        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ConflictException(String.format("Event with id = %d already contain" +
                    " request from user with id = %d.", eventId, userId));
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("The event initiator cannot add a request to participate in their event.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("You cannot participate in an unpublished event");
        }
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("The event has reached its participation request limit.");
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(userId)
                .event(eventId)
                .status(EventRequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            request.setStatus(EventRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }

        eventRepository.save(event);
        request = requestRepository.save(request);
        return mapper.toRequestDto(request);
    }

    /**
     * Отменяет собственный запрос на участие.
     * <p>
     * Позволяет пользователю отозвать свой запрос на участие в событии.
     * Выполняет проверку прав доступа и существования запроса перед отменой.
     * Запрос переходит в статус {@code CANCELED}.
     * </p>
     *
     * @param userId идентификатор пользователя, отменяющего запрос
     * @param requestId идентификатор запроса на участие для отмены
     * @return DTO отмененного запроса на участие
     * @throws ru.practicum.exception.NotFoundException если пользователь или запрос не найдены
     * @throws ru.practicum.exception.ForbiddenException если пользователь пытается отменить чужой запрос
     */
    @Override
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        userService.userExists(userId);
        ParticipationRequest request = requestExists(requestId);

        if (!request.getRequester().equals(userId)) {
            throw new ForbiddenException("Only the user who created the request" +
                    " can cancel a request to participate in an event.");
        }

        request.setStatus(EventRequestStatus.CANCELED);
        request = requestRepository.save(request);
        return mapper.toRequestDto(request);
    }

    /**
     * Проверяет существование запроса на участие по идентификатору.
     * <p>
     * Вспомогательный метод для валидации наличия запроса в базе данных.
     * Используется в других методах сервиса перед выполнением операций
     * с конкретным запросом.
     * </p>
     *
     * @param requestId идентификатор запроса для проверки
     * @return сущность запроса, если найдена
     * @throws ru.practicum.exception.NotFoundException если запрос с указанным ID не найден
     */
    private ParticipationRequest requestExists(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        "Participation request with id=%d was not found".formatted(requestId)
                ));
    }
}
