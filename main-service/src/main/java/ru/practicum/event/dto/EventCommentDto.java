package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * DTO-класс для передачи краткой информации о событии с дополнительным комментарием.
 * Содержит основные данные о событии, необходимые для отображения в списках или в интерфейсах,
 * где требуется краткое представление события.
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventCommentDto {

    /**
     * Уникальный идентификатор события.
     */
    Long id;

    /**
     * Кратное описание события.
     */
    String annotation;

    /**
     * Категория события.
     */
    CategoryDto category;

    /**
     * Дата и время проведения события.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    /**
     * Инициатор события.
     */
    UserShortDto initiator;

    /**
     * Флаг платности события.
     */
    Boolean paid;

    /**
     * Заголовок события.
     */
    String title;
}
