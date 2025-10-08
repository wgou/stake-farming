package io.renren.modules.core.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.core.entity.MessageEntity;

@Mapper
public interface MessageMapper  extends BaseMapper<MessageEntity> {

}
