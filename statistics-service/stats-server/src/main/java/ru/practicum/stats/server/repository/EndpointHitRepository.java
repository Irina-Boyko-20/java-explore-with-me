package ru.practicum.stats.server.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с сущностью EndpointHit.
 * Предоставляет методы для получения статистики просмотров эндпоинтов,
 * включая уникальные IP-адреса и общее количество просмотров.
 * Расширяет JpaRepository для базовых операций CRUD с сущностью EndpointHit.
 */
public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    /**
     * Получает статистику просмотров эндпоинтов с уникальными IP-адресами для заданных URI.
     * Запрос фильтрует данные по списку URI и временному диапазону, группирует по приложению и URI,
     * подсчитывает уникальные IP-адреса и сортирует по убыванию количества уникальных просмотров.
     *
     * @param uris  список URI для фильтрации (не может быть null)
     * @param start начальная дата и время диапазона (включительно)
     * @param end   конечная дата и время диапазона (включительно)
     * @return список объектов ViewStatsDto со статистикой просмотров
     */
    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.uri IN (?1) AND h.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findHitsWithUniqueIpWithUris(List<String> uris, String start, String end);

    /**
     * Получает статистику просмотров эндпоинтов с уникальными IP-адресами для всех URI.
     * Запрос фильтрует данные по временному диапазону, группирует по приложению и URI,
     * подсчитывает уникальные IP-адреса и сортирует по убыванию количества уникальных просмотров.
     *
     * @param start начальная дата и время диапазона (включительно)
     * @param end   конечная дата и время диапазона (включительно)
     * @return список объектов ViewStatsDto со статистикой просмотров
     */
    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> findHitsWithUniqueIpWithoutUris(LocalDateTime start, LocalDateTime end);

    /**
     * Получает общую статистику просмотров эндпоинтов для заданных URI.
     * Запрос фильтрует данные по списку URI и временному диапазону, группирует по приложению и URI,
     * подсчитывает общее количество просмотров (включая повторные с одного IP) и сортирует по убыванию.
     *
     * @param start начальная дата и время диапазона (включительно)
     * @param end   конечная дата и время диапазона (включительно)
     * @param uris  список URI для фильтрации (не может быть null)
     * @return список объектов ViewStatsDto со статистикой просмотров
     */
    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN (:uris) " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> findAllHits(@Param("start") String start,
                                   @Param("end") String end,
                                   @Param("uris") List<String> uris);

    /**
     * Получает общую статистику просмотров эндпоинтов для всех URI.
     * Запрос фильтрует данные по временному диапазону, группирует по приложению и URI,
     * подсчитывает общее количество просмотров (включая повторные с одного IP) и сортирует по убыванию.
     *
     * @param start начальная дата и время диапазона (включительно)
     * @param end   конечная дата и время диапазона (включительно)
     * @return список объектов ViewStatsDto со статистикой просмотров
     */
    @Query("SELECT new ru.practicum.ViewStatsDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsDto> findAllHitsWithoutUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
