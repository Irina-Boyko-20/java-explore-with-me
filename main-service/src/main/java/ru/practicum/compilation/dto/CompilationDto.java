package ru.practicum.compilation.dto;

import ru.practicum.event.dto.EventShortDto;

import java.util.List;

/**
 * DTO для представления подборки событий.
 * <p>
 * Используется для передачи данных о подборке между слоями приложения,
 * в частности - для возврата данных в API-ответах. Содержит основную информацию
 * о подборке и краткие сведения о входящих в нее событиях.
 * </p>
 *
 * <p>
 * Является иммутабельным объектом (record), что обеспечивает потокобезопасность
 * и предсказуемость поведения в многопоточных сценариях.
 * </p>
 *
 * @param id Уникальный идентификатор подборки
 * @param title Заголовок подборки
 * @param pinned Флаг закрепления на главной странице
 * @param events Список кратких описаний событий, входящих в подборку
 */
public record CompilationDto(Long id, String title, boolean pinned, List<EventShortDto> events) {
}
