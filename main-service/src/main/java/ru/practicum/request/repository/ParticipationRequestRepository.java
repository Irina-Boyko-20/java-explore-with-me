package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.request.entity.ParticipationRequest;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link ParticipationRequest}.
 * <p>
 * Расширяет {@link JpaRepository} для предоставления стандартных CRUD операций
 * над запросами на участие в событиях. Содержит специализированные методы для
 * поиска запросов по различным критериям и проверки существования запросов.
 * </p>
 *
 * <p>
 * Обеспечивает эффективный доступ к данным запросов на участие с поддержкой
 * различных сценариев фильтрации и используется сервисным слоем для выполнения
 * операций валидации и бизнес-логики, связанной с участием в событиях.
 * </p>
 */
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    /**
     * Находит все запросы на участие для указанного события.
     *
     * @param eventId идентификатор события для поиска запросов
     * @return список запросов на участие для указанного события.
     *         Если запросы не найдены, возвращается пустой список.
     */
    List<ParticipationRequest> findByEvent(Long eventId);

    /**
     * Находит запросы на участие по списку идентификаторов.
     *
     * @param ids список идентификаторов запросов для поиска
     * @return список запросов на участие, чьи идентификаторы присутствуют в переданном списке.
     *         Если ни один идентификатор не найден, возвращается пустой список.
     */
    List<ParticipationRequest> findByIdIn(List<Long> ids);

    /**
     * Находит все запросы на участие для указанного пользователя.
     *
     * @param userId идентификатор пользователя для поиска его запросов
     * @return список запросов на участие, созданных указанным пользователем.
     *         Если запросы не найдены, возвращается пустой список.
     */
    List<ParticipationRequest> findByRequester(Long userId);

    /**
     * Проверяет существование запроса на участие для указанной пары пользователь-событие.
     *
     * @param requesterId идентификатор пользователя
     * @param eventId идентификатор события
     * @return {@code true} если запрос для указанной пары пользователь-событие существует,
     *         {@code false} в противном случае
     */
    boolean existsByRequesterAndEvent(Long requesterId, Long eventId);
}
