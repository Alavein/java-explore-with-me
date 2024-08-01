package ru.yandex.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.events.status.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class GetAdminEvent {

    private List<Integer> users;

    private List<EventStatus> states;

    private List<Integer> categories;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

    public List<Integer> getUsers() {
        return users;
    }

    public void setUsers(List<Integer> users) {
        this.users = users;
    }

    public List<EventStatus> getStates() {
        return states;
    }

    public void setStates(List<EventStatus> states) {
        this.states = states;
    }

    public List<Integer> getCategories() {
        return categories;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public LocalDateTime getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(LocalDateTime rangeStart) {
        this.rangeStart = rangeStart;
    }

    public LocalDateTime getRangeEnd() {
        return rangeEnd;
    }

    public void setRangeEnd(LocalDateTime rangeEnd) {
        this.rangeEnd = rangeEnd;
    }
}