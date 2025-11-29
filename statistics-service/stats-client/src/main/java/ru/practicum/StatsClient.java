package ru.practicum;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Клиент для взаимодействия с сервером статистики.
 * Этот класс наследуется от {@link BaseClient} и предоставляет методы для сохранения хитов
 * и получения статистики просмотров эндпоинтов.
 * Использует REST-шаблон для отправки HTTP-запросов к серверу статистики.
 */
@Component
public class StatsClient extends BaseClient {
    private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Имя приложения, которое отправляет статистику.
     */
    private static final String APPLICATION_NAME = "ewm-main-service";

    /**
     * Шаблон URI для API получения статистики.
     * Параметры: start, end, uris, unique.
     */
    private static final String API_PARAMETERS = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

    /**
     * Конструктор клиента для сервера статистики.
     * Инициализирует базовый клиент с указанным URL сервера и настраивает RestTemplate.
     *
     * @param serverUrl URL сервера статистики, получаемый из конфигурации.
     * @param builder   Строитель для создания RestTemplate.
     */
    @Autowired
    public StatsClient(@Value("${stats-service.url:http://localhost:9090}") String serverUrl,
                       RestTemplateBuilder builder
    ) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    /**
     * Сохраняет хит (запрос) на сервере статистики.
     * Создает объект {@link EndpointHitDto} на основе данных из HTTP-запроса
     * и отправляет его на эндпоинт "/hit" методом POST.
     *
     * @param request HTTP-запрос, из которого извлекаются URI, IP-адрес и временная метка.
     */
    public void saveHit(HttpServletRequest request) {
        final EndpointHitDto hit = EndpointHitDto.builder()
                .app(APPLICATION_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat)))
                .build();
        post("/hit", hit);
    }

    /**
     * Получает статистику просмотров эндпоинтов с сервера статистики.
     * Отправляет GET-запрос на эндпоинт "/stats" с параметрами фильтрации.
     *
     * @param start  Начальная дата и время в формате строки (например, "2023-01-01T00:00:00").
     * @param end    Конечная дата и время в формате строки (например, "2023-01-31T23:59:59").
     * @param uris   Список URI для фильтрации статистики. Может быть null или пустым.
     * @param unique Флаг, указывающий, учитывать ли только уникальные просмотры (true) или все (false).
     * @return Ответ от сервера в виде {@link ResponseEntity}, содержащий объект статистики.
     */
    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        return get(
                API_PARAMETERS,
                Map.of(
                        "start", start,
                        "end", end,
                        "uris", uris,
                        "unique", unique
                ));
    }
}
