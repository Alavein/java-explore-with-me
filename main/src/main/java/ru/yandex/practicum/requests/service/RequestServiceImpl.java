package ru.yandex.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.events.service.EventsService;
import ru.yandex.practicum.requests.mapper.RequestMapper;
import ru.yandex.practicum.events.model.Event;
import ru.yandex.practicum.requests.model.Request;
import ru.yandex.practicum.requests.dto.StatusUpdateRequest;
import ru.yandex.practicum.requests.dto.StatusUpdateResult;
import ru.yandex.practicum.requests.dto.ParticipationRequestDto;
import ru.yandex.practicum.events.status.EventStatus;
import ru.yandex.practicum.requests.model.status.RequestStatus;
import ru.yandex.practicum.requests.model.status.RequestStatusAction;
import ru.yandex.practicum.requests.repository.RequestRepository;
import ru.yandex.practicum.exceptions.ConflictException;
import ru.yandex.practicum.exceptions.DataNotFoundException;
import ru.yandex.practicum.users.model.User;
import ru.yandex.practicum.users.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserServiceImpl userService;
    private final EventsService eventsService;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getEventRequestsByRequester(Integer userId) {

        userService.getUserById(userId);
        log.info("Получение запросов пользователя {}", userId);
        return requestRepository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createEventRequest(Integer userId, Integer eventId) {

        User user = userService.getUserById(userId);
        Event event = eventsService.getEvent(eventId);

        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Ошибка. Нельзя создать запрос на собственное мероприятие.");
        }

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException("Ошибка. Нельзя создать запрос на неопубликованное мероприя.");
        }

        if (requestRepository.findByEventAndRequesterId(eventId, userId).isPresent()) {
            throw new ConflictException("Ошибка. Запрос уже существует.");
        }

        if (event.getParticipantLimit() != 0 &&
                requestRepository.findAllByEventIdAndStatusEquals(event.getId(), RequestStatus.CONFIRMED).size()
                        >= event.getParticipantLimit()) {
            throw new ConflictException("Ошибка. Достигнут лимит участников на мероприятие.");
        }

        Request newRequest = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        Request request = requestRepository.save(newRequest);
        log.info("Создание запроса на мероприятие.");
        return RequestMapper.toParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequest(Integer userId, Integer requestId) {

        User user = userService.getUserById(userId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Запрос на мероприятие не найден."));

        request.setStatus(RequestStatus.CANCELED);
        log.info("Отмена запроса на мероприятие.");
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestOfEvent(Integer userId, Integer eventId) {

        User user = userService.getUserById(userId);
        Event event = eventsService.getEvent(eventId);

        if (!userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Ошибка. Вы не являетесь организатором мероприятия.");
        }
        log.info("Получение запроса.");
        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StatusUpdateResult updateEventRequestsByEventOwner(Integer userId, Integer eventId,
                                                              StatusUpdateRequest event) {
        List<Request> confirmed = new ArrayList<>();
        List<Request> rejected = new ArrayList<>();

        User user = userService.getUserById(userId);
        Event event1 = eventsService.getEvent(eventId);

        int conf = requestRepository
                .findAllByEventIdAndStatusEquals(event1.getId(), RequestStatus.CONFIRMED).size();

        if (!userId.equals(event1.getInitiator().getId())) {
            throw new ConflictException("Ошибка. Вы не являетесь организатором мероприятия.");
        }

        if (!event1.getRequestModeration() ||
                event1.getParticipantLimit() == 0 ||
                event.getRequestIds().isEmpty()) {
            return new StatusUpdateResult(List.of(), List.of());
        }

        List<Request> requests = requestRepository.findAllByIdIn(event.getRequestIds());

        if (requests.size() != event.getRequestIds().size()) {
            throw new DataNotFoundException("Ошибка. Запрос не найден");
        }

        if (event1.getParticipantLimit() <= conf) {
            throw new ConflictException("Ошибка. Достигнут лимит участников на мероприятие.");
        }

        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ConflictException("Ошибка. Статус всех заявок должен быть PENDING.");
        }

        if (event.getStatus().equals(RequestStatusAction.REJECTED)) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            requestRepository.saveAll(requests);
            rejected.addAll(requests);
        } else {
            int freePlaces = event1.getParticipantLimit() - conf;

            if (freePlaces > 0 && freePlaces >= requests.size()) {
                requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                requestRepository.saveAll(requests);
                confirmed.addAll(requests);
            } else {
                requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                requestRepository.saveAll(requests);
                rejected.addAll(requests);
            }
        }
        log.info("Обновление запроса.");
        return new StatusUpdateResult(
                confirmed
                        .stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()),
                rejected
                        .stream()
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList()));
    }
}