package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

/**
 * Сервис для работы с запросами на участие в событиях.
 * <p>
 * Предоставляет бизнес-логику для операций с запросами на участие от лица пользователей.
 * Обеспечивает валидацию данных, проверку прав доступа и обработку исключительных ситуаций
 * при работе с запросами на участие.
 * </p>
 *
 * <p>
 * Служит прослойкой между контроллерами и репозиторием, инкапсулируя
 * сложную бизнес-логику и гарантируя целостность данных запросов на участие.
 * </p>
 */
public interface ParticipationRequestService {

    /**
     * Возвращает список запросов на участие текущего пользователя.
     *
     * @param userId идентификатор пользователя, для которого запрашиваются данные
     * @return список DTO запросов на участие пользователя.
     *         Если запросы не найдены, возвращается пустой список.
     */
    List<ParticipationRequestDto> get(Long userId);

    /**
     * Создает новый запрос на участие в указанном событии.
     *
     * @param userId идентификатор пользователя, создающего запрос
     * @param eventId идентификатор события, в котором пользователь хочет участвовать
     * @return DTO созданного запроса на участие
     * @throws ru.practicum.exception.ConflictException если пользователь уже подавал запрос на это событие,
     *         или если пользователь является инициатором события, или событие не опубликовано,
     *         или достигнут лимит участников, или требуется предварительная модерация
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     */
    ParticipationRequestDto add(Long userId, Long eventId);

    /**
     * Отменяет собственный запрос на участие.
     *
     * @param userId идентификатор пользователя, отменяющего запрос
     * @param requestId идентификатор запроса на участие для отмены
     * @return DTO отмененного запроса на участие
     * @throws ru.practicum.exception.NotFoundException если запрос не найден
     * @throws ru.practicum.exception.ConflictException если запрос уже обработан (подтвержден или отклонен)
     */
    ParticipationRequestDto cancel(Long userId, Long requestId);
}
