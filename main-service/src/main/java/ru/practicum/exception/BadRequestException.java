package ru.practicum.exception;

/**
 * Исключение, указывающее на некорректный или невалидный запрос от клиента.
 * <p>
 * Используется в сценариях, когда клиент отправляет запрос с некорректными данными,
 * которые не проходят бизнес-валидацию или нарушают семантику API.
 * </p>
 *
 * <p>
 * Автоматически обрабатывается {@link GlobalExceptionHandler} и преобразуется
 * в HTTP-ответ со статусом 400 (Bad Request) с соответствующим сообщением об ошибке.
 * </p>
 */
public class BadRequestException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение об ошибке, объясняющее причину
     *                некорректности запроса и, если возможно, способ исправления.
     *                Пример: "Parameter 'from' must be non-negative"
     */
    public BadRequestException(String message) {
        super(message);
    }
}
