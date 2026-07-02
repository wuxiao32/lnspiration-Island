package com.inspiration.island.controller;

import com.inspiration.island.common.Result;
import com.inspiration.island.entity.Inspiration;
import com.inspiration.island.service.InspirationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inspiration")
public class InspirationController {

    @Autowired
    private InspirationService inspirationService;

    /** 灵感广场（公开） */
    @GetMapping("/list")
    public Result<?> getInspirationList() {
        List<Inspiration> list = inspirationService.listAll();
        return Result.success(list);
    }

    /** 我的灵感（需登录） */
    @GetMapping("/myList")
    public Result<?> myList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Inspiration> list = inspirationService.listByUserId(userId);
        return Result.success(list);
    }

    /** 发布灵感（需登录） */
    @PostMapping("/publish")
    public Result<?> publish(@RequestBody Inspiration inspiration, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        inspiration.setUserId(userId);
        inspirationService.publish(inspiration);
        return Result.success("发布成功");
    }

    /** 修改灵感（需登录，仅作者） */
    @PutMapping("/update")
    public Result<?> update(@RequestBody Inspiration inspiration, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            inspirationService.update(inspiration, userId);
            return Result.success("修改成功");
        } catch (RuntimeException e) {
            return Result.error(403, e.getMessage());
        }
    }

    /** 删除灵感（需登录，仅作者，软删除） */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            inspirationService.delete(id, userId);
            return Result.success("删除成功");
        } catch (RuntimeException e) {
            return Result.error(403, e.getMessage());
        }
    }
}
