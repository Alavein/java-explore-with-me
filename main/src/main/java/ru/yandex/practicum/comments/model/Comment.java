package ru.yandex.practicum.comments.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.events.model.Event;
import ru.yandex.practicum.users.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(nullable = false)
    @Size(min = 10, max = 2000)
    String text;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;
    @Column(nullable = false)
    LocalDateTime createdOn;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
