package ru.practicum.event.filterStrategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;

/**
 * Стратегия фильтрации событий по доступности для участия.
 * <p>
 * Реализует фильтрацию событий на основе наличия свободных мест для участия.
 * Определяет события, в которых текущее количество участников меньше установленного
 * лимита, что означает возможность подачи новых заявок на участие.
 * </p>
 */
@Component
public class AvailableFilterStrategy implements EventFilterStrategy {

    /**
     * Создает спецификацию для фильтрации событий по доступности участия.
     * <p>
     * Фильтрует события, оставляя только те, в которых есть свободные места
     * для новых участников. Событие считается доступным если:
     * </p>
     * <ul>
     * <li>participantLimit = 0 (отсутствие ограничения)</li>
     * <li>confirmedRequests < participantLimit (есть свободные места)</li>
     * </ul>
     * <p>
     * В текущей реализации используется сравнение participantAmount < participantLimit,
     * что требует наличия соответствующих полей в сущности Event.
     * </p>
     *
     * @param params параметры не используются в данной стратегии
     * @return спецификация {@link Specification} для фильтрации по доступности.
     *         Всегда возвращает условие проверки доступности событий.
     */
    @Override
    public Specification<Event> apply(Object... params) {
        return (root, query, cb) ->
                cb.lt(
                        root.get("participantAmount"),
                        root.get("participantLimit")
                );
    }
}
