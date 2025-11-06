package ru.practicum;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Класс, представляющий объект передачи данных (DTO) для статистики просмотров.
 * Используется для передачи информации о количестве просмотров конкретного URI в приложении.
 * Содержит поля для названия приложения, URI и количества просмотров.
 * Аннотации Lombok автоматически генерируют геттеры, сеттеры, equals, hashCode, toString,
 * конструктор без параметров и конструктор со всеми параметрами.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewStatsDto {

    /**
     * Название приложения, для которого собирается статистика просмотров.
     */
    String app;

    /**
     * URI ресурса, просмотры которого подсчитываются.
     */
    String uri;

    /**
     * Количество просмотров (хитов) для данного URI.
     */
    Long hits;
}
