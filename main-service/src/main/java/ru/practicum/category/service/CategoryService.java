package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.entity.Category;

import java.util.List;

/**
 * Сервис для работы с категориями событий.
 * <p>
 * Предоставляет бизнес-логику для операций с категориями событий, включая
 * создание, удаление, обновление и получение данных. Обеспечивает валидацию
 * данных, проверку уникальности наименований и обработку исключительных ситуаций.
 * </p>
 *
 * <p>
 * Служит прослойкой между контроллерами и репозиторием, инкапсулируя
 * сложную бизнес-логику и гарантируя целостность данных категорий.
 * </p>
 */
public interface CategoryService {

    /**
     * Создает новую категорию событий.
     *
     * @param dto DTO с данными для создания категории
     * @return DTO созданной категории с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если категория с таким наименованием уже существует
     */
    CategoryDto add(NewCategoryDto dto);

    /**
     * Удаляет категорию событий по идентификатору.
     *
     * @param catId идентификатор удаляемой категории
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.ConflictException если категория не может быть удалена
     *         (существуют связанные события)
     */
    void delete(Long catId);

    /**
     * Обновляет данные существующей категории событий.
     *
     * @param catId идентификатор обновляемой категории
     * @param dto DTO с новыми данными для категории
     * @return DTO обновленной категории
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.ConflictException если новое наименование уже используется другой категорией
     */
    CategoryDto update(Long catId, NewCategoryDto dto);

    /**
     * Возвращает список категорий событий с поддержкой пагинации.
     *
     * @param from количество категорий, которые нужно пропустить (offset)
     * @param size количество категорий в возвращаемом наборе (limit)
     * @return список DTO категорий. Если категории не найдены, возвращается пустой список
     */
    List<CategoryDto> getAll(Integer from, Integer size);

    /**
     * Возвращает категорию событий по идентификатору.
     *
     * @param catId идентификатор запрашиваемой категории
     * @return DTO категории с детальной информацией
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     */
    CategoryDto getById(Long catId);

    /**
     * Проверяет уникальность наименования категории.
     *
     * @param name наименование для проверки
     * @throws ru.practicum.exception.ConflictException если категория с таким наименованием уже существует
     */
    void categoryExists(String name);

    /**
     * Проверяет существование категории по идентификатору.
     *
     * @param catId идентификатор категории для проверки
     * @return сущность категории, если найдена
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     */
    Category categoryExists(Long catId);
}
