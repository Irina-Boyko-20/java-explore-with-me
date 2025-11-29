package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.entity.Compilation;

import java.util.List;

/**
 * Сервис для работы с подборками событий.
 * <p>
 * Предоставляет бизнес-логику для операций с подборками событий, включая
 * создание, удаление, обновление и получение данных. Обеспечивает валидацию
 * данных и обработку исключительных ситуаций.
 * </p>
 *
 * <p>
 * Служит прослойкой между контроллерами и репозиторием, инкапсулируя
 * сложную бизнес-логику и гарантируя целостность данных.
 * </p>
 */
public interface CompilationService {

    /**
     * Создает новую подборку событий.
     *
     * @param dto DTO с данными для создания подборки
     * @return DTO созданной подборки с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если подборка с таким заголовком уже существует
     */
    CompilationDto add(NewCompilationDto dto);

    /**
     * Удаляет подборку событий по идентификатору.
     *
     * @param compId идентификатор удаляемой подборки
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    void delete(Long compId);

    /**
     * Обновляет данные существующей подборки событий.
     *
     * @param compId идентификатор обновляемой подборки
     * @param newCompilation DTO с данными для обновления
     * @return DTO обновленной подборки
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     * @throws ru.practicum.exception.ConflictException если новый заголовок уже используется другой подборкой
     */
    CompilationDto update(Long compId, UpdateCompilationRequest newCompilation);

    /**
     * Возвращает список подборок событий с поддержкой фильтрации и пагинации.
     *
     * @param pinned опциональный параметр фильтрации:
     *              - {@code true} - только закрепленные подборки
     *              - {@code false} - только незакрепленные подборки
     *              - {@code null} - все подборки без фильтрации
     * @param from количество подборок, которые нужно пропустить (offset)
     * @param size количество подборок в возвращаемом наборе (limit)
     * @return список DTO подборок. Если подборки не найдены, возвращается пустой список
     */
    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    /**
     * Возвращает подборку событий по идентификатору.
     *
     * @param compId идентификатор запрашиваемой подборки
     * @return DTO подборки с детальной информацией
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    CompilationDto getById(Long compId);

    /**
     * Проверяет уникальность заголовка подборки.
     *
     * @param title заголовок для проверки
     * @throws ru.practicum.exception.ConflictException если подборка с таким заголовком уже существует
     */
    void titleExists(String title);

    /**
     * Проверяет существование подборки по идентификатору.
     *
     * @param compId идентификатор подборки для проверки
     * @return сущность подборки, если найдена
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    Compilation compilationExists(Long compId);
}
