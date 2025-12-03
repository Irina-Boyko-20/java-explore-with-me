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
 * DTO для краткого представления события.
 * <p>
 * Используется для передачи основных данных о событии в сценариях, где
 * не требуется полная информация. Оптимизирован для отображения в списках,
 * карточках событий и подборках.
 * </p>
 *
 * <p>
 * Содержит только наиболее важные поля для быстрого ознакомления пользователя
 * с событием и принятия решения об участии или дальнейшем изучении.
 * </p>
 *
 * <p>
 * Поддерживает создание объектов через паттерн Builder для удобства тестирования
 * и использования в сервисных методах.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {

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
     * Количество подтвержденных заявок на участие.
     */
    Long confirmedRequests;

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

    /**
     * Количество просмотрев события.
     */
    Long views;
}
