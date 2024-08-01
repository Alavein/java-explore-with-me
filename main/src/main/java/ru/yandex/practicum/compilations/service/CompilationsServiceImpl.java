package ru.yandex.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilations.mapper.CompilationMapper;
import ru.yandex.practicum.compilations.model.Compilation;
import ru.yandex.practicum.compilations.dto.CompilationDto;
import ru.yandex.practicum.compilations.dto.CompilationShortDto;
import ru.yandex.practicum.compilations.dto.NewCompilationDto;
import ru.yandex.practicum.compilations.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilations.repository.CompilationRepository;
import ru.yandex.practicum.events.mapper.EventMapper;
import ru.yandex.practicum.events.model.Event;
import ru.yandex.practicum.events.dto.EventShortDto;
import ru.yandex.practicum.events.repository.EventsRepository;
import ru.yandex.practicum.exceptions.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationsServiceImpl implements CompilationsService {

    private final CompilationRepository compilationRepository;
    private final EventsRepository eventsRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {

        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventsRepository.findAllByIdIn(newCompilationDto.getEvents());
        }

        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto,
                events));

        List<EventShortDto> eventsShortDto = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        log.info("Сохранение подборки.");
        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilationById(Integer compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Подборка не найдена."));

        List<Event> events = new ArrayList<>();

        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            events = eventsRepository.findAllByIdIn(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        compilationRepository.save(compilation);

        List<EventShortDto> eventsShortDto = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Обновление подборки с id = {}", compId);
        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<Compilation> compilations;
        List<CompilationDto> compilationsDto = new ArrayList<>();

        if (pinned == null) {
            compilations = compilationRepository.findAll(page).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        }

        List<Integer> comp = new ArrayList<>();
        for (Compilation compilation : compilations) {
            comp.add(compilation.getId());
        }

        Map<Integer, List<Event>> compilationsShortDto = compilationRepository.findAllByIdIn(comp)
                .stream()
                .collect(Collectors.toMap(CompilationShortDto::getId, CompilationShortDto::getEvents));

        for (Compilation compilation : compilations) {
            List<EventShortDto> eventsShortDto = compilationsShortDto.get(compilation.getId())
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());

            compilationsDto.add(CompilationMapper.toCompilationDto(compilation, eventsShortDto));
        }
        log.info("Получение всех подборок.");
        return compilationsDto;
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Подборка не найдена."));

        List<EventShortDto> eventsShortDto = compilation.getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Получение подборки по id = {}", compId);
        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Integer compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Подборка не найдена."));

        log.info("Удаление по id = {}", compId);
        compilationRepository.deleteById(compId);
    }
}
