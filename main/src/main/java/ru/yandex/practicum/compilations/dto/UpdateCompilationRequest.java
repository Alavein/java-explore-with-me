package ru.yandex.practicum.compilations.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {

    @Size(min = 1, max = 50)
    String title;
    Boolean pinned;
    List<Integer> events;
}
