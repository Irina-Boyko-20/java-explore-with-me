package ru.practicum.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.entity.User;

/**
 * Маппер для преобразования между сущностью пользователя и DTO.
 * <p>
 * Обеспечивает конвертацию данных между слоями приложения:
 * из DTO в сущности для сохранения в БД и из сущностей в DTO для возврата в API.
 * Использует MapStruct для автоматической генерации кода преобразования.
 * </p>
 *
 * <p>
 * Содержит как методы, генерируемые MapStruct автоматически на основе аннотаций,
 * так и статические методы для ручного маппинга в специфических сценариях.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразует DTO для создания пользователя в сущность User.
     * <p>
     * Используется при создании нового пользователя. Идентификатор игнорируется,
     * так как он будет сгенерирован базой данных при сохранении.
     * </p>
     * <p>
     * Автоматически маппит поля email и name из DTO в соответствующие поля сущности.
     * </p>
     *
     * @param newUserDto DTO с данными для создания пользователя
     * @return сущность User с заполненными полями email и name
     */
    @Mapping(target = "id", ignore = true)
    User toUser(NewUserRequest newUserDto);

    /**
     * Преобразует сущность пользователя в DTO для ответа API.
     * <p>
     * Автоматически генерируется MapStruct. Выполняет преобразование
     * всех полей сущности в соответствующие поля DTO.
     * </p>
     * <p>
     * Порядок полей в результирующем DTO соответствует порядку параметров
     * в record {@link UserDto} - email, id, name.
     * </p>
     *
     * @param user сущность пользователя для преобразования
     * @return DTO пользователя с данными для ответа
     */
    UserDto toUserDto(User user);

    /**
     * Преобразует сущность пользователя в DTO для использования в краткой информации о событии.
     * <p>
     * Специализированный метод для создания краткого DTO пользователя, который используется
     * при формировании краткой информации о событии (EventShortDto) и других сценариях,
     * где требуется только базовая идентификация пользователя.
     * </p>
     * <p>
     * Выполняет ручной маппинг только необходимых полей (id и name), исключая email
     * для сохранения конфиденциальности и компактности данных.
     * </p>
     *
     * @param user сущность пользователя для преобразования
     * @return краткий DTO пользователя для использования в EventShortDto и аналогичных контекстах
     */
    static UserShortDto toUserForEventShotDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
