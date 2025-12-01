package ru.practicum.compilation.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.entity.Event;

import java.util.Set;

/**
 * Сущность, представляющая подборку событий.
 * <p>
 * Подборки позволяют группировать события по определенным темам или критериям
 * для удобного представления пользователям. Подборка может быть закреплена
 * на главной странице для повышенной видимости.
 * </p>
 *
 * <p>
 * Связь с событиями организована через отношение многие-ко-многим
 * с использованием промежуточной таблицы {@code compilation_event}.
 * Загрузка событий выполняется лениво (LAZY) для оптимизации производительности.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "compilations")
public class Compilation {

    /**
     * Уникальный идентификатор подборки.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /**
     * Заголовок подборки.
     */
    @Column(name = "title", nullable = false, length = 50, unique = true)
    String title;

    /**
     * Флаг закрепления подборки на главной странице.
     */
    @Column(name = "pinned", nullable = false)
    boolean pinned;

    /**
     * Множество событий, входящих в подборку.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    Set<Event> events;
}
