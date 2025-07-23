package com.shemuel.timeline.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shemuel.timeline.entity.Event;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 时间轴事件表 Mapper接口
 */
@Mapper
public interface EventMapper extends BaseMapper<Event> {

    /**
     * 使用 XML 查询分页数据，确保自定义 TypeHandler 正常工作
     * @param page 分页参数
     * @param event 查询条件
     * @return 分页结果
     */
    IPage<Event> selectPage(Page<Event> page, @Param("event") Event event);


} 