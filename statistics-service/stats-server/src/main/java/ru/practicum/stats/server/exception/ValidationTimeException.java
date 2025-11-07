package ru.practicum.stats.server.exception;

/**
 * Исключение, выбрасываемое при обнаружении ошибок валидации времени.
 * <p>
 * Это исключение используется для сигнализации о некорректных значениях времени,
 * таких как недопустимые даты, диапазоны или форматы, в контексте приложения.
 */
public class ValidationTimeException extends RuntimeException {

    /**
     * Создает новое исключение ValidationTimeException с указанным сообщением.
     *
     * @param message сообщение, описывающее причину исключения (не должно быть null)
     */
    public ValidationTimeException(String message) {
        super(message);
    }
}
