package com.ontology.mapper;

import com.ontology.entity.BlockHeight;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;


@Component
public interface BlockHeightMapper extends Mapper<BlockHeight> {
    Integer selectMaxHeight();
}
