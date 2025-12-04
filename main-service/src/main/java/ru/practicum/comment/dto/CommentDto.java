package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * DTO-класс для передачи данных комментария при создании или обновлении.
 * <p>
 * Используется как тело запроса при создании нового комментария или
 * редактировании существующего. Содержит только текстовое содержание
 * комментария, так как остальные атрибуты (автор, событие, время создания)
 * устанавливаются автоматически на основе контекста запроса.
 * </p>
 * <p>
 * Включает валидационные аннотации для обеспечения корректности данных:
 * проверка на пустоту, минимальную и максимальную длину текста.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    /**
     * Текст комментария.
     */
    @NotBlank(message = "Comment text must be specified")
    @Size(min = 2, max = 2000, message = "Comment text length must be between {min} and {max} characters")
    String text;
}
