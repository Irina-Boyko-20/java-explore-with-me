package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventCommentDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

/**
 * DTO-класс для передачи полных данных о комментарии в ответах API.
 * <p>
 * Содержит полную информацию о комментарии, включая его идентификатор,
 * текст, автора, связанное событие и время создания. Используется для
 * возврата данных клиенту при запросах на получение комментариев.
 * </p>
 * <p>
 * Все поля объявлены как private с помощью Lombok аннотации
 * {@code @FieldDefaults(level = AccessLevel.PRIVATE)}.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDto {

    /**
     * Уникальный идентификатор комментария.
     */
    Long id;

    /**
     * Текст комментария.
     */
    String text;

    /**
     * Автор комментария.
     */
    UserShortDto authorName;

    /**
     * Событие, к которому относится комментарий.
     */
    EventCommentDto event;

    /**
     * Дата создания комментария.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
}
