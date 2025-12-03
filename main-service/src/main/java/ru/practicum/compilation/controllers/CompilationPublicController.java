package ru.practicum.compilation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Публичный контроллер для работы с подборками событий.
 * <p>
 * Предоставляет API для получения информации о подборках событий.
 * Доступен всем пользователям без необходимости аутентификации.
 * Предназначен для отображения подборок на публичных страницах приложения.
 * </p>
 *
 * <p>
 * Обеспечивает возможности для:
 * </p>
 * <ul>
 * <li>Получения списка подборок с возможностью фильтрации и пагинации</li>
 * <li>Получения детальной информации о конкретной подборке по идентификатору</li>
 * </ul>
 */
@RestController
@RequestMapping(ApiPaths.COMPILATIONS)
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {

    /**
     * Сервис для работы с подборками событий.
     */
    public final CompilationService compilationService;

    /**
     * Возвращает список подборок событий с поддержкой фильтрации и пагинации.
     *
     * @param pinned опциональный параметр фильтрации по статусу закрепления:
     *              <ul>
     *              <li>{@code true} - только закрепленные подборки</li>
     *              <li>{@code false} - только незакрепленные подборки</li>
     *              <li>не указан - все подборки без фильтрации</li>
     *              </ul>
     * @param from начальная позиция в списке для пагинации (по умолчанию 0)
     * @param size количество элементов на страницу (по умолчанию 10)
     * @return ResponseEntity со списком DTO подборок и статусом 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false) boolean pinned,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(compilationService.getAll(pinned, from, size), HttpStatus.OK);
    }

    /**
     * Возвращает детальную информацию о подборке событий по идентификатору.
     *
     * @param compId идентификатор запрашиваемой подборки
     * @return ResponseEntity с DTO подборки и статусом 200 (OK)
     */
    @GetMapping(ApiPaths.COMPILATION_BY_ID)
    public ResponseEntity<CompilationDto> getById(@PathVariable Long compId) {
        return new ResponseEntity<>(compilationService.getById(compId), HttpStatus.OK);
    }
}
