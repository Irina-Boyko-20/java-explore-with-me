package ru.practicum.event.filterStrategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;

import java.util.List;

/**
 * Стратегия фильтрации событий по категориям.
 * <p>
 * Реализует фильтрацию событий на основе их категориальной принадлежности.
 * Позволяет находить события, относящиеся к одной или нескольким указанным категориям.
 * Категории задаются через их идентификаторы, что обеспечивает эффективный поиск
 * по связям между событиями и категориями.
 * </p>
 */
@Component
public class CategoryFilterStrategy implements EventFilterStrategy {

    /**
     * Создает спецификацию для фильтрации событий по категориям.
     * <p>
     * Фильтрует события, оставляя только те, которые принадлежат к одной из
     * указанных категорий. Поиск выполняется по идентификаторам категорий,
     * что позволяет эффективно работать с большими объемами данных.
     * </p>
     *
     * @param params массив параметров, где:
     *               <ul>
     *               <li>params[0] - список идентификаторов категорий ({@code List<Long>})</li>
     *               </ul>
     * @return спецификация {@link Specification} для фильтрации по категориям.
     *         Если передан null или пустой список категорий, возвращает
     *         условие, которое не накладывает ограничений (все события проходят фильтр).
     * @throws ClassCastException если params[0] не является {@code List<Long>}
     * @throws ArrayIndexOutOfBoundsException если params пуст
     */
    @Override
    public Specification<Event> apply(Object... params) {
        @SuppressWarnings("unchecked")
        List<Long> categories = (List<Long>) params[0];
        return (root, query, criteriaBuilder) -> {
            if (categories == null || categories.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("category").get("id").in(categories);
        };
    }
}
