package ru.yandex.practicum.requests.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmedRequests {

    private Integer eventId;
    private Long confirmedRequests;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Long getConfirmedRequests() {
        return confirmedRequests;
    }

    public void setConfirmedRequests(Long confirmedRequests) {
        this.confirmedRequests = confirmedRequests;
    }
}
