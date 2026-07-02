package com.inspiration.island.controller;

import com.inspiration.island.common.Result;
import com.inspiration.island.service.LikeRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
public class LikeRecordController {

    private final LikeRecordService likeRecordService;

    public LikeRecordController(LikeRecordService likeRecordService) {
        this.likeRecordService = likeRecordService;
    }

    /**
     * 点赞/取消点赞（需登录）
     */
    @PostMapping("/toggle/{inspirationId}")
    public Result<?> toggle(@PathVariable Long inspirationId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String msg = likeRecordService.toggle(inspirationId, userId);
        return Result.success(msg);
    }

    /**
     * 点赞数量（公开）
     */
    @GetMapping("/count/{inspirationId}")
    public Result<?> count(@PathVariable Long inspirationId) {
        int count = likeRecordService.countByInspirationId(inspirationId);
        return Result.success(count);
    }
}
