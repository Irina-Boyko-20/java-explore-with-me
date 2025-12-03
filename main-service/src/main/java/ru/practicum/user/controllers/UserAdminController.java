package ru.practicum.user.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.service.UserService;
import ru.practicum.util.ApiPaths;

import java.util.List;

/**
 * Контроллер для административных операций с пользователями.
 * <p>
 * Обеспечивает API для управления пользователями системы администраторами.
 * Все endpoints требуют соответствующих прав доступа и доступны только
 * аутентифицированным пользователям с ролью администратора.
 * </p>
 *
 * <p>
 * Работает с пользователями системы, предоставляя возможности:
 * </p>
 * <ul>
 * <li>Получение списка пользователей с фильтрацией и пагинацией</li>
 * <li>Создание новых пользователей</li>
 * <li>Удаление существующих пользователей</li>
 * </ul>
 *
 * <p>
 * Все методы выполняют валидацию входящих данных и возвращают соответствующие
 * HTTP-статусы в зависимости от результата операции.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.ADMIN + ApiPaths.USERS)
@RequiredArgsConstructor
public class UserAdminController {

    /**
     * Сервис для работы с пользователями.
     */
    public final UserService userService;

    /**
     * Возвращает список пользователей с поддержкой фильтрации и пагинации.
     *
     * @param ids опциональный список идентификаторов пользователей для фильтрации.
     *           Если параметр не указан, возвращаются все пользователи.
     * @param from начальная позиция в списке для пагинации (по умолчанию 0)
     * @param size количество пользователей в возвращаемом наборе (по умолчанию 10)
     * @return ResponseEntity со списком DTO пользователей и статусом 200 (OK).
     *         Если пользователи не найдены, возвращается пустой список.
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) List<Long> ids,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(userService.get(ids, from, size), HttpStatus.OK);
    }

    /**
     * Создает нового пользователя.>
     *
     * @param dto DTO с данными для создания пользователя
     * @return ResponseEntity с созданным пользователем и статусом 201 (Created)
     * @throws ru.practicum.exception.ConflictException если пользователь с таким email уже существует
     */
    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody NewUserRequest dto) {
        return new ResponseEntity<>(userService.add(dto), HttpStatus.CREATED);
    }

    /**
     * Удаляет пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя для удаления
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    @DeleteMapping(ApiPaths.USER_BY_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
