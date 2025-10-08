package io.renren.modules.core.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.core.entity.TelegramBotEntity;

@Mapper
public interface TelegramBotMapper extends BaseMapper<TelegramBotEntity> {
    
} 