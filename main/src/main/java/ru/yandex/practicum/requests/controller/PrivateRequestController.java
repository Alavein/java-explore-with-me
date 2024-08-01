package ru.yandex.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.requests.dto.ParticipationRequestDto;
import ru.yandex.practicum.requests.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequest(@PathVariable Integer userId,
                                                   @RequestParam Integer eventId) {
        log.info("Создание запроса по userId = {} и eventId = {}", userId, eventId);
        return requestService.addEventRequest(userId, eventId);
    }


    @GetMapping
    public List<ParticipationRequestDto> getEventRequestsById(@PathVariable Integer userId) {
        log.info("Получение запроса по userId = {}", userId);
        return requestService.getEventRequestsByRequester(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequest(@PathVariable Integer userId,
                                                      @PathVariable Integer requestId) {
        log.info("Обновление статуса запроса на cancel. Параметры: userId = {} и requestId = {}", userId, requestId);
        return requestService.cancelEventRequest(userId, requestId);
    }
}
