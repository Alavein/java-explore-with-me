package ru.yandex.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.events.dto.EventFullDto;
import ru.yandex.practicum.events.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.events.status.EventStatus;
import ru.yandex.practicum.events.service.EventsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
public class AdminEventsController {

    private final EventsService eventsService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Integer eventId,
                                           @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обновление события по id = {}", eventId);
        return eventsService.updateEventByIdAdmin(eventId, updateEventAdminRequest);
    }

    @GetMapping
    public List<EventFullDto> getEventsOfAdmin(
            @RequestParam(required = false) List<Integer> users,
            @RequestParam(required = false) List<EventStatus> states,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение события по параметрам.");
        return eventsService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

}
