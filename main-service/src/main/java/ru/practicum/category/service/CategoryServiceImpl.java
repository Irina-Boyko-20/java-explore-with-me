package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.entity.Category;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.entity.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

/**
 * Реализация сервиса для работы с категориями событий.
 * <p>
 * Предоставляет конкретную реализацию бизнес-логики для операций с категориями:
 * создание, удаление, обновление, получение данных и валидация.
 * Обеспечивает целостность данных и соблюдение бизнес-правил при работе с категориями.
 * </p>
 *
 * <p>
 * Взаимодействует с репозиториями {@link CategoryRepository} и {@link EventRepository}
 * для доступа к данным и маппером {@link CategoryMapper} для преобразования
 * между сущностями и DTO.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    /**
     * Репозиторий для работы с категориями событий.
     */
    public final CategoryRepository categoryRepository;

    /**
     * Репозиторий для работы с событиями.
     * <p>
     * Используется для проверки связанных событий при удалении категорий.
     * </p>
     */
    public final EventRepository eventRepository;

    /**
     * Маппер для преобразования между сущностями и DTO категорий.
     */
    public final CategoryMapper mapper;

    /**
     * Создает новую категорию событий на основе предоставленных данных.
     * <p>
     * Выполняет валидацию уникальности наименования, преобразует DTO в сущность
     * и сохраняет в базе данных. Возвращает созданную категорию с присвоенным идентификатором.
     * </p>
     *
     * @param dto DTO с данными для создания категории
     * @return DTO созданной категории с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если категория с таким наименованием уже существует
     */
    @Override
    public CategoryDto add(NewCategoryDto dto) {
        categoryExists(dto.getName());
        return mapper.toCategoryDto(categoryRepository.save(mapper.toCategory(dto)));
    }

    /**
     * Удаляет категорию по идентификатору после проверки её существования и связанных событий.
     * <p>
     * Выполняет проверку наличия категории и проверяет, что с категорией не связано
     * ни одного события. Если найдены связанные события, удаление блокируется.
     * </p>
     *
     * @param catId идентификатор категории для удаления
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.ConflictException если категория содержит связанные события
     *         и не может быть удалена
     */
    @Override
    public void delete(Long catId) {
        categoryExists(catId);
        List<Event> categoryEvents = eventRepository.findByCategoryId(catId);
        if (!categoryEvents.isEmpty()) {
            throw new ConflictException("You cannot delete an event that contains events.");
        }
        categoryRepository.deleteById(catId);
    }

    /**
     * Обновляет данные существующей категории событий.
     * <p>
     * Выполняет обновление наименования категории. Проверяет уникальность
     * нового наименования (если оно изменилось) и существование категории.
     * Использует маппер для частичного обновления полей.
     * </p>
     *
     * @param catId идентификатор обновляемой категории
     * @param newCategory DTO с новыми данными для категории
     * @return DTO обновленной категории
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     * @throws ru.practicum.exception.ConflictException если новое наименование уже используется другой категорией
     */
    @Transactional
    @Override
    public CategoryDto update(Long catId, NewCategoryDto newCategory) {
        Category category = categoryExists(catId);
        if (newCategory.getName() != null && !newCategory.getName().equals(category.getName())) {
            categoryExists(newCategory.getName());
        }

        mapper.updateCategory(category, newCategory);
        return mapper.toCategoryDto(categoryRepository.save(category));
    }

    /**
     * Возвращает список категорий событий с поддержкой пагинации.
     * <p>
     * Предоставляет возможность постраничного просмотра всех категорий.
     * Использует пагинацию для ограничения объема возвращаемых данных.
     * </p>
     *
     * @param from количество категорий, которые нужно пропустить (offset)
     * @param size количество категорий в возвращаемом наборе (limit)
     * @return список DTO категорий. Если категории не найдены, возвращается пустой список
     */
    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream()
                .map(mapper::toCategoryDto)
                .toList();
    }

    /**
     * Возвращает категорию событий по идентификатору.
     * <p>
     * Предоставляет полную информацию о категории. Использует метод проверки
     * существования категории, который гарантирует, что категория существует.
     * </p>
     *
     * @param catId идентификатор запрашиваемой категории
     * @return DTO категории с детальной информацией
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     */
    @Transactional(readOnly = true)
    @Override
    public CategoryDto getById(Long catId) {
        return mapper.toCategoryDto(categoryExists(catId));
    }

    /**
     * Проверяет уникальность наименования категории.
     * <p>
     * Выполняет проверку существования категории с указанным наименованием.
     * Используется при создании и обновлении категорий для предотвращения
     * дублирования наименований.
     * </p>
     *
     * @param name наименование категории для проверки
     * @throws ru.practicum.exception.ConflictException если категория с таким наименованием уже существует
     */
    @Override
    public void categoryExists(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException("could not execute statement; SQL [n/a];" +
                    " constraint " + name + "; nested exception is org.hibernate.exception." +
                    "ConstraintViolationException: could not execute statement");
        }
    }

    /**
     * Проверяет существование категории по идентификатору.
     * <p>
     * Вспомогательный метод для валидации наличия категории в базе данных.
     * Используется в других методах сервиса перед выполнением операций
     * с конкретной категорией.
     * </p>
     *
     * @param catId идентификатор категории для проверки
     * @return сущность категории, если найдена
     * @throws ru.practicum.exception.NotFoundException если категория с указанным ID не найдена
     */
    @Override
    public Category categoryExists(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=%d was not found".formatted(catId)));
    }
}
