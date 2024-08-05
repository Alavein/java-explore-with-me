package ru.yandex.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comments.dto.CommentDto;
import ru.yandex.practicum.comments.dto.NewCommentDto;
import ru.yandex.practicum.comments.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentsController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Integer userId,
                                    @RequestParam Integer eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getComments(
            @PathVariable Integer userId,
            @RequestParam(required = false) Integer eventId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getComments(userId, eventId, from, size);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComments(@PathVariable Integer userId,
                                     @PathVariable Integer commentId,
                                     @Valid @RequestBody NewCommentDto newCommentDto) {
        return commentService.updateComments(userId, commentId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer userId,
                              @PathVariable Integer commentId) {
        commentService.deleteComment(userId, commentId);
    }
}
