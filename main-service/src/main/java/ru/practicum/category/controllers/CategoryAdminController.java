package ru.practicum.category.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.util.ApiPaths;

/**
 * Контроллер для административных операций с категориями событий.
 * <p>
 * Обеспечивает API для управления категориями событий администраторами системы.
 * Все endpoints требуют соответствующих прав доступа и доступны только
 * аутентифицированным пользователям с ролью администратора.
 * </p>
 *
 * <p>
 * Работает с категориями событий, предоставляя возможности:
 * </p>
 * <ul>
 * <li>Создание новых категорий</li>
 * <li>Удаление существующих категорий</li>
 * <li>Обновление данных категорий</li>
 * </ul>
 *
 * <p>
 * Все методы выполняют валидацию входящих данных и возвращают соответствующие
 * HTTP-статусы в зависимости от результата операции.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.ADMIN + ApiPaths.CATEGORIES)
@RequiredArgsConstructor
public class CategoryAdminController {

    /**
     * Сервис для работы с категориями событий.
     */
    public final CategoryService categoryService;

    /**
     * Создает новую категорию событий.
     *
     * @param dto DTO с данными для создания категории
     * @return ResponseEntity с созданной категорией и статусом 201 (Created)
     * @throws ru.practicum.exception.ConflictException если категория с таким названием уже существует
     */
    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto dto) {
        return new ResponseEntity<>(categoryService.add(dto), HttpStatus.CREATED);
    }

    /**
     * Удаляет категорию событий по идентификатору.
     *
     * @param catId идентификатор категории для удаления
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.ConflictException если категория не может быть удалена
     *         (например, существуют связанные события)
     */
    @DeleteMapping(ApiPaths.CATEGORY_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.delete(catId);
    }

    /**
     * Обновляет данные существующей категории событий.
     *
     * @param catId идентификатор обновляемой категории
     * @param dto DTO с новыми данными для категории
     * @return ResponseEntity с обновленной категорией и статусом 200 (OK)
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.ConflictException если новое название уже используется другой категорией
     */
    @PatchMapping(ApiPaths.CATEGORY_BY_ID)
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long catId,
                                                      @Valid @RequestBody NewCategoryDto dto) {
        return new ResponseEntity<>(categoryService.update(catId, dto), HttpStatus.OK);
    }
}
