package ru.practicum.stats.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.server.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST-контроллер для обработки запросов статистики просмотров эндпоинтов.
 * Предоставляет эндпоинты для сохранения информации о запросах и получения статистики просмотров.
 * Контроллер использует сервис {@link EndpointHitService} для бизнес-логики.
 * Включает валидацию входных данных и логирование запросов.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class EndpointHitController {
    private final EndpointHitService service;
    private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Сохраняет информацию о запросе к эндпоинту.
     * Принимает данные о запросе в теле запроса, валидирует их и сохраняет через сервис.
     * Возвращает сообщение об успешном сохранении.
     *
     * @param request DTO с информацией о запросе к эндпоинту, включая URI, IP-адрес и другие данные.
     *                Должен быть валидным согласно аннотациям в {@link EndpointHitDto}.
     * @return Сообщение "Информация сохранена" в случае успешного сохранения.
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EndpointHitDto> hit(@RequestBody @Valid EndpointHitDto request) {
        log.info("Stats-server: create hit request={}", request);
        return new ResponseEntity<>(service.saveHit(request), HttpStatus.CREATED);
    }

    /**
     * Получает статистику просмотров эндпоинтов за указанный период.
     * Возвращает список объектов со статистикой просмотров, включая URI, количество просмотров и другие метрики.
     * Поддерживает фильтрацию по списку URI и опцию учета уникальных IP-адресов.
     *
     * @param start Начало периода для статистики в формате "yyyy-MM-dd HH:mm:ss".
     * @param end Конец периода для статистики в формате "yyyy-MM-dd HH:mm:ss".
     * @param uris Список URI для фильтрации статистики (необязательный параметр). Если не указан, статистика по всем URI.
     * @param unique Флаг учета уникальных IP-адресов (true - только уникальные, false - все просмотры). По умолчанию false.
     * @return Список {@link ViewStatsDto} со статистикой просмотров за указанный период.
     */
    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = timeFormat) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = timeFormat) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) {
        log.info("Stats-server: get stats start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }
}
