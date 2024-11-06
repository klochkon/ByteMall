package com.shop.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.productservice.model.Comment;
import com.shop.productservice.model.Product;
import com.shop.productservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    private Product product;

    private Comment comment;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .cost(new BigDecimal("100.0"))
                .description("Test Description")
                .build();

        comment = Comment.builder()
                .id(1L)
                .comment("comment")
                .authorNickname("author")
                .product(product)
                .build();
    }

    @Test
    void testFindAllByProductId() throws Exception {
//        given
        List<Comment> comments = List.of(comment);
        when(commentService.findAllByProductId(anyLong())).thenReturn(comments);

//        when
        mockMvc.perform(get("/api/v1/comment/find/product/10"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(comments)));

//        then
        verify(commentService, times(1)).findAllByProductId(10L);
    }

    @Test
    void testAddComment() throws Exception {
//        given
        when(commentService.addComment(any(Comment.class))).thenReturn(comment);

//        when
        mockMvc.perform(post("/api/v1/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(comment)));

//        then
        verify(commentService, times(1)).addComment(any(Comment.class));
    }

    @Test
    void testUpdateComment() throws Exception {
//        given
        when(commentService.updateComment(any(Comment.class))).thenReturn(comment);

//        when
        mockMvc.perform(put("/api/v1/comment/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(comment)));

//        then
        verify(commentService, times(1)).updateComment(any(Comment.class));
    }

    @Test
    void testDeleteCommentById() throws Exception {
//        given
        doNothing().when(commentService).deleteCommentById(anyLong());

//        when
        mockMvc.perform(delete("/api/v1/comment/delete/1"))
                .andExpect(status().isOk());

//        then
        verify(commentService, times(1)).deleteCommentById(1L);
    }

    @Test
    void testFindAllByAuthorNickname() throws Exception {
//        given
        List<Comment> comments = List.of(comment);
        when(commentService.findAllByAuthorNickname(anyString())).thenReturn(comments);

//        when
        mockMvc.perform(get("/api/v1/comment/find/author/Author1"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(comments)));

//        then
        verify(commentService, times(1)).findAllByAuthorNickname("Author1");
    }
}
