package ru.practicum.request.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.EventRequestStatus;
import ru.practicum.user.entity.User;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая запрос на участие в событии.
 * <p>
 * Запросы на участие позволяют пользователям выражать желание принять участие
 * в событиях, созданных другими пользователями. Каждый запрос проходит
 * определенный жизненный цикл статусов (ожидание, подтверждено, отменено, отклонено).
 * </p>
 *
 * <p>
 * Связана с событиями ({@link Event}) и пользователями ({@link User}) через
 * идентификаторы, представляя отношение многие-к-одному в обоих направлениях.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@Table(name = "participation")
public class ParticipationRequest {

    /**
     * Уникальный идентификатор запроса на участие.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /**
     * Идентификатор события, для которого создан запрос на участие.
     */
    @Column(name = "event_id", nullable = false)
    private Long event;

    /**
     * Идентификатор пользователя, создавшего запрос на участие.
     */
    @Column(name = "requester_id", nullable = false)
    private Long requester;

    /**
     * Статус запроса на участие.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventRequestStatus status;

    /**
     * Дата и время создания запроса на участие.
     */
    @Column(name = "created", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}

