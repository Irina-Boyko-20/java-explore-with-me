package ru.practicum.category.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Публичный контроллер для работы с категориями событий.
 * <p>
 * Предоставляет API для получения информации о категориях событий.
 * Доступен всем пользователям без необходимости аутентификации.
 * Предназначен для отображения категорий на публичных страницах приложения
 * и использования в пользовательском интерфейсе для фильтрации событий.
 * </p>
 *
 * <p>
 * Обеспечивает возможности для:
 * </p>
 * <ul>
 * <li>Получения списка категорий с пагинацией</li>
 * <li>Получения детальной информации о конкретной категории по идентификатору</li>
 * </ul>
 *
 * <p>
 * Все методы возвращают только данные для чтения и не изменяют состояние системы.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.CATEGORIES)
@RequiredArgsConstructor
public class CategoryPublicController {

    /**
     * Сервис для работы с категориями событий.
     */
    public final CategoryService categoryService;

    /**
     * Возвращает список категорий событий с поддержкой пагинации.
     *
     * @param from начальная позиция в списке для пагинации (по умолчанию 0)
     * @param size количество категорий в возвращаемом наборе (по умолчанию 10)
     * @return ResponseEntity со списком DTO категорий и статусом 200 (OK).
     *         Если категории не найдены, возвращается пустой список.
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(categoryService.getAll(from, size), HttpStatus.OK);
    }

    /**
     * Возвращает детальную информацию о категории событий по идентификатору.
     *
     * @param catId идентификатор запрашиваемой категории
     * @return ResponseEntity с DTO категории и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     */
    @GetMapping(ApiPaths.CATEGORY_BY_ID)
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long catId) {
        return new ResponseEntity<>(categoryService.getById(catId), HttpStatus.OK);
    }
}
