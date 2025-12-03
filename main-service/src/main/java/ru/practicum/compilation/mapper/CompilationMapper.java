package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.entity.Event;
import ru.practicum.event.mapper.EventMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Маппер для преобразования между сущностью подборки и DTO.
 * <p>
 * Обеспечивает конвертацию данных между слоями приложения:
 * из DTO в сущности для сохранения в БД и из сущностей в DTO для возврата в API.
 * Использует MapStruct для автоматической генерации кода преобразования.
 * </p>
 *
 * <p>
 * Содержит как статические методы для ручного маппинга, так и методы,
 * генерируемые MapStruct автоматически на основе аннотаций.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface CompilationMapper {

    /**
     * Преобразует DTO для создания подборки в сущность Compilation.
     * <p>
     * Используется при создании новой подборки. Идентификатор устанавливается в {@code null},
     * так как он будет сгенерирован базой данных при сохранении. Коллекция событий
     * инициализируется пустым HashSet.
     * </p>
     *
     * @param newCompilationDto DTO с данными для создания подборки
     * @return сущность Compilation с заполненными полями title и pinned
     */
    static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(null, newCompilationDto.getTitle(), newCompilationDto.isPinned(),
                new HashSet<>());
    }

    /**
     * Преобразует сущность подборки в DTO для ответа API.
     * <p>
     * Автоматически генерируется MapStruct на основе аннотаций {@link Mapping}.
     * Выполняет прямое копирование полей id, pinned и title.
     * </p>
     * <p>
     * <b>Внимание:</b> Для корректного маппинга коллекции событий требуется
     * дополнительная настройка или использование метода {@link #toDto(Compilation)}.
     * </p>
     *
     * @param compilation сущность подборки
     * @return DTO с данными подборки
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "pinned", target = "pinned")
    @Mapping(source = "title", target = "title")
    CompilationDto toCompilationDto(Compilation compilation);

    /**
     * Преобразует сущность подборки в DTO с ручным маппингом событий.
     * <p>
     * Выполняет полное преобразование сущности в DTO, включая преобразование
     * событий через {@link EventMapper#toEventShortDto(Event)}.
     * </p>
     * <p>
     * Обрабатывает случаи, когда коллекция событий равна {@code null} или пуста -
     * в этом случае возвращается пустой список.
     * </p>
     *
     * @param compilation сущность подборки для преобразования
     * @return DTO подборки с преобразованными событиями
     */
    static CompilationDto toDto(Compilation compilation) {
        List<EventShortDto> compilationEvents;
        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            compilationEvents = compilation.getEvents().stream()
                    .map(EventMapper::toEventShortDto).toList();
        } else compilationEvents = new ArrayList<>();
        return new CompilationDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.isPinned(),
                compilationEvents);
    }
}
