package ru.practicum.event.filterStrategy;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.event.entity.Event;

/**
 * Стратегия текстового поиска событий по аннотации, заголовку и описанию.
 * <p>
 * Реализует полнотекстовый поиск событий по ключевым текстовым полям:
 * аннотации, заголовку и полному описанию. Поиск выполняется без учета регистра
 * и поддерживает частичное совпадение текста.
 * </p>
 */
@Component
public class TextFilterStrategy implements EventFilterStrategy {

    /**
     * Создает спецификацию для полнотекстового поиска событий.
     * <p>
     * Выполняет поиск событий, в которых указанный текст встречается
     * в аннотации, заголовке или полном описании.
     * </p>
     *
     * @param params массив параметров, где:
     *               <ul>
     *               <li>params[0] - строка текста для поиска ({@code String})</li>
     *               </ul>
     * @return спецификация {@link Specification} для текстового поиска.
     *         Если передан null или пустая строка, возвращает условие,
     *         которое не накладывает ограничений (все события проходят фильтр).
     * @throws ClassCastException если params[0] не является {@code String}
     * @throws ArrayIndexOutOfBoundsException если params пуст
     */
    @Override
    public Specification<Event> apply(Object... params) {
        String text = (String) params[0];
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("title")), "%" + text.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + text.toLowerCase() + "%")
        );
    }
}
