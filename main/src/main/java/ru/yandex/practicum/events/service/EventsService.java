package ru.yandex.practicum.events.service;

import ru.yandex.practicum.events.dto.*;
import ru.yandex.practicum.events.model.Event;
import ru.yandex.practicum.events.status.SortStatus;
import ru.yandex.practicum.events.status.EventStatus;
import ru.yandex.practicum.events.model.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventsService {

    EventFullDto createEventPrivate(Integer userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsOfUser(Integer userId, Integer from, Integer size);

    EventFullDto getEventsById(Integer userId, Integer eventId);

    EventFullDto updateEventPrivate(Integer userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getEventsAdmin(List<Integer> users,
                                      List<EventStatus> states,
                                      List<Integer> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from,
                                      Integer size);

    EventFullDto updateEventByIdAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getEventsPublic(String text,
                                        List<Integer> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        SortStatus sort,
                                        Integer from,
                                        Integer size,
                                        HttpServletRequest request);

    EventFullDto getEventByIdPublic(Integer id, HttpServletRequest request);

    Event getEvent(Integer eventId);
}
