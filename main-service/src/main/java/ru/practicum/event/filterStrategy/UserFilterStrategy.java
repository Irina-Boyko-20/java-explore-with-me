package ru.practicum.event.filterStrategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;

import java.util.List;

/**
 * Стратегия фильтрации событий по идентификаторам пользователей-инициаторов.
 * <p>
 * Реализует фильтрацию событий на основе списка идентификаторов пользователей,
 * которые являются создателями событий. Используется для поиска событий,
 * созданных конкретными пользователями.
 * </p>
 */
@Component
public class UserFilterStrategy implements EventFilterStrategy {

    /**
     * Создает спецификацию для фильтрации событий по идентификаторам пользователей-инициаторов.
     * <p>
     * Фильтрует события, оставляя только те, которые созданы пользователями
     * с идентификаторами из переданного списка.
     * </p>
     *
     * @param params массив параметров, где:
     *               <ul>
     *               <li>params[0] - список идентификаторов пользователей ({@code List<Long>})</li>
     *               </ul>
     * @return спецификация {@link Specification} для фильтрации по пользователям.
     *         Если передан null или пустой список пользователей, возвращает
     *         условие, которое не накладывает ограничений (все события проходят фильтр).
     * @throws ClassCastException если params[0] не является {@code List<Long>}
     * @throws ArrayIndexOutOfBoundsException если params пуст
     */
    @Override
    public Specification<Event> apply(Object... params) {
        @SuppressWarnings("unchecked")
        List<Long> users = (List<Long>) params[0];
        return (root, query, criteriaBuilder) -> {
            if (users == null || users.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("initiator").get("id").in(users);
        };
    }
}
