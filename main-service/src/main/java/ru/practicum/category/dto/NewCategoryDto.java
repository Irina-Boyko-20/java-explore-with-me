package ru.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.entity.Category;

/**
 * DTO для создания новой категории событий.
 * <p>
 * Используется для получения данных от клиента при создании новой категории.
 * Содержит валидационные аннотации для проверки корректности входящих данных.
 * </p>
 *
 * <p>
 * Применяется на уровне контроллеров для приема и первичной валидации данных
 * перед их преобразованием в сущность {@link Category}.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {

    /**
     * Наименование категории.
     */
    @NotBlank(message = "Category name must be specified")
    @Size(min = 1, max = 50, message = "Category name length must be between {min} and {max} characters")
    String name;
}
