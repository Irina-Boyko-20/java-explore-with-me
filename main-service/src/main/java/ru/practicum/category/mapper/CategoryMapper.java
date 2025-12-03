package ru.practicum.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.entity.Category;

/**
 * Маппер для преобразования между сущностью категории и DTO.
 * <p>
 * Обеспечивает конвертацию данных между слоями приложения:
 * из DTO в сущности для сохранения в БД и из сущностей в DTO для возврата в API.
 * Использует MapStruct для автоматической генерации кода преобразования.
 * </p>
 *
 * <p>
 * Содержит как методы, генерируемые MapStruct автоматически на основе аннотаций,
 * так и статические методы для ручного маппинга в специфических сценариях.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Преобразует DTO для создания категории в сущность Category.
     * <p>
     * Используется при создании новой категории. Идентификатор игнорируется,
     * так как он будет сгенерирован базой данных при сохранении.
     * </p>
     *
     * @param newCategoryDto DTO с данными для создания категории
     * @return сущность Category с заполненным полем name
     */
    @Mapping(target = "id", ignore = true)
    Category toCategory(NewCategoryDto newCategoryDto);

    /**
     * Преобразует сущность категории в DTO для ответа API.
     * <p>
     * Автоматически генерируется MapStruct. Выполняет преобразование
     * всех полей сущности в соответствующие поля DTO.
     * </p>
     *
     * @param category сущность категории для преобразования
     * @return DTO категории с данными для ответа
     */
    CategoryDto toCategoryDto(Category category);

    /**
     * Обновляет сущность категории из DTO с новыми данными.
     * <p>
     * Используется для частичного обновления категории. Игнорирует поле id,
     * так как идентификатор категории не может быть изменен.
     * </p>
     * <p>
     * Применяет стратегию игнорирования null-значений - обновляются только
     * те поля, которые явно указаны в DTO и не равны null.
     * </p>
     *
     * @param category сущность категории для обновления
     * @param newCategory DTO с новыми данными для обновления
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(@MappingTarget Category category, NewCategoryDto newCategory);

    /**
     * Преобразует сущность категории в DTO для использования в краткой информации о событии.
     * <p>
     * Специализированный метод для создания DTO категории, который используется
     * при формировании краткой информации о событии (EventShortDto).
     * </p>
     * <p>
     * Выполняет ручной маппинг полей, обеспечивая необходимую структуру данных
     * для вложенного представления категории в событиях.
     * </p>
     *
     * @param category сущность категории для преобразования
     * @return DTO категории для использования в EventShortDto
     */
    static CategoryDto toCategoryForEventShotDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}
