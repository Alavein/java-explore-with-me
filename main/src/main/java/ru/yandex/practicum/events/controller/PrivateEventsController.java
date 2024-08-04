package ru.yandex.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.events.dto.EventFullDto;
import ru.yandex.practicum.events.dto.EventShortDto;
import ru.yandex.practicum.events.dto.NewEventDto;
import ru.yandex.practicum.events.dto.UpdateEventUserRequest;
import ru.yandex.practicum.events.service.EventsService;
import ru.yandex.practicum.requests.dto.ParticipationRequestDto;
import ru.yandex.practicum.requests.dto.StatusUpdateRequest;
import ru.yandex.practicum.requests.dto.StatusUpdateResult;
import ru.yandex.practicum.requests.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {
    private final EventsService eventsService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventPrivate(@PathVariable Integer userId,
                                           @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Создание события {} по id пользователя = {}", newEventDto, userId);
        return eventsService.createEventPrivate(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> getEventsOfUser(@PathVariable Integer userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получения событий пользователя по Id = {}", userId);
        return eventsService.getEventsOfUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventsById(@PathVariable Integer userId,
                                      @PathVariable Integer eventId) {
        log.info("Получения событий по Id = {}", userId);
        return eventsService.getEventsById(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestOfEvent(@PathVariable Integer userId,
                                                           @PathVariable Integer eventId) {
        log.info("Получения запросов на событие по Id = {}", userId);
        return requestService.getRequestOfEvent(userId, eventId);

    }

    @PatchMapping("/{eventId}/requests")
    public StatusUpdateResult updateEventRequestsByEventOwner(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @Valid @RequestBody StatusUpdateRequest statusUpdateRequest) {
        log.info("Обновление запросов на событие по Id организатора.");
        return requestService.updateEventRequestsByEventOwner(userId, eventId, statusUpdateRequest);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventPrivate(
            @PathVariable Integer userId,
            @PathVariable Integer eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обновление события.");
        return eventsService.updateEventPrivate(userId, eventId, updateEventUserRequest);
    }
}
