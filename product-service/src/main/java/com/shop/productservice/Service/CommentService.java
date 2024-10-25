package com.shop.productservice.Service;

import com.shop.productservice.Model.Comment;
import com.shop.productservice.Repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository repository;

    @Cacheable(value = "allProductComment")
    public List<Comment> findAllByProductId(Long productId) {
        log.info("Finding all comments for product id: {}", productId);
        List<Comment> comments = repository.findAllByProductId(productId);
        log.info("Found {} comments for product id: {}", comments.size(), productId);
        return comments;
    }

    @CachePut(value = "allProductComment", key = "#comment.id")
    public Comment addComment(Comment comment) {
        Comment savedComment = repository.save(comment);
        log.info("Comment added successfully: {}", savedComment);
        return savedComment;
    }

    @CachePut(value = "allProductComment", key = "#comment.id")
    public Comment updateComment(Comment comment) {
        Comment updatedComment = repository.save(comment);
        log.info("Comment updated successfully: {}", updatedComment);
        return updatedComment;
    }

    @CacheEvict(value = "allProductComment", key = "#id")
    public void deleteCommentById(Long id) {
        log.info("Deleting comment with id: {}", id);
        repository.deleteById(id);
        log.info("Comment with id {} deleted successfully", id);
    }

    @Cacheable(value = "allProductComment")
    public List<Comment> findAllByAuthorNickname(String authorNickname) {
        log.info("Finding all comments by author nickname: {}", authorNickname);
        List<Comment> comments = repository.findAllByAuthorNickname(authorNickname);
        log.info("Found {} comments by author nickname: {}", comments.size(), authorNickname);
        return comments;
    }
}
