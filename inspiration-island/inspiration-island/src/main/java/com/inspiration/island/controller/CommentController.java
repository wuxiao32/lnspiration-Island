package com.inspiration.island.controller;

import com.inspiration.island.common.Result;
import com.inspiration.island.entity.Comment;
import com.inspiration.island.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 发布评论（需登录） */
    @PostMapping("/publish")
    public Result<?> publish(@RequestBody Comment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        comment.setUserId(userId);
        commentService.publish(comment);
        return Result.success("评论成功");
    }

    /** 查看某个灵感的所有评论（公开） */
    @GetMapping("/list/{inspirationId}")
    public Result<?> list(@PathVariable Long inspirationId) {
        List<Comment> commentList = commentService.listByInspirationId(inspirationId);
        return Result.success(commentList);
    }

    /** 删除评论（需登录，仅作者） */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            commentService.delete(id, userId);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(403, e.getMessage());
        }
    }
}
