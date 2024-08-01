package ru.yandex.practicum.compilations.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.compilations.model.Compilation;
import ru.yandex.practicum.compilations.dto.CompilationShortDto;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    List<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);

    List<CompilationShortDto> findAllByIdIn(List<Integer> compId);
}
