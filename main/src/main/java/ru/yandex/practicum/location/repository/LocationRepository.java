package ru.yandex.practicum.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.location.model.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByLatAndLon(Float lat, Float lon);
}
