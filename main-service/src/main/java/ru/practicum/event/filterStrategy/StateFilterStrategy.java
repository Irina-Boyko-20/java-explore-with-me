package ru.practicum.event.filterStrategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventState;
import ru.practicum.exception.BadRequestException;

import java.util.List;

/**
 * Стратегия фильтрации событий по состояниям жизненного цикла.
 * <p>
 * Реализует фильтрацию событий на основе их текущего состояния ({@link EventState}).
 * Поддерживает фильтрацию по одному или нескольким состояниям одновременно.
 * Выполняет валидацию переданных строковых значений состояний и преобразует их
 * в соответствующие значения enum {@link EventState}.
 * </p>
 */
@Component
public class StateFilterStrategy implements EventFilterStrategy {

    /**
     * Создает спецификацию для фильтрации событий по состояниям.
     * <p>
     * Фильтрует события, оставляя только те, которые находятся в одном из
     * указанных состояний. Выполняет преобразование строковых значений
     * в enum {@link EventState} с валидацией.
     * </p>
     *
     * @param params массив параметров, где:
     *               <ul>
     *               <li>params[0] - список строковых представлений состояний ({@code List<String>})</li>
     *               </ul>
     * @return спецификация {@link Specification} для фильтрации по состояниям.
     *         Если передан null или пустой список состояний, возвращает
     *         условие, которое не накладывает ограничений (все события проходят фильтр).
     * @throws ClassCastException если params[0] не является {@code List<String>}
     * @throws ArrayIndexOutOfBoundsException если params пуст
     * @throws BadRequestException если переданное строковое значение не соответствует
     *                             ни одному из допустимых состояний {@link EventState}
     */
    @Override
    public Specification<Event> apply(Object... params) {
        @SuppressWarnings("unchecked")
        List<String> states = (List<String>) params[0];
        return (root, query, criteriaBuilder) -> {
            if (states == null || states.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            List<EventState> eventStates = states.stream()
                    .map(String::toUpperCase)
                    .map(state -> {
                        try {
                            return EventState.valueOf(state);
                        } catch (IllegalArgumentException e) {
                            throw new BadRequestException("Invalid state: " + state);
                        }
                    })
                    .toList();
            return root.get("state").in(eventStates);
        };
    }
}
