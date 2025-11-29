package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.user.entity.User;

/**
 * DTO для создания нового пользователя.
 * <p>
 * Используется для получения данных от клиента при создании нового пользователя.
 * Содержит валидационные аннотации для проверки корректности входящих данных
 * в соответствии с бизнес-требованиями системы.
 * </p>
 *
 * <p>
 * Применяется на уровне контроллеров для приема и первичной валидации данных
 * перед их преобразованием в сущность {@link User}.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {

    /**
     * Электронная почта пользователя.
     */
    @Email(message = "The 'email' field must contain a valid email address, such as user@example.com")
    @NotBlank(message = "The user's email must be specified")
    @Size(min = 6, max = 254, message = "User email length must be between {min} and {max} characters")
    String email;

    /**
     * Имя пользователя.
     */
    @NotBlank(message = "User name must be specified")
    @Size(min = 2, max = 250, message = "User name length must be between {min} and {max} characters")
    String name;
}
