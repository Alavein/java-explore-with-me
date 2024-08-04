package ru.yandex.practicum.comments.service;

import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto createComment(Integer userId, Integer eventId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentsAdmin(Integer from, Integer size);

    void deleteCommentsAdmin(Integer commentId);

    List<CommentDto> getComments(Integer userId, Integer eventId, Integer from, Integer size);

    CommentDto updateComments(Integer userId, Integer commentId, NewCommentDto newCommentDto);

    List<CommentDto> getCommentsPublic(Integer eventId, Integer from, Integer size);

    void deleteComment(Integer userId, Integer commentId);
}
