package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.event.entity.Event;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static ru.practicum.compilation.mapper.CompilationMapper.toCompilation;

/**
 * Реализация сервиса для работы с подборками событий.
 * <p>
 * Предоставляет конкретную реализацию бизнес-логики для операций с подборками:
 * создание, удаление, обновление, получение данных и валидация.
 * Использует транзакции для обеспечения целостности данных.
 * </p>
 *
 * <p>
 * Взаимодействует с репозиториями {@link CompilationRepository} и {@link EventRepository}
 * для доступа к данным и маппером {@link CompilationMapper} для преобразования
 * между сущностями и DTO.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    /**
     * Репозиторий для работы с подборками событий.
     */
    public final CompilationRepository compilationRepository;

    /**
     * Репозиторий для работы с событиями.
     */
    public final EventRepository eventRepository;

    /**
     * Маппер для преобразования между сущностями и DTO подборок.
     */
    public final CompilationMapper mapper;

    /**
     * Создает новую подборку событий на основе предоставленных данных.
     * <p>
     * Выполняет валидацию уникальности заголовка, преобразует DTO в сущность,
     * обрабатывает привязку событий к подборке и сохраняет в базе данных.
     * </p>
     *
     * @param dto DTO с данными для создания подборки
     * @return DTO созданной подборки с присвоенным идентификатором
     * @throws ru.practicum.exception.ConflictException если подборка с таким заголовком уже существует
     * @throws ru.practicum.exception.NotFoundException если указанные события не найдены
     */
    @Override
    public CompilationDto add(NewCompilationDto dto) {
        log.debug("CompilationServiceImpl: dto= {}", dto);
        titleExists(dto.getTitle());

        Compilation compilation = toCompilation(dto);
        log.debug("compilation= {}", compilation);
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            List<Event> events = checkEvents(dto.getEvents());
            compilation.setEvents(new HashSet<>(events));
        } else compilation.setEvents(new HashSet<>());
        log.debug("After add compilation= {}", compilation);

        return mapper.toCompilationDto(compilationRepository.save(compilation));
    }

    /**
     * Удаляет подборку по идентификатору после проверки её существования.
     * <p>
     * Выполняет проверку наличия подборки перед удалением. Если подборка не найдена,
     * генерируется исключение. Операция удаления выполняется безвозвратно.
     * </p>
     *
     * @param compId идентификатор подборки для удаления
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    @Override
    public void delete(Long compId) {
        compilationExists(compId);
        compilationRepository.deleteById(compId);
    }

    /**
     * Обновляет данные существующей подборки событий.
     * <p>
     * Выполняет частичное обновление подборки - изменяются только те поля,
     * которые явно указаны в запросе. Проверяет уникальность нового заголовка
     * и существование указанных событий.
     * </p>
     *
     * @param compId идентификатор обновляемой подборки
     * @param newCompilation DTO с данными для обновления
     * @return DTO обновленной подборки
     * @throws ru.practicum.exception.NotFoundException если подборка или события не найдены
     * @throws ru.practicum.exception.ConflictException если новый заголовок уже используется
     */
    @Transactional
    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest newCompilation) {
        Compilation compilation = compilationExists(compId);
        if (newCompilation.hasTitle() && !newCompilation.getTitle().equals(compilation.getTitle())) {
            titleExists(newCompilation.getTitle());
            compilation.setTitle(newCompilation.getTitle());
        }
        if (newCompilation.hasPinned()) {
            compilation.setPinned(newCompilation.getPinned());
        }
        if (newCompilation.hasEvents()) {
            List<Event> events = checkEvents(newCompilation.getEvents());
            compilation.setEvents(new HashSet<>(events));
        }

        Compilation saved = compilationRepository.save(compilation);

        return CompilationMapper.toDto(saved);
    }

    /**
     * Возвращает список подборок событий с поддержкой фильтрации и пагинации.
     * <p>
     * Предоставляет возможность получения как всех подборок, так и фильтрации
     * по статусу закрепления. Использует пагинацию для ограничения объема данных.
     * </p>
     *
     * @param pinned опциональный параметр фильтрации по статусу закрепления
     * @param from начальная позиция в списке (offset)
     * @param size количество элементов на странице (limit)
     * @return список DTO подборок. Если подборки не найдены, возвращается пустой список
     */
    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        PageRequest pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        }

        return compilations.stream()
                .map(mapper::toCompilationDto)
                .toList();
    }

    /**
     * Возвращает подборку событий по идентификатору.
     * <p>
     * Загружает подборку и все связанные с ней события. Для оптимизации
     * производительности использует отдельный запрос для загрузки идентификаторов
     * событий с последующей загрузкой полных данных о событиях.
     * </p>
     *
     * @param compId идентификатор запрашиваемой подборки
     * @return DTO подборки с полной информацией о событиях
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    @Transactional(readOnly = true)
    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationExists(compId);
        List<Long> eventIds = compilationRepository.findEventIdsByCompilationId(compId);

        if (!eventIds.isEmpty()) {
            List<Event> events = eventRepository.findAllById(eventIds);
            compilation.setEvents(new HashSet<>(events));
        } else {
            compilation.setEvents(new HashSet<>());
        }

        return mapper.toCompilationDto(compilation);
    }

    /**
     * Проверяет уникальность заголовка подборки.
     * <p>
     * Выполняет проверку существования подборки с указанным заголовком.
     * Используется при создании и обновлении подборок для предотвращения
     * дублирования названий.
     * </p>
     *
     * @param title заголовок подборки для проверки
     * @throws ru.practicum.exception.ConflictException если подборка с таким заголовком уже существует
     */
    @Override
    public void titleExists(String title) {
        if (compilationRepository.existsByTitle(title)) {
            throw new ConflictException("could not execute statement; SQL [n/a];" +
                    " constraint " + title + "; nested exception is org.hibernate.exception." +
                    "ConstraintViolationException: could not execute statement");
        }
    }

    /**
     * Проверяет существование подборки по идентификатору.
     * <p>
     * Вспомогательный метод для валидации наличия подборки в базе данных.
     * Используется в других методах сервиса перед выполнением операций
     * с конкретной подборкой.
     * </p>
     *
     * @param compId идентификатор подборки для проверки
     * @return сущность подборки, если найдена
     * @throws ru.practicum.exception.NotFoundException если подборка с указанным ID не найдена
     */
    @Override
    public Compilation compilationExists(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=%d was not found".formatted(compId)));
    }

    /**
     * Проверяет существование и загружает события по их идентификаторам.
     * <p>
     * Выполняет проверку, что все указанные идентификаторы событий существуют
     * в базе данных. Если хотя бы одно событие не найдено, генерируется исключение.
     * </p>
     *
     * @param eventsId множество идентификаторов событий
     * @return список сущностей событий
     * @throws ru.practicum.exception.NotFoundException если не все события найдены
     */
    private List<Event> checkEvents(Set<Long> eventsId) {
        List<Event> events = eventRepository.findAllById(eventsId);
        if (events.size() != eventsId.size()) {
            throw new NotFoundException("Not all events were found");
        }
        return events;
    }
}
