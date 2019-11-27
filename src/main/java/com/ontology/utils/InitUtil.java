package com.ontology.utils;

import com.ontology.mapper.BlockHeightMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by lijie on 2019/9/3.
 */
@Slf4j
@Component
public class InitUtil implements CommandLineRunner {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private BlockHeightMapper blockHeightMapper;

    //mysql方案
    @Override
    public void run(String... args) throws Exception {
        if (configParam.CLEAR_SYNC_SWITCH) {
            blockHeightMapper.deleteByPrimaryKey(1);
        }
    }

    //ES方案
//    @Override
//    public void run(String... args) throws Exception {
//        if (configParam.CLEAR_SYNC_SWITCH) {
//            if (ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_SYNC)) {
//                // 清除之前同步的块高记录，从最新块开始同步
//                ElasticsearchUtil.deleteIndex(Constant.ES_INDEX_SYNC);
//            }
//        } else {
//            if (!ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_SYNC)) {
//                ElasticsearchUtil.createIndex(Constant.ES_INDEX_SYNC);
//            }
//        }
//    }
}
