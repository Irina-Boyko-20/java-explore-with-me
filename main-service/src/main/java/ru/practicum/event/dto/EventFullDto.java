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
 * DTO для полного представления события.
 * <p>
 * Используется для передачи полных данных о событии в сценариях, где
 * требуется исчерпывающая информация. Содержит все атрибуты события
 * для отображения на детальной странице и в административных интерфейсах.
 * </p>
 *
 * <p>
 * Включает как базовую информацию о событии, так и мета-данные, статистику
 * и административные атрибуты, необходимые для полного понимания контекста события.
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
public class EventFullDto {

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
     * Заголовок события.
     */
    String title;

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
     * Местоположение события.
     */
    LocationDto location;

    //Location or LocationDto

    /**
     * Флаг платности события.
     */
    Boolean paid;

    /**
     * Количество подтвержденных заявок на участие.
     */
    Long confirmedRequests;

    /**
     * Дата и время создания события.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    /**
     * Полное описание события.
     */
    String description;

    /**
     * Ограничение на количество участников.
     */
    Integer participantLimit;

    /**
     * Дата и время публикации события
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    /**
     * Требуется ли пре-модерация заявок на участие.
     */
    Boolean requestModeration;

    /**
     * Состояние жизненного цикла события.
     */
    String state;

    /**
     * Количество просмотров события.
     */
    Integer views;

    /**
     * Количество комментариев события.
     */
    Integer comments;
}
