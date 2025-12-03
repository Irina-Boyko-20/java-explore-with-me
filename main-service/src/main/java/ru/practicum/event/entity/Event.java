package ru.practicum.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.entity.Category;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность, представляющая событие в системе.
 * <p>
 * События являются основным контентом платформы - они создаются пользователями
 * и представляют различные мероприятия, активности или встречи. Каждое событие
 * проходит определенный жизненный цикл от создания до публикации и завершения.
 * </p>
 *
 * <p>
 * Связана с категориями ({@link Category}), пользователями ({@link User}),
 * локациями ({@link Location}) и подборками ({@link Compilation}).
 * Поддерживает систему участия пользователей через запросы на участие.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "events")
public class Event {

    /**
     * Уникальный идентификатор события.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /**
     * Кратное описание события.
     */
    @Column(name = "annotation", nullable = false, length = 2000)
    String annotation;

    /**
     * Категория события.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    /**
     * Заголовок события.
     */
    @Column(name = "title", nullable = false)
    String title;

    /**
     * Дата и время проведения события.
     */
    @Column(name = "event_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    /**
     * Инициатор события.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    /**
     * Географическое местоположение события.
     */
    @Embedded
    Location location;

    /**
     * Флаг платности события.
     */
    @Column(name = "paid", nullable = false)
    Boolean paid;

    /**
     * Количество подтвержденных заявок на участие.
     */
    @Column(name = "confirmed_requests", nullable = false)
    @Builder.Default
    Long confirmedRequests = 0L;

    /**
     * Дата и время создания события.
     */
    @Column(name = "created_on", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

    /**
     * Полное описание события.
     */
    @Column(name = "description", nullable = false, length = 7000)
    String description;

    /**
     * Ограничение на количество участников.
     */
    @Column(name = "participant_limit")
    @Builder.Default
    Integer participantLimit = 0;

    /**
     * Дата и время публикации события
     */
    @Column(name = "published_on")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;

    /**
     * Требуется ли пре-модерация заявок на участие.
     */
    @Column(name = "request_moderation")
    @Builder.Default
    Boolean requestModeration = true;

    /**
     * Состояние жизненного цикла события.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Builder.Default
    EventState state = EventState.PENDING;

    /**
     * Количество просмотров события.
     */
    @Column(name = "views", nullable = false)
    @Builder.Default
    Long views = 0L;

    /**
     * Подборки, в которые включено данное событие.
     */
    @ManyToMany(mappedBy = "events")
    @ToString.Exclude
    @Builder.Default
    private Set<Compilation> compilations = new HashSet<>();
}
