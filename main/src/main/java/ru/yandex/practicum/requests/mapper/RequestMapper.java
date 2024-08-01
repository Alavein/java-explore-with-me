package ru.yandex.practicum.requests.mapper;

import ru.yandex.practicum.requests.model.Request;
import ru.yandex.practicum.requests.dto.ParticipationRequestDto;

public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();

    }
}
