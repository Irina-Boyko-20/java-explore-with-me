package ru.practicum.event.filterStrategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;

/**
 * Стратегия фильтрации событий по признаку платности.
 * <p>
 * Реализует фильтрацию событий на основе флага платности ({@code paid}).
 * Позволяет разделять события на платные и бесплатные, что является важным
 * критерием для многих пользователей при выборе мероприятий для участия.
 * </p>
 */
@Component
public class PaidFilterStrategy implements EventFilterStrategy {

    /**
     * Создает спецификацию для фильтрации событий по признаку платности.
     * <p>
     * Фильтрует события, оставляя только те, которые соответствуют указанному
     * значению флага платности. Если передано {@code true}, возвращаются только
     * платные события; если {@code false} - только бесплатные.
     * </p>
     *
     * @param params массив параметров, где:
     *               <ul>
     *               <li>params[0] - булево значение платности ({@code Boolean})</li>
     *               </ul>
     * @return спецификация {@link Specification} для фильтрации по платности.
     *         Если передан {@code null}, возвращает условие, которое не накладывает
     *         ограничений (все события проходят фильтр).
     * @throws ClassCastException если params[0] не является {@code Boolean}
     * @throws ArrayIndexOutOfBoundsException если params пуст
     */
    @Override
    public Specification<Event> apply(Object... params) {
        Boolean paid = (Boolean) params[0];
        return (root, query, cb) -> cb.equal(root.get("paid"), paid);
    }
}
