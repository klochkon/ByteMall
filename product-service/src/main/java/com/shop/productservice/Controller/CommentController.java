package com.shop.productservice.Controller;

import com.shop.productservice.Model.Comment;
import com.shop.productservice.Service.CommentService;
import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @GetMapping("find/product/{productId}")
    public List<Comment> findAllByProductId(@PathVariable Long productId) {
        return service.findAllByProductId(productId);
    }

    @PostMapping("add")
    public Comment addComment(@RequestBody Comment comment) {
        return service.addComment(comment);
    }

    @PutMapping("update")
    public Comment updateComment(@RequestBody Comment comment) {
        return service.updateComment(comment);
    }

    @DeleteMapping("delete/{id}")
    public void deleteCommentById(@PathVariable Long id) {
        service.deleteCommentById(id);
    }

    @GetMapping("find/author/{authorNickname}")
    public List<Comment> findAllByAuthorNickname(@PathVariable String authorNickname) {
        return service.findAllByAuthorNickname(authorNickname);
    }


}
