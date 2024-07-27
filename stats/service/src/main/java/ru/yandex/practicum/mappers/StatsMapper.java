package ru.yandex.practicum.mappers;

import ru.yandex.practicum.EndpointHitDto;
import ru.yandex.practicum.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {

    public static Stats toStats(EndpointHitDto endpointHitDto) {
        return Stats.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(LocalDateTime.parse(endpointHitDto.getTimestamp(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
