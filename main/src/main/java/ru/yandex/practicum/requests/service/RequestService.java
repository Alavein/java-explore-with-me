package ru.yandex.practicum.requests.service;

import ru.yandex.practicum.requests.dto.StatusUpdateRequest;
import ru.yandex.practicum.requests.dto.StatusUpdateResult;
import ru.yandex.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getEventRequestsByRequester(Integer userId);

    ParticipationRequestDto createEventRequest(Integer userId, Integer eventId);

    ParticipationRequestDto cancelEventRequest(Integer userId, Integer requestId);

    List<ParticipationRequestDto> getRequestOfEvent(Integer userId, Integer eventId);

    StatusUpdateResult updateEventRequestsByEventOwner(Integer userId,
                                                       Integer eventId,
                                                       StatusUpdateRequest event);
}
