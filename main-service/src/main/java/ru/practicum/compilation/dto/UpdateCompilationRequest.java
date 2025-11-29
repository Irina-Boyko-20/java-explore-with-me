package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

/**
 * DTO для обновления существующей подборки событий.
 * <p>
 * Используется для частичного обновления данных подборки. Все поля являются опциональными -
 * обновляются только те поля, которые явно указаны в запросе (patch-семантика).
 * </p>
 *
 * <p>
 * Содержит вспомогательные методы для проверки наличия значений в полях,
 * что позволяет определить, какие именно поля должны быть обновлены.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {

    /**
     * Новый заголовок подборки.
     */
    @Size(min = 1, max = 50, message = "Compilation title length must be between {min} and {max} characters")
    String title;

    /**
     * Новое значение флага закрепления на главной странице.
     */
    Boolean pinned;

    /**
     * Новый набор идентификаторов событий для подборки.
     */
    Set<Long> events;

    /**
     * Проверяет, указано ли значение для заголовка подборки.
     * <p>
     * Возвращает {@code true}, если поле {@code title} не равно {@code null}
     * и содержит непустую строку (после обрезки пробелов).
     * </p>
     *
     * @return {@code true} если заголовок указан и не пуст, {@code false} в противном случае
     */
    public boolean hasTitle() {
        return title != null && !title.isBlank();
    }

    /**
     * Проверяет, указано ли значение для флага закрепления.
     * <p>
     * Возвращает {@code true}, если поле {@code pinned} не равно {@code null}.
     * </p>
     *
     * @return {@code true} если флаг закрепления указан, {@code false} в противном случае
     */
    public boolean hasPinned() {
        return pinned != null;
    }

    /**
     * Проверяет, указан ли набор событий для обновления.
     * <p>
     * Возвращает {@code true}, если поле {@code events} не равно {@code null}.
     * </p>
     * <p>
     * Примечание: метод возвращает {@code true} даже для пустого множества,
     * что означает необходимость очистки подборки от всех событий.
     * </p>
     *
     * @return {@code true} если набор событий указан, {@code false} в противном случае
     */
    public boolean hasEvents() {
        return events != null;
    }
}
