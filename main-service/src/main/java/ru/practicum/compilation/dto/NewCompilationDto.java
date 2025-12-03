package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.compilation.entity.Compilation;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO для создания новой подборки событий.
 * <p>
 * Используется для получения данных от клиента при создании новой подборки.
 * Содержит валидационные аннотации для проверки корректности входящих данных.
 * Все поля являются опциональными, за исключением обязательного заголовка.
 * </p>
 *
 * <p>
 * Применяется на уровне контроллеров для приема и первичной валидации данных
 * перед их преобразованием в сущность {@link Compilation}.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    /**
     * Заголовок подборки.
     */
    @NotBlank(message = "Compilation title must be specified")
    @Size(min = 1, max = 50, message = "Compilation title length must be between {min} and {max} characters")
    String title;

    /**
     * Флаг закрепления подборки на главной странице сайта.
     */
    boolean pinned;

    /**
     * Множество идентификаторов событий, включаемых в подборку.
     */
    Set<Long> events = new HashSet<>();
}
