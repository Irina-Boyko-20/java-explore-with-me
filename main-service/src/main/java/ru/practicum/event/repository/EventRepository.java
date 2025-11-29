package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.event.entity.Event;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Event}.
 * <p>
 * Расширяет {@link JpaRepository} для предоставления стандартных CRUD операций
 * над событиями и {@link JpaSpecificationExecutor} для поддержки сложных
 * динамических запросов с использованием спецификаций.
 * </p>
 *
 * <p>
 * Обеспечивает эффективный доступ к данным событий с поддержкой пагинации
 * и сложных критериев поиска. Используется сервисным слоем для выполнения
 * операций поиска и фильтрации событий по различным критериям.
 * </p>
 */
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    /**
     * Находит события по идентификатору инициатора с поддержкой пагинации.
     *
     * @param userId идентификатор пользователя-инициатора событий
     * @param pageable параметры пагинации (номер страницы, размер страницы, сортировка)
     * @return список событий, созданных указанным пользователем.
     *         Если события не найдены, возвращается пустой список.
     */
    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    /**
     * Находит все события по идентификатору категории.
     *
     * @param catId идентификатор категории для поиска событий
     * @return список событий, принадлежащих указанной категории.
     *         Если события не найдены, возвращается пустой список.
     */
    List<Event> findByCategoryId(Long catId);
}
