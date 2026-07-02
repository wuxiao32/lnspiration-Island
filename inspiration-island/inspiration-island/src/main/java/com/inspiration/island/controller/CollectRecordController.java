package com.inspiration.island.controller;

import com.inspiration.island.common.Result;
import com.inspiration.island.service.CollectRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collect")
public class CollectRecordController {

    private final CollectRecordService collectRecordService;

    public CollectRecordController(CollectRecordService collectRecordService) {
        this.collectRecordService = collectRecordService;
    }

    /**
     * 收藏/取消收藏（需登录）
     */
    @PostMapping("/toggle/{inspirationId}")
    public Result<?> toggle(@PathVariable Long inspirationId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String msg = collectRecordService.toggle(inspirationId, userId);
        return Result.success(msg);
    }

    /**
     * 收藏数量（公开）
     */
    @GetMapping("/count/{inspirationId}")
    public Result<?> count(@PathVariable Long inspirationId) {
        int count = collectRecordService.countByInspirationId(inspirationId);
        return Result.success(count);
    }
}
