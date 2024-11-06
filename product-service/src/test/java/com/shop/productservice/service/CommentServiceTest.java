package com.shop.productservice.service;

import com.shop.productservice.model.Comment;
import com.shop.productservice.model.Product;
import com.shop.productservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
//        given
        List<Comment> comments = Arrays.asList(comment1, comment2);
        when(commentRepository.findAllByProductId(1L)).thenReturn(comments);

//        when
        List<Comment> result = commentService.findAllByProductId(1L);

//        then
        assertEquals(2, result.size());
        assertEquals(comment1, result.get(0));
        verify(commentRepository, times(1)).findAllByProductId(1L);
    }

    @Test
    void addComment() {
//        given
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);

//        when
        Comment result = commentService.addComment(comment1);

//        then
        assertEquals(comment1, result);
        verify(commentRepository, times(1)).save(comment1);
    }

    @Test
    void updateComment() {
//        given
        when(commentRepository.save(any(Comment.class))).thenReturn(comment1);

//        when
        Comment result = commentService.updateComment(comment1);

//        then
        assertEquals(comment1, result);
        verify(commentRepository, times(1)).save(comment1);
    }

    @Test
    void deleteCommentById() {
//        given
        Long commentId = 1L;
        doNothing().when(commentRepository).deleteById(commentId);

//        when
        commentService.deleteCommentById(commentId);

//        then
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void findAllByAuthorNickname() {
//        given
        List<Comment> comments = Collections.singletonList(comment1);
        when(commentRepository.findAllByAuthorNickname("user1")).thenReturn(comments);

//        when
        List<Comment> result = commentService.findAllByAuthorNickname("user1");

//        then
        assertEquals(1, result.size());
        assertEquals(comment1, result.get(0));
        verify(commentRepository, times(1)).findAllByAuthorNickname("user1");
    }
}
