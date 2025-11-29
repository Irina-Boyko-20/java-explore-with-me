package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.user.entity.User;

import java.util.List;

/**
 * Репозиторий для работы с сущностью {@link User}.
 * <p>
 * Расширяет {@link JpaRepository} для предоставления стандартных CRUD операций
 * над пользователями системы. Содержит специализированные методы для поиска
 * пользователей по списку идентификаторов и проверки уникальности email-адресов.
 * </p>
 *
 * <p>
 * Обеспечивает эффективный доступ к данным пользователей с поддержкой пагинации
 * и используется сервисным слоем для выполнения операций валидации и выборки данных.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Находит пользователей по списку идентификаторов с поддержкой пагинации.
     *
     * @param ids список идентификаторов пользователей для поиска
     * @param page параметры пагинации (номер страницы, размер страницы)
     * @return список пользователей, чьи идентификаторы присутствуют в переданном списке.
     *         Если ни один идентификатор не найден, возвращается пустой список.
     */
    @Query("SELECT users FROM User users WHERE users.id IN :ids")
    List<User> findByIds(@Param("ids") List<Long> ids, Pageable page);

    /**
     * Находит всех пользователей с поддержкой пагинации.
     *
     * @param pageable параметры пагинации (номер страницы, размер страницы)
     * @return список всех пользователей с учетом пагинации.
     *         Если пользователи не найдены, возвращается пустой список.
     */
    List<User> findAllBy(Pageable pageable);

    /**
     * Проверяет существование пользователя с указанной электронной почтой.
     *
     * @param email электронная почта для проверки
     * @return {@code true} если пользователь с таким email существует,
     *         {@code false} в противном случае
     */
    boolean existsByEmail(String email);
}
