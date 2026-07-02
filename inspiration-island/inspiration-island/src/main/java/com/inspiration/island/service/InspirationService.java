package com.inspiration.island.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.inspiration.island.entity.Inspiration;

import java.util.List;

public interface InspirationService extends IService<Inspiration> {

    /** 获取所有灵感列表（首页，带缓存） */
    List<Inspiration> listAll();

    /** 获取当前用户发布的灵感 */
    List<Inspiration> listByUserId(Long userId);

    /** 发布灵感 */
    void publish(Inspiration inspiration);

    /** 修改灵感（仅作者本人） */
    void update(Inspiration inspiration, Long currentUserId);

    /** 删除灵感（仅作者本人，软删除） */
    void delete(Long inspirationId, Long currentUserId);
}
