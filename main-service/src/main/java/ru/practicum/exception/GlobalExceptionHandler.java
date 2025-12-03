package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST API.
 * <p>
 * Перехватывает и обрабатывает исключения, возникающие в контроллерах,
 * преобразуя их в структурированные HTTP-ответы с соответствующими статусами
 * и информацией об ошибках. Обеспечивает единообразный формат ответов
 * при возникновении различных типов исключительных ситуаций.
 * </p>
 *
 * <p>
 * Все ответы содержат временную метку, HTTP-статус и детали ошибки
 * в стандартизированном формате.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
    private static final int HTTP_STATUS_BAD_REQUEST = 400;
    private static final int HTTP_STATUS_FORBIDDEN = 403;
    private static final int HTTP_STATUS_NOT_FOUND = 404;
    private static final int HTTP_STATUS_CONFLICT = 409;

    /**
     * Обрабатывает исключения валидации входных данных.
     * <p>
     * Перехватывает исключения, возникающие при нарушении валидационных правил
     * DTO объектов. Собирает все сообщения об ошибках из BindingResult
     * и возвращает их в структурированном виде.
     * </p>
     *
     * @param ex исключение валидации данных
     * @return ResponseEntity с информацией об ошибках валидации и статусом 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleExceptions(
            final MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HTTP_STATUS_BAD_REQUEST);

        // Собираем все сообщения ошибок в список
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        body.put("error", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает исключения "Ресурс не найден".
     * <p>
     * Перехватывает ситуации, когда запрашиваемый ресурс (пользователь, событие,
     * категория и т.д.) не существует в системе.
     * </p>
     *
     * @param ex исключение "не найдено"
     * @return ResponseEntity с информацией об ошибке и статусом 404 (Not Found)
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(
            final NotFoundException ex
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HTTP_STATUS_NOT_FOUND);
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Обрабатывает исключения "Доступ запрещен".
     * <p>
     * Перехватывает ситуации, когда пользователь пытается выполнить операцию,
     * для которой у него недостаточно прав (например, изменение чужого события).
     * </p>
     *
     * @param ex исключение "доступ запрещен"
     * @return ResponseEntity с информацией об ошибке и статусом 403 (Forbidden)
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbiddenException(
            final ForbiddenException ex
    ) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HTTP_STATUS_FORBIDDEN);
        body.put("errorMessages", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    /**
     * Обрабатывает исключения "Конфликт операций".
     * <p>
     * Перехватывает ситуации, когда запрошенная операция конфликтует
     * с текущим состоянием системы (например, попытка удалить категорию
     * со связанными событиями или публикация уже опубликованного события).
     * </p>
     *
     * @param ex исключение "конфликт"
     * @return ResponseEntity с информацией об ошибке и статусом 409 (Conflict)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflictException(
            final ConflictException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HTTP_STATUS_CONFLICT);
        body.put("errorMessages", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    /**
     * Обрабатывает исключения "Некорректный запрос".
     * <p>
     * Перехватывает ситуации, когда клиент отправляет некорректные данные
     * или параметры запроса (например, отрицательный лимит участников
     * или неверный формат даты).
     * </p>
     *
     * @param ex исключение "некорректный запрос"
     * @return ResponseEntity с информацией об ошибке и статусом 400 (Bad Request)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(
            final BadRequestException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HTTP_STATUS_BAD_REQUEST);
        body.put("errorMessages", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
