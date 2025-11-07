package ru.practicum.stats.server.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.server.exception.ValidationTimeException;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для обработки хитов эндпоинтов и получения статистики просмотров.
 * Этот класс предоставляет методы для сохранения информации о хите и получения агрегированной статистики
 * на основе заданных параметров, таких как временной диапазон, URI и уникальность IP-адресов.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EndpointHitService {
    private final EndpointHitRepository repository;

    /**
     * Сохраняет информацию о хите эндпоинта в репозитории.
     * Создает новую сущность {@link EndpointHit} на основе переданного DTO и сохраняет ее.
     * Логирует процесс сохранения для отладки.
     *
     * @param request DTO с данными о хите, включая приложение, URI, IP и временную метку.
     */
    public void saveHit(EndpointHitDto request) {
        EndpointHit entity = new EndpointHit();
        entity.setApp(request.getApp());
        entity.setUri(request.getUri());
        entity.setIp(request.getIp());
        entity.setTimestamp(request.getTimestamp());
        log.info("Saving entity: {}", entity);

        EndpointHit saved = repository.save(entity);
        log.info("Saved entity with ID: {}, timestamp: {}", saved.getId(), saved.getTimestamp());
    }

    /**
     * Получает статистику просмотров за заданный временной диапазон.
     * Возвращает список DTO со статистикой, фильтрованный по URI и уникальности IP-адресов.
     * Валидирует входные параметры: start и end не могут быть null, start не может быть после end.
     *
     * @param start начальная дата и время для фильтрации (включительно).
     * @param end конечная дата и время для фильтрации (включительно).
     * @param uris список URI для фильтрации; если null или пустой, фильтрация по URI не применяется.
     * @param unique флаг, указывающий, учитывать ли только уникальные IP-адреса (true) или все хиты (false).
     * @return список {@link ViewStatsDto} с агрегированной статистикой просмотров.
     * @throws ValidationTimeException если start или end равны null, или start находится после end.
     */
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null) {
            throw new ValidationTimeException("Start/end cannot be is empty!");
        }
        if (start.isAfter(end)) {
            throw new ValidationTimeException("Start cannot be after end.");
        }

        log.info("Service: processing stats with unique={}, uris={}", unique, uris);

        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                return repository.findHitsWithUniqueIpWithUris(uris, start, end);
            } else {
                return repository.findHitsWithUniqueIpWithoutUris(start, end);
            }
        } else {
            if (uris != null && !uris.isEmpty()) {
                return repository.findAllHits(start, end, uris);
            } else {
                return repository.findAllHitsWithoutUris(start, end);
            }
        }
    }
}
