package ru.practicum.compilation.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.util.ApiPaths;

/**
 * Контроллер для административных операций с подборками событий.
 * <p>
 * Обеспечивает API для управления подборками событий администраторами системы.
 * Все endpoints требуют соответствующих прав доступа и доступны только
 * аутентифицированным пользователям с ролью администратора.
 * </p>
 *
 * <p>
 * Работает с подборками событий, предоставляя возможности:
 * </p>
 * <ul>
 * <li>Создание новых подборок</li>
 * <li>Удаление существующих подборок</li>
 * <li>Обновление данных подборок</li>
 * </ul>
 */
@RestController
@RequestMapping(ApiPaths.ADMIN + ApiPaths.COMPILATIONS)
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    /**
     * Сервис для работы с подборками событий.
     */
    public final CompilationService compilationService;

    /**
     * Создает новую подборку событий.
     *
     * @param dto DTO с данными для создания подборки
     * @return ResponseEntity с созданной подборкой и статусом 201 (Created)
     */
    @PostMapping
    public ResponseEntity<CompilationDto> addCompilation(@Valid @RequestBody NewCompilationDto dto) {
        return new ResponseEntity<>(compilationService.add(dto), HttpStatus.CREATED);
    }

    /**
     * Удаляет подборку событий по идентификатору.
     *
     * @param compId идентификатор подборки для удаления
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    @DeleteMapping(ApiPaths.COMPILATION_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

    /**
     * Обновляет данные существующей подборки событий.
     *
     * @param compId идентификатор обновляемой подборки
     * @param newCompilation DTO с данными для обновления
     * @return ResponseEntity с обновленной подборкой и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    @PatchMapping(ApiPaths.COMPILATION_BY_ID)
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable Long compId,
            @Valid @RequestBody UpdateCompilationRequest newCompilation
    ) {
        return new ResponseEntity<>(compilationService.update(compId, newCompilation), HttpStatus.OK);
    }
}
