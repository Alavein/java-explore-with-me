package ru.yandex.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.requests.dto.ConfirmedRequests;
import ru.yandex.practicum.requests.model.Request;
import ru.yandex.practicum.requests.model.status.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    @Query("SELECT new ru.yandex.practicum.requests.dto.ConfirmedRequests(r.event.id, count(r.id)) " +
            "FROM Request AS r " +
            "WHERE r.event.id IN ?1 " +
            "AND r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id")
    List<ConfirmedRequests> getConfirmedRequests(List<Integer> eventsId);

    List<Request> findAllByRequesterId(Integer requesterId);

    Optional<Request> findByEventIdAndRequesterId(Integer eventId, Integer userId);

    List<Request> findAllByEventIdAndStatusEquals(Integer eventId, RequestStatus status);

    List<Request> findAllByEventId(Integer eventId);

    List<Request> findAllByIdIn(List<Integer> requestIds);

}