package ru.practicum.util;

/**
 * Класс с константами для API-путей.
 * Используется в контроллерах для избежания дублирования строк.
 * Содержит базовые пути API и шаблоны для работы с сущностями по идентификаторам.
 */
public class ApiPaths {

    public static final String CATEGORIES = "/categories";
    public static final String EVENTS = "/events";
    public static final String USERS = "/users";
    public static final String COMPILATIONS = "/compilations";
    public static final String REQUESTS = "/requests";

    public static final String CATEGORY_BY_ID = "/{catId}";
    public static final String EVENT_BY_ID = "/{eventId}";
    public static final String USER_BY_ID = "/{userId}";
    public static final String COMPILATION_BY_ID = "/{compId}";
    public static final String REQUESTS_BY_ID = "/{requestId}";

    public static final String ADMIN = "/admin";

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитного класса.
     */
    private ApiPaths() {
        throw new UnsupportedOperationException("Utility class");
    }
}
