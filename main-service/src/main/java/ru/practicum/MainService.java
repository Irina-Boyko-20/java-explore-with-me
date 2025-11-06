package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс приложения MainService.
 * Этот класс является точкой входа в Spring Boot приложение.
 * Аннотация @SpringBootApplication включает в себя @Configuration, @EnableAutoConfiguration и @ComponentScan,
 * что позволяет автоматически конфигурировать приложение, включать автоконфигурацию Spring Boot
 * и сканировать компоненты в пакете и подпакетах этого класса.
 */
@SpringBootApplication
public class MainService {
    /**
     * Точка входа в приложение.
     * Метод main запускает Spring Boot приложение, создавая и инициализируя контекст приложения.
     * @param args аргументы командной строки, передаваемые приложению при запуске
     */
    public static void main(String[] args) {
        SpringApplication.run(MainService.class, args);
    }
}