package ru.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.entity.Comment;

import java.util.List;

/**
 * Репозиторий для доступа к данным комментариев в базе данных.
 * <p>
 * Расширяет {@link JpaRepository}, предоставляя стандартные CRUD-операции
 * для сущности {@link Comment}. Дополнительно определяет методы для поиска
 * комментариев по критериям с поддержкой пагинации.
 * </p>
 * <p>
 * Интерфейс использует Spring Data JPA, что позволяет автоматически генерировать
 * реализации методов на основе их имен (Query Creation from Method Names).
 * Все методы выполняются в контексте транзакций.
 * </p>
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Находит все комментарии указанного пользователя с пагинацией.
     * <p>
     * Выполняет поиск комментариев по идентификатору автора. Использует
     * связь {@code authorName} сущности {@link Comment} для фильтрации.
     * </p>
     *
     * @param userId   идентификатор пользователя-автора для поиска комментариев
     *                 (должен быть не {@code null})
     * @param pageable параметры пагинации и сортировки ({@link Pageable}).
     *                 Определяет номер страницы, размер страницы и порядок сортировки.
     *                 Если сортировка не указана, используется порядок по умолчанию
     *                 (обычно по дате создания в порядке убывания).
     * @return список комментариев пользователя. Возвращает пустой список,
     *         если комментарии не найдены.
     */
    List<Comment> findByAuthorNameId(Long userId, Pageable pageable);

    /**
     * Находит все комментарии к указанному событию с пагинацией.
     * <p>
     * Выполняет поиск комментариев по идентификатору связанного события.
     * Использует связь {@code event} сущности {@link Comment} для фильтрации.
     * </p>
     *
     * @param eventId  идентификатор события для поиска комментариев
     *                 (должен быть не {@code null})
     * @param pageable параметры пагинации и сортировки ({@link Pageable}).
     *                 Определяет номер страницы, размер страницы и порядок сортировки.
     *                 Рекомендуется сортировать по {@code created DESC} для отображения
     *                 сначала новых комментариев.
     * @return список комментариев к событию. Возвращает пустой список,
     *         если комментарии не найдены.
     */
    List<Comment> findByEventId(Long eventId, Pageable pageable);
}
