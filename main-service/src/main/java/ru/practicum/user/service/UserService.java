package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.entity.User;

import java.util.List;

/**
 * Сервис для работы с пользователями системы.
 * <p>
 * Предоставляет бизнес-логику для операций с пользователями, включая
 * получение, создание, удаление и валидацию данных. Обеспечивает проверку
 * уникальности email-адресов и обработку исключительных ситуаций.
 * </p>
 *
 * <p>
 * Служит прослойкой между контроллерами и репозиторием, инкапсулируя
 * сложную бизнес-логику и гарантируя целостность данных пользователей.
 * </p>
 */
public interface UserService {

    /**
     * Возвращает информацию о пользователях с поддержкой фильтрации и пагинации.
     *
     * @param ids опциональный список идентификаторов пользователей для фильтрации.
     *           Если параметр {@code null} или пустой, возвращаются все пользователи.
     * @param from количество пользователей, которые нужно пропустить (offset)
     * @param size количество пользователей в возвращаемом наборе (limit)
     * @return список DTO пользователей. Если пользователи не найдены, возвращается пустой список
     */
    List<UserDto> get(List<Long> ids, Integer from, Integer size);

    /**
     * Создает нового пользователя.
     *
     * @param dto DTO с данными для создания пользователя
     * @return DTO созданного пользователя с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если пользователь с таким email уже существует
     */
    UserDto add(NewUserRequest dto);

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор удаляемого пользователя
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    void delete(Long userId);

    /**
     * Проверяет уникальность email-адреса пользователя.
     *
     * @param email email-адрес для проверки
     * @throws ru.practicum.exception.ConflictException если пользователь с таким email уже существует
     */
    void emailExists(String email);

    /**
     * Проверяет существование пользователя по идентификатору.
     *
     * @param id идентификатор пользователя для проверки
     * @return сущность пользователя, если найдена
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    User userExists(Long id);
}
