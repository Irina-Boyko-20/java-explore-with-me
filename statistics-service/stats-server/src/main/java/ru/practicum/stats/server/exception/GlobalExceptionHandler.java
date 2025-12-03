package ru.practicum.stats.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * Перехватывает исключения, возникающие в процессе работы приложения,
 * и возвращает структурированные ответы в формате JSON.
 *
 * <p>Класс наследуется от {@link RuntimeException} для унификации обработки исключений,
 * но фактически используется как компонент Spring для обработки исключений.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    private static final int HTTP_STATUS_BAD_REQUEST = 400;

    /**
     * Обрабатывает исключения валидации времени {@link ValidationTimeException}.
     *
     * <p>Метод перехватывает исключения, связанные с некорректными временными параметрами,
     * и возвращает структурированный ответ с информацией об ошибке.</p>
     *
     * @param ex перехваченное исключение {@link ValidationTimeException}
     * @return объект {@link ResponseEntity} с телом ответа, содержащим информацию об ошибке,
     *         и статусом {@link HttpStatus#BAD_REQUEST}
     */
    @ExceptionHandler(ValidationTimeException.class)
    public ResponseEntity<Map<String, Object>> handleValidationTimeException(
            final ValidationTimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HTTP_STATUS_BAD_REQUEST);
        body.put("errorMessages", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
