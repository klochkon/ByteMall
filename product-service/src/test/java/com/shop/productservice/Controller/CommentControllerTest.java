package com.shop.productservice.Controller;

import com.shop.productservice.Model.Comment;
import com.shop.productservice.Service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService service;

    @InjectMocks
    private CommentController controller;

    private MockMvc mockMvc;
    private Comment comment;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        comment = Comment.builder()
                .id(1L)
                .authorNickname("JohnDoe")
                .dateOfPublishing(LocalDate.now())
                .comment("Great product!")
                .build();
    }

    @Test
    void findAllByProductId() throws Exception {
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);

        when(service.findAllByProductId(anyLong())).thenReturn(commentList);

        mockMvc.perform(get("/api/v1/comment/find/product/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].authorNickname").value("JohnDoe"));
    }

    @Test
    void addComment() throws Exception {
        when(service.addComment(any(Comment.class))).thenReturn(comment);

        mockMvc.perform(post("/api/v1/comment/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorNickname\":\"JohnDoe\", \"comment\":\"Great product!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authorNickname").value("JohnDoe"))
                .andExpect(jsonPath("$.comment").value("Great product!"));
    }

    @Test
    void updateComment() throws Exception {
        when(service.updateComment(any(Comment.class))).thenReturn(comment);

        mockMvc.perform(put("/api/v1/comment/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1, \"authorNickname\":\"JohnDoe\", \"comment\":\"Updated comment\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.authorNickname").value("JohnDoe"))
                .andExpect(jsonPath("$.comment").value("Updated comment"));
    }

    @Test
    void deleteCommentById() throws Exception {
        mockMvc.perform(delete("/api/v1/comment/delete/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void findAllByAuthorNickname() throws Exception {
        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);

        when(service.findAllByAuthorNickname(anyString())).thenReturn(commentList);

        mockMvc.perform(get("/api/v1/comment/find/author/{authorNickname}", "JohnDoe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorNickname").value("JohnDoe"))
                .andExpect(jsonPath("$[0].comment").value("Great product!"));
    }
}
