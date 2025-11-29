package ru.practicum.user.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.entity.User;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * Реализация сервиса для работы с пользователями системы.
 * <p>
 * Предоставляет конкретную реализацию бизнес-логики для операций с пользователями:
 * получение, создание, удаление и валидация данных. Обеспечивает целостность данных
 * и соблюдение бизнес-правил при работе с пользователями.
 * </p>
 *
 * <p>
 * Взаимодействует с репозиторием {@link UserRepository} для доступа к данным
 * и маппером {@link UserMapper} для преобразования между сущностями и DTO.
 * Использует транзакции для обеспечения согласованности данных.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    /**
     * Репозиторий для работы с пользователями.
     */
    private final UserRepository repository;

    /**
     * Маппер для преобразования между сущностями и DTO пользователей.
     */
    private final UserMapper mapper;

    /**
     * Возвращает информацию о пользователях с поддержкой фильтрации и пагинации.
     * <p>
     * Предоставляет гибкий интерфейс для получения пользователей: либо по списку
     * идентификаторов, либо всех пользователей с применением пагинации.
     * Использует сортировку по идентификатору для стабильности результатов.
     * </p>
     *
     * @param ids опциональный список идентификаторов пользователей для фильтрации.
     *           Если параметр {@code null} или пустой, возвращаются все пользователи.
     * @param from количество пользователей, которые нужно пропустить (offset)
     * @param size количество пользователей в возвращаемом наборе (limit)
     * @return список DTO пользователей. Если пользователи не найдены, возвращается пустой список
     */

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> get(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(ASC, "id"));

        if (ids != null && !ids.isEmpty()) {
            List<User> users = repository.findByIds(ids, pageable);

            return users.stream()
                    .map(mapper::toUserDto)
                    .toList();
        } else {
            List<User> users = repository.findAllBy(pageable);

            return users.stream()
                    .map(mapper::toUserDto)
                    .toList();
        }
    }

    /**
     * Создает нового пользователя на основе предоставленных данных.
     * <p>
     * Выполняет валидацию уникальности email-адреса, преобразует DTO в сущность
     * и сохраняет в базе данных. Возвращает созданного пользователя с присвоенным идентификатором.
     * </p>
     *
     * @param dto DTO с данными для создания пользователя
     * @return DTO созданного пользователя с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если пользователь с таким email уже существует
     */
    @Override
    public UserDto add(NewUserRequest dto) {
        emailExists(dto.getEmail());
        return mapper.toUserDto(repository.save(mapper.toUser(dto)));
    }

    /**
     * Удаляет пользователя по идентификатору после проверки его существования.
     * <p>
     * Выполняет проверку наличия пользователя перед удалением.
     * Удаление выполняется каскадно в соответствии с настройками базы данных
     * и бизнес-логикой приложения для связанных сущностей.
     * </p>
     *
     * @param userId идентификатор пользователя для удаления
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public void delete(Long userId) {
        userExists(userId);
        repository.deleteById(userId);
    }

    /**
     * Проверяет уникальность email-адреса пользователя.
     * <p>
     * Выполняет проверку существования пользователя с указанным email-адресом.
     * Используется при создании пользователей для предотвращения дублирования email-адресов.
     * </p>
     *
     * @param email email-адрес для проверки
     * @throws ru.practicum.exception.ConflictException если пользователь с таким email уже существует
     */
    @Override
    public void emailExists(String email) {
        if (repository.existsByEmail(email)) {
            throw new ConflictException("could not execute statement; SQL [n/a];" +
                    " constraint " + email + "; nested exception is org.hibernate.exception." +
                    "ConstraintViolationException: could not execute statement");
        }
    }

    /**
     * Проверяет существование пользователя по идентификатору.
     * <p>
     * Вспомогательный метод для валидации наличия пользователя в базе данных.
     * Используется в других методах сервиса перед выполнением операций
     * с конкретным пользователем.
     * </p>
     *
     * @param userId идентификатор пользователя для проверки
     * @return сущность пользователя, если найдена
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    @Override
    public User userExists(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=%d was not found".formatted(userId)));
    }
}
