package ru.yandex.practicum.compilations.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.events.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    Integer id;
    Boolean pinned;
    String title;
    List<EventShortDto> events;
}
