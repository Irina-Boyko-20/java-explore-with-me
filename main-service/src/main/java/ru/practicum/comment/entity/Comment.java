package ru.practicum.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.entity.Event;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая комментарий к событию.
 * <p>
 * Комментарии позволяют пользователям оставлять отзывы и обсуждения
 * по поводу событий. Каждый комментарий привязан к конкретному событию
 * и пользователю-автору.
 * </p>
 * <p>
 * Сущность сохраняется в таблице "comments" базы данных и содержит
 * информацию о содержании комментария, авторе, событии и времени создания.
 * Использует стратегию ленивой загрузки для связанных сущностей
 * {@link Event} и {@link User} для оптимизации производительности.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "comments")
public class Comment {

    /**
     * Уникальный идентификатор комментария.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @EqualsAndHashCode.Include
    Long id;

    /**
     * Событие, к которому относится комментарий.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    Event event;

    /**
     * Текст комментария.
     */
    @Column(name = "text", nullable = false)
    String text;

    /**
     * Имя автора комментария.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User authorName;

    /**
     * Время создания комментария. По умолчанию - текущее время.
     */
    @Builder.Default
    @Column(name = "create_date", nullable = false)
    LocalDateTime created = LocalDateTime.now();
}
