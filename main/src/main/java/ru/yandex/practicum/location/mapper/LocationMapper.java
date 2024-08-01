package ru.yandex.practicum.location.mapper;

import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.dto.LocationDto;

public class LocationMapper {

    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }

/*    public LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }*/
}
