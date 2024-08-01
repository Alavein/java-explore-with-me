package ru.yandex.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.yandex.practicum.events.model.Event;
import ru.yandex.practicum.users.model.User;

import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiator(User user, Pageable pageable);

    List<Event> findAllByIdIn(List<Integer> eventsId);

}
