package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    private CommentMapper commentMapper;
    private Comment comment;
    private CommentDto commentDto;
    private User user;

    @BeforeEach
    public void setUp() {
        commentMapper = new CommentMapper();
        user = new User();
        user.setName("Test User");

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test Comment");
        commentDto.setAuthorName("Test User");
        commentDto.setCreated(LocalDateTime.now());
    }

    @Test
    public void testToCommentDtoWhenCommentProvidedThenCommentDtoReturnedWithCorrectState() {
        CommentDto result = commentMapper.toCommentDto(comment);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(comment.getId());
        assertThat(result.getText()).isEqualTo(comment.getText());
        assertThat(result.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(result.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    public void testToCommentWhenCommentDtoProvidedThenCommentReturnedWithCorrectState() {
        Comment result = commentMapper.toComment(commentDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(commentDto.getId());
        assertThat(result.getText()).isEqualTo(commentDto.getText());
        assertThat(result.getCreated()).isEqualTo(commentDto.getCreated());
    }
}