package ru.practicum.stats.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения StatsServer — сервиса статистики для ExploreWithMe.
 * <p>
 * Этот класс является точкой входа в Spring Boot приложение, которое предоставляет
 * REST API для сбора и анализа статистики просмотров событий. Приложение использует
 * базу данных PostgreSQL для хранения данных о хитов (просмотрах) и предоставляет
 * эндпоинты для сохранения статистики и получения сводок.
 * <p>
 * Класс аннотирован {@link org.springframework.boot.autoconfigure.SpringBootApplication},
 * что включает автоматическую конфигурацию Spring Boot, сканирование компонентов
 * и запуск встроенного сервера Tomcat на порту 9090 (по умолчанию).
 */
@SpringBootApplication
public class StatsServer {
    /**
     * Точка входа в приложение.
     * Метод запускает Spring Boot приложение, инициализируя контекст Spring,
     * подключая базу данных, настраивая JPA и стартуя веб-сервер.
     */
    public static void main(String[] args) {
        SpringApplication.run(StatsServer.class, args);
    }
}
