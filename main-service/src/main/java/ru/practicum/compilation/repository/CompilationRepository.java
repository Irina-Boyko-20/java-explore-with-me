package ru.practicum.compilation.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.compilation.entity.Compilation;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link Compilation}.
 * <p>
 * Расширяет {@link JpaRepository} для предоставления стандартных CRUD операций
 * над подборками событий. Содержит специализированные методы для проверки
 * уникальности заголовков, фильтрации по статусу закрепления и работы
 * со связанными событиями.
 * </p>
 *
 * <p>
 * Сочетает использование производных методов Spring Data JPA и нативных
 * SQL-запросов для эффективного доступа к данным.
 * </p>
 */
public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    /**
     * Проверяет существование подборки с указанным заголовком.
     *
     * @param title Заголовок подборки событий.
     * @return {@code true} если подборка с таким заголовком существует,
     *         {@code false} в противном случае
     */
    boolean existsByTitle(String title);

    /**
     * Находит подборки с учетом статуса закрепления и пагинации.
     *
     * @param pinned опциональный параметр фильтрации по статусу закрепления
     * @param pageRequest параметры пагинации (номер страницы и размер страницы)
     * @return список подборок, соответствующих критериям фильтрации,
     *         с учетом пагинации
     */
    List<Compilation> findByPinned(Boolean pinned, PageRequest pageRequest);

    /**
     * Находит идентификаторы событий, связанных с указанной подборкой.
     *
     * @param compId идентификатор подборки
     * @return список идентификаторов событий, входящих в указанную подборку.
     *         Если подборка не содержит событий, возвращается пустой список.
     */
    @Query(value = "SELECT event_id FROM compilation_event WHERE compilation_id = :compId", nativeQuery = true)
    List<Long> findEventIdsByCompilationId(@Param("compId") Long compId);
}
