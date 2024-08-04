package ru.yandex.practicum.requests.dto;

import lombok.*;
import ru.yandex.practicum.requests.model.status.RequestStatusAction;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusUpdateRequest {

    @NotEmpty
    private List<Integer> requestIds;
    @NotNull
    private RequestStatusAction status;

    public List<Integer> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<Integer> requestIds) {
        this.requestIds = requestIds;
    }

    public RequestStatusAction getStatus() {
        return status;
    }

    public void setStatus(RequestStatusAction status) {
        this.status = status;
    }
}