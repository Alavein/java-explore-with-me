package ru.yandex.practicum.service;

import ru.yandex.practicum.mapper.StatsMapper;

import java.time.LocalDateTime;
import java.util.List;

public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional
    public void createHit(EndpointHit endpointHit) {
        statsRepository.save(statsMapper.toStats(endpointHit));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (start.isAfter(end) || start.isEqual(end)) {
            throw new BadRequestException("Ошибка. Неверно заполнены даты.");
        }

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getAllStatsUniqueIp(start, end);
            } else {
                return statsRepository.getAllStats(start, end);
            }
        } else {
            if (unique) {
                return statsRepository.getStatsByUrisUniqueIp(start, end, uris);
            } else {
                return statsRepository.getStatsByUris(start, end, uris);
            }
        }
    }
}
