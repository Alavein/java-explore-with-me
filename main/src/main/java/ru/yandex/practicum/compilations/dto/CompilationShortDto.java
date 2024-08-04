package ru.yandex.practicum.compilations.dto;
import ru.yandex.practicum.events.model.Event;

import java.util.List;

public interface CompilationShortDto {
    Integer getId();

    List<Event> getEvents();
}
