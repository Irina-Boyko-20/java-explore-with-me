package ru.practicum.stats.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая запись о хите (запросе) к эндпоинту сервиса.
 * Хранится в таблице "hits" базы данных и используется для сбора статистики
 * о посещениях различных URI приложения.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "hits")
public class EndpointHit {

    /**
     * Уникальный идентификатор записи о хите.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    /**
     * Название приложения, к которому относится хит.
     */
    @Column(name = "app", nullable = false)
    String app;

    /**
     * URI эндпоинта, к которому был выполнен запрос.
     */
    @Column(name = "uri", nullable = false)
    String uri;

    /**
     * IP-адрес клиента, с которого был выполнен запрос.
     */
    @Column(name = "ip", nullable = false, length = 15)
    String ip;

    /**
     * Время и дата выполнения запроса.
     */
    @Column(name = "timestamp", nullable = false)
    LocalDateTime timestamp;
}
