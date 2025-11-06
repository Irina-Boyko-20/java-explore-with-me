package ru.practicum;

import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Базовый класс для HTTP-клиентов, предоставляющий общие методы для отправки запросов
 * с использованием RestTemplate. Этот класс предназначен для наследования и упрощения
 * взаимодействия с внешними API.
 */
public class BaseClient {

    /**
     * Экземпляр RestTemplate, используемый для выполнения HTTP-запросов.
     * Этот объект должен быть предоставлен при создании экземпляра класса.
     */
    protected final RestTemplate rest;

    /**
     * Конструктор базового клиента.
     *
     * @param rest экземпляр RestTemplate для выполнения HTTP-запросов.
     *             Не должен быть null, иначе поведение не определено.
     */
    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    /**
     * Приватный статический метод для подготовки ответа шлюза.
     * Если статус-код ответа успешен (2xx), возвращает оригинальный ответ.
     * В противном случае создает новый ResponseEntity с тем же статус-кодом и телом (если оно есть).
     *
     * @param response оригинальный ResponseEntity от сервера.
     * @return подготовленный ResponseEntity, готовый для возврата клиенту.
     */
    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    /**
     * Защищенный метод для выполнения и отправки HTTP-запроса с использованием RestTemplate.
     * Поддерживает различные HTTP-методы, тело запроса и параметры пути.
     * В случае исключения HttpStatusCodeException возвращает ResponseEntity с ошибкой.
     * Иначе подготавливает и возвращает ответ через prepareGatewayResponse.
     *
     * @param method     HTTP-метод (например, GET, POST).
     * @param path       путь к ресурсу (может содержать плейсхолдеры для параметров).
     * @param body       тело запроса (может быть null для методов без тела).
     * @param parameters карта параметров для замены в пути (может быть null, если параметров нет).
     * @param <T>        тип тела запроса.
     * @return ResponseEntity с объектом или байтовым массивом в случае ошибки.
     */
    protected <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable T body, @Nullable Map<String, Object> parameters) {
        HttpEntity<T> requestEntity = (body != null) ? new HttpEntity<>(body) : null;

        ResponseEntity<Object> serverResponse;
        try {
            if (parameters != null) {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                serverResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(serverResponse);
    }

    /**
     * Защищенный метод для отправки POST-запроса.
     * Использует makeAndSendRequest для выполнения запроса без параметров.
     *
     * @param path путь к ресурсу.
     * @param body тело запроса (не null для POST).
     * @param <T>  тип тела запроса.
     */
    protected <T> void post(String path, T body) {
        makeAndSendRequest(HttpMethod.POST, path, body, null);
    }

    /**
     * Защищенный метод для отправки GET-запроса с параметрами.
     * Использует makeAndSendRequest для выполнения запроса.
     *
     * @param path       путь к ресурсу (может содержать плейсхолдеры).
     * @param parameters карта параметров для замены в пути.
     * @return ResponseEntity с результатом запроса.
     */
    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, null, parameters);
    }
}
