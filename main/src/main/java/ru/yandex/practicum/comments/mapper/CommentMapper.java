package ru.yandex.practicum.comments.mapper;

import ru.yandex.practicum.comments.model.Comment;
import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.dto.NewCommentDto;
import ru.yandex.practicum.users.dto.UserShortDto;

public class CommentMapper {

    public static Comment toComment(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .eventId(comment.getEvent().getId())
                .user(UserShortDto.builder()
                        .id(comment.getUser().getId())
                        .name(comment.getUser().getName())
                        .build())
                .build();
    }
}
