package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.entity.Category;

/**
 * Репозиторий для работы с сущностью {@link Category}.
 * <p>
 * Расширяет {@link JpaRepository} для предоставления стандартных CRUD операций
 * над категориями событий. Содержит специализированные методы для проверки
 * уникальности наименований категорий.
 * </p>
 *
 * <p>
 * Обеспечивает доступ к данным категорий и используется сервисным слоем
 * для выполнения операций валидации и проверки бизнес-правил.
 * </p>
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Проверяет существование категории с указанным наименованием.
     *
     * @param name наименование категории для проверки
     * @return {@code true} если категория с таким наименованием существует,
     *         {@code false} в противном случае
     */
    boolean existsByName(String name);
}
