package ru.practicum.event.filterStrategy;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;

import java.time.LocalDateTime;

/**
 * Стратегия фильтрации событий по временному диапазону проведения.
 * <p>
 * Реализует фильтрацию событий на основе даты и времени их проведения.
 * Поддерживает различные сценарии: поиск событий в определенном интервале,
 * будущих событий, или событий прошедших за указанный период.
 * </p>
 */
@Component
public class DateRangeFilterStrategy implements EventFilterStrategy {

    /**
     * Создает спецификацию для фильтрации событий по временному диапазону.
     * <p>
     * Фильтрует события, оставляя только те, дата проведения которых попадает
     * в указанный временной интервал. Поддерживает различные комбинации параметров:
     * </p>
     * <ul>
     * <li>Только rangeStart - события после указанной даты</li>
     * <li>Только rangeEnd - события до указанной даты</li>
     * <li>Оба параметра - события в указанном интервале</li>
     * <li>Ни одного параметра - будущие события (от текущего момента)</li>
     * </ul>
     *
     * @param params массив параметров, где:
     *               <ul>
     *               <li>params[0] - начальная дата диапазона ({@code LocalDateTime})</li>
     *               <li>params[1] - конечная дата диапазона ({@code LocalDateTime})</li>
     *               </ul>
     * @return спецификация {@link Specification} для фильтрации по дате проведения.
     *         Если оба параметра null, возвращает условие для будущих событий
     *         (eventDate > now()).
     * @throws ClassCastException если params[0] или params[1] не являются {@code LocalDateTime}
     * @throws ArrayIndexOutOfBoundsException если params содержит менее 2 элементов
     */
    @Override
    public Specification<Event> apply(Object... params) {
        LocalDateTime rangeStart = (LocalDateTime) params[0];
        LocalDateTime rangeEnd = (LocalDateTime) params[1];
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (rangeStart != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThan(root.get("eventDate"), rangeStart)
                );
            }
            if (rangeEnd != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd)
                );
            }
            if (rangeStart == null && rangeEnd == null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThan(root.get("eventDate"), LocalDateTime.now())
                );
            }
            return predicate;
        };
    }
}
