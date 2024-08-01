package ru.yandex.practicum.events.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ViewStatsDto;
import ru.yandex.practicum.categories.mapper.CategoryMapper;
import ru.yandex.practicum.categories.model.Category;
import ru.yandex.practicum.categories.service.CategoriesServiceImpl;
import ru.yandex.practicum.events.dto.*;
import ru.yandex.practicum.events.mapper.EventMapper;
import ru.yandex.practicum.events.model.Event;
import ru.yandex.practicum.events.repository.EventsRepository;
import ru.yandex.practicum.events.status.EventStatus;
import ru.yandex.practicum.events.status.SortStatus;
import ru.yandex.practicum.exceptions.BadRequestException;
import ru.yandex.practicum.exceptions.ConflictException;
import ru.yandex.practicum.exceptions.DataNotFoundException;
import ru.yandex.practicum.location.mapper.LocationMapper;
import ru.yandex.practicum.location.model.Location;
import ru.yandex.practicum.location.repository.LocationRepository;
import ru.yandex.practicum.requests.model.status.RequestStatus;
import ru.yandex.practicum.requests.repository.RequestRepository;
import ru.yandex.practicum.users.model.User;
import ru.yandex.practicum.users.service.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import ru.yandex.practicum.events.model.QEvent;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventsServiceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final UserServiceImpl userService;
    private final CategoriesServiceImpl categoriesService;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsServiceImpl statsService;
    private static final QEvent qEvent = QEvent.event;



    @Override
    public List<EventShortDto> getEventsOfUser(Integer userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        User user = userService.getUserById(userId);

        List<Event> events = eventsRepository.findAllByInitiator(user, page);

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Long> views = getViews(events);

        List<EventShortDto> eventsShortDto = new ArrayList<>();
        for (Event event : events) {
            EventShortDto i = EventMapper.toEventShortDto(event);
            i.setConfirmedRequests(confirmedRequests.getOrDefault(i.getId(), 0));
            i.setViews(Math.toIntExact(views.getOrDefault(i.getId(), 0L)));
            eventsShortDto.add(i);
        }

        return eventsShortDto;
    }

    @Override
    public EventFullDto getEventsById(Integer userId, Integer eventId) {
        userService.getUserById(userId);

        Event event = getEvent(eventId);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventPrivate(Integer userId, Integer eventId,
                                           UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest.getEventDate() != null &&
                updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Ошибка. Дата начала события не может быть ранее текущего времени +2 часа");
        }

        userService.getUserById(userId);

        Event event = getEvent(eventId);

        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Ошибка. Нельзя обновить событие, находящееся в статусе PUBLISHED.");
        }

        if (updateEventUserRequest.getAnnotation() != null && !updateEventUserRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoriesService.getCategoryById(updateEventUserRequest
                    .getCategory())));
        }

        if (updateEventUserRequest.getDescription() != null && !updateEventUserRequest.getDescription().isBlank()) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(locationRepository.findByLatAndLon(updateEventUserRequest.getLocation().getLat(),
                            updateEventUserRequest.getLocation().getLon())
                    .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(updateEventUserRequest
                            .getLocation()))));
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventStatus.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventStatus.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getTitle() != null && !updateEventUserRequest.getTitle().isBlank()) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        eventsRepository.save(event);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto createEventPrivate(Integer userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Ошибка. Дата начала события не может быть ранее текущего времени +2 часа");
        }

        User user = userService.getUserById(userId);

        Category category = CategoryMapper.toCategory(categoriesService.getCategoryById(newEventDto.getCategory()));

        Location location = locationRepository.findByLatAndLon(newEventDto.getLocation().getLat(),
                        newEventDto.getLocation().getLon())
                .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation())));

        Event event = EventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStatus.PENDING);

        Event event1 = eventsRepository.save(event);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event1);
        eventFullDto.setConfirmedRequests(0);
        eventFullDto.setViews(0);

        return eventFullDto;
    }


    @Override
    public List<EventFullDto> getEventsAdmin(List<Integer> users, List<EventStatus> states, List<Integer> categories,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                             Integer size) {
        GetAdminEvent getAdminEvent = new GetAdminEvent(users, states, categories, rangeStart, rangeEnd);
        BooleanExpression conditions = makeAdminEventQueryFilters(getAdminEvent);

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Event> events = eventsRepository.findAll(conditions, page).toList();

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Long> views = getViews(events);

        List<EventFullDto> eventsFullDto = new ArrayList<>();

        for (Event event : events) {
            EventFullDto i = EventMapper.toEventFullDto(event);
            i.setConfirmedRequests(confirmedRequests.getOrDefault(i.getId(), 0));
            i.setViews(Math.toIntExact(views.getOrDefault(i.getId(), 0L)));
            eventsFullDto.add(i);
        }

        return eventsFullDto;
    }

    @Override
    @Transactional
    public EventFullDto updateEventByIdAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Ошибка. Дата начала события не может быть ранее текущего времени +1 часа");
        }

        Event event = getEvent(eventId);

        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Ошибка. Нельзя обновить событие, находящеемся в статусе PUBLISHED.");
        }

        if (updateEventAdminRequest.getAnnotation() != null && !updateEventAdminRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(CategoryMapper.toCategory(categoriesService.getCategoryById(updateEventAdminRequest
                    .getCategory())));
        }

        if (updateEventAdminRequest.getDescription() != null && !updateEventAdminRequest.getDescription().isBlank()) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationRepository.findByLatAndLon(updateEventAdminRequest.getLocation().getLat(),
                            updateEventAdminRequest.getLocation().getLon())
                    .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(updateEventAdminRequest
                            .getLocation()))));
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventStatus.PENDING)) {
                throw new ConflictException("Ошибка. У события долженн быть стутус ожидания.");
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventStatus.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventStatus.CANCELED);
                    break;
            }
        }

        if (updateEventAdminRequest.getTitle() != null && !updateEventAdminRequest.getTitle().isBlank()) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        eventsRepository.save(event);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Integer> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                               SortStatus sort, Integer from, Integer size, HttpServletRequest request) {
        if (rangeEnd != null && rangeStart != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("Ошибка. Дата окончания не может быть раньше даты его начала.");
        }

        GetUserEvent getUserEvent = new GetUserEvent(text, categories, paid, rangeStart, rangeEnd);
        BooleanExpression conditions = makeUserEventQueryFilters(getUserEvent);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        statsService.createHit(request);

        List<Event> events = eventsRepository.findAll(conditions, page)
                .stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());

        if (events.isEmpty()) {
            return List.of();
        }

        List<EventShortDto> eventsShortDto = new ArrayList<>();
        Map<Integer, Integer> confirmedRequests = getConfirmedRequests(events);
        Map<Integer, Long> views = getViews(events);

        if (onlyAvailable) {
            eventsShortDto = events
                    .stream()
                    .filter(event -> (event.getParticipantLimit() == 0 ||
                            event.getParticipantLimit() > confirmedRequests.get(event.getId())))
                    .map(EventMapper::toEventShortDto)
                    .peek(i -> i.setConfirmedRequests(confirmedRequests.get(i.getId())))
                    .peek(i -> i.setViews(Math.toIntExact(views.getOrDefault(i.getId(), 0L))))
                    .collect(Collectors.toList());
        } else {
            eventsShortDto = events
                    .stream()
                    .map(EventMapper::toEventShortDto)
                    .peek(i -> i.setConfirmedRequests(confirmedRequests.getOrDefault(i.getId(), 0)))
                    .peek(i -> i.setViews(Math.toIntExact(views.getOrDefault(i.getId(), 0L))))
                    .collect(Collectors.toList());
        }

        if (sort.equals(SortStatus.VIEWS)) {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getViews));
        } else {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        return eventsShortDto;
    }

    @Override
    public EventFullDto getEventByIdPublic(Integer id, HttpServletRequest request) {
        Event event = getEvent(id);

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new DataNotFoundException("Ошибка. Событие не опубликовано");
        }

        statsService.createHit(request);

        return setViewsAndConfirmedRequestsToEventFullDto(event);
    }

    @Override
    public Event getEvent(Integer eventId) {

        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Событие не найдено."));
    }

    public static BooleanExpression makeUserEventQueryFilters(GetUserEvent getUserEvent) {
        List<BooleanExpression> conditions = new ArrayList<>();

        if (getUserEvent.getText() != null) {
            String textToSearch = getUserEvent.getText();
            conditions.add(qEvent.title.containsIgnoreCase(textToSearch)
                    .or(qEvent.annotation.containsIgnoreCase(textToSearch))
                    .or(qEvent.description.containsIgnoreCase(textToSearch)));
        }

        if (getUserEvent.getCategories() != null) {
            conditions.add(qEvent.category.id.in(getUserEvent.getCategories()));
        }

        if (getUserEvent.getPaid() != null) {
            conditions.add(qEvent.paid.eq(getUserEvent.getPaid()));
        }

        LocalDateTime rangeStart = getUserEvent.getRangeStart() != null ? getUserEvent.getRangeStart() : LocalDateTime.now();
        conditions.add(qEvent.eventDate.goe(rangeStart));

        if (getUserEvent.getRangeEnd() != null) {
            conditions.add(
                    qEvent.eventDate.loe(getUserEvent.getRangeEnd())
            );
        }

        conditions.add(qEvent.state.eq(EventStatus.PUBLISHED));

        return conditions
                .stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    public static BooleanExpression makeAdminEventQueryFilters(GetAdminEvent getAdminEvent) {
        List<BooleanExpression> conditions = new ArrayList<>();

        if (getAdminEvent.getCategories() != null) {
            conditions.add(qEvent.category.id.in(getAdminEvent.getCategories()));
        }

        if (getAdminEvent.getStates() != null) {
            conditions.add(qEvent.state.in(getAdminEvent.getStates()));
        }

        if (getAdminEvent.getUsers() != null) {
            conditions.add(qEvent.initiator.id.in(getAdminEvent.getUsers()));
        }
        LocalDateTime rangeStart = getAdminEvent.getRangeStart() != null ? getAdminEvent.getRangeStart() : LocalDateTime.now();
        conditions.add(qEvent.eventDate.goe(rangeStart));

        if (getAdminEvent.getRangeEnd() != null) {
            conditions.add(
                    qEvent.eventDate.loe(getAdminEvent.getRangeEnd())
            );
        }
        return conditions
                .stream()
                .reduce(BooleanExpression::and)
                .get();
    }

    private Map<Integer, Integer> getConfirmedRequests(List<Event> events) {
        Map<Integer, Integer> confirmedRequests = new HashMap<>();

        List<Integer> eventsId = events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        if (!eventsId.isEmpty()) {
            requestRepository.getConfirmedRequests(eventsId)
                    .forEach(conf -> confirmedRequests.put(conf.getEventId(), Math.toIntExact(conf.getConfirmedRequests())));
        }

        return confirmedRequests;
    }

    private Map<Integer, Long> getViews(List<Event> events) {
        Map<Integer, Long> views = new HashMap<>();

        LocalDateTime start = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = events
                .stream()
                .map(Event::getId)
                .map(id -> ("/events/" + id))
                .collect(Collectors.toList());

        List<ViewStatsDto> stats = statsService.getStats(start, end, uris, null);

        if (!stats.isEmpty()) {
            stats.forEach(stat -> {
                Integer eventId = Integer.parseInt(stat.getUri()
                        .split("/", 0)[2]);
                views.put(eventId, stat.getHits());
            });
        }

        return views;
    }

    private EventFullDto setViewsAndConfirmedRequestsToEventFullDto(Event event) {
        LocalDateTime start = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/events/" + event.getId());

        List<ViewStatsDto> stats = statsService.getStats(start, end, uris, true);

        EventFullDto eventFull = EventMapper.toEventFullDto(event);
        if (!stats.isEmpty()) {
            eventFull.setViews(Math.toIntExact(stats.get(0).getHits()));
        } else {
            eventFull.setViews(0);
        }

        int request = requestRepository.findAllByEventIdAndStatusEquals(event.getId(),
                RequestStatus.CONFIRMED).size();

        eventFull.setConfirmedRequests(request);

        return eventFull;
    }

}
