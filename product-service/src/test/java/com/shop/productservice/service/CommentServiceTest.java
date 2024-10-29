package com.shop.productservice.service;

import com.shop.productservice.model.Comment;
import com.shop.productservice.model.Product;
import com.shop.productservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment comment1;
    private Comment comment2;
    private Product product;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        product = Product.builder()
                .id(1L)
                .name("Product 1")
                .build();

        comment1 = Comment.builder()
                .id(1L)
                .authorNickname("user1")
                .dateOfPublishing(LocalDate.now())
                .comment("Great product")
                .product(product)
                .build();

        comment2 = Comment.builder()
                .id(2L)
                .authorNickname("user2")
                .dateOfPublishing(LocalDate.now())
                .comment("Not bad")
                .product(product)
                .build();
    }

    @Test
    void findAllByProductId() {
        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.findAllByProductId(1L)).thenReturn(comments);

        List<Comment> result = commentService.findAllByProductId(1L);

        assertEquals(2, result.size());
        assertEquals(comment1, result.get(0));
        verify(commentRepository, times(1)).findAllByProductId(1L);
    }

    @Test
    void addComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);

        Comment result = commentService.addComment(comment1);

        assertEquals(comment1, result);
        verify(commentRepository, times(1)).save(comment1);
    }

    @Test
    void updateComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);

        Comment result = commentService.updateComment(comment1);

        assertEquals(comment1, result);
        verify(commentRepository, times(1)).save(comment1);
    }

    @Test
    void deleteCommentById() {
        Long commentId = 1L;

        doNothing().when(commentRepository).deleteById(commentId);

        commentService.deleteCommentById(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void findAllByAuthorNickname() {
        List<Comment> comments = Collections.singletonList(comment1);

        when(commentRepository.findAllByAuthorNickname("user1")).thenReturn(comments);

        List<Comment> result = commentService.findAllByAuthorNickname("user1");

        assertEquals(1, result.size());
        assertEquals(comment1, result.get(0));
        verify(commentRepository, times(1)).findAllByAuthorNickname("user1");
    }
}
