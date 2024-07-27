package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.EndpointHitDto;
import ru.yandex.practicum.ViewStatsDto;
import ru.yandex.practicum.exceptions.BadRequestException;
import ru.yandex.practicum.mappers.statsMapper;
import ru.yandex.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;


    @Override
    @Transactional
    public void createHit(EndpointHitDto endpointHitDto) {
        log.info("POST");
        statsRepository.save(statsMapper.toStats(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        checkTime(start, end);

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getAllStatsIp(start, end);
            } else {
                return statsRepository.getAllStats(start, end);
            }
        } else {
            if (unique) {
                return statsRepository.getStatsByUrisIp(start, end, uris);
            } else {
                return statsRepository.getStatsByUris(start, end, uris);
            }
        }
    }

    private void checkTime(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || end.equals(start)) {
            log.warn("GET");
            throw new BadRequestException("Ошибка. Дата введена неверно. Дата начала не может быть позже даты " +
                    "окончания");
        }
    }
}
