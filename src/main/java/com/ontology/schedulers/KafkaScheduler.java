package com.ontology.schedulers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ontology.utils.ConfigParam;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@EnableScheduling
public class KafkaScheduler extends BaseScheduler {

    @Autowired
    private SDKUtil sdk;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private ConfigParam configParam;

    /**
     * 同步链上信息
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 6000)
    public void synchronizeData() throws IOException {
        log.info("synchronizeData schedule : {}", Thread.currentThread().getName());
        int i = 0;
        try {
            int blockHeight = sdk.getBlockHeight();
            int currentHeight;
            Integer height = null;
            try {
                height = ElasticsearchUtil.searchMaxValue(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "height");
            } catch (Exception e) {
                // 代码走到此次说明，需要从最新块开始同步
                Map<String, Object> startHeight = new HashMap<>();
                startHeight.put("height", blockHeight);
                ElasticsearchUtil.addData(startHeight, Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "startHeight");

                // 记录同步之前块的块高终止位置
                Map<String, Object> preEndHeight = new HashMap<>();
                preEndHeight.put("height", blockHeight);
                ElasticsearchUtil.addData(preEndHeight, Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "preEndHeight");
            }
            if (height == null) {
                return;
            }

            if (height == -2147483648) {
                // 索引之前不存在，新建索引，height需要从0开始
                currentHeight = 0;
                Map<String, Object> map = new HashMap<>();
                map.put("height", 0);
                ElasticsearchUtil.addData(map, Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "startHeight");
            } else {
                currentHeight = height;
            }

            log.info("最大块高：{}", blockHeight);
            log.info("开始块高：{}", currentHeight);

            for (i = currentHeight; i <= blockHeight; i++) {
                log.info("当前块高:{}", i);
                Object events = sdk.getSmartCodeEvent(i);
                log.info("events:{}",events);
                if (!StringUtils.isEmpty(events)) {
                    JSONArray eventList = (JSONArray) events;
                    for (int j = 0; j < eventList.size(); j++) {
                        JSONObject event = eventList.getJSONObject(j);
                        Map<String, Object> data = new HashMap<>();
                        data.put("height", i);
                        data.put("events", event.toJSONString());
                        kafkaTemplate.send(Constant.KAFKA_TOPIC, JSON.toJSONString(data));
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("height", i);
            ElasticsearchUtil.addData(map, Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "startHeight");
        } catch (Exception e) {
            log.error("catch exception:",e);
            log.info("{}", i);
            if (i != 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("height", i);

                ElasticsearchUtil.addData(map, Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "startHeight");
            }
        }
    }


//    @Scheduled(initialDelay = 10000, fixedDelay = 3600000)
    public void synchronizePreviousData() throws IOException {
        if (!configParam.SYNC_PREBLOCK_SWITCH) {
            return;
        }
        // 查询是否有需要同步的之前块高
        Map<String, Object> preEndHeight = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "preEndHeight", null);
        log.info("preEndHeight:{}", preEndHeight);
        if (preEndHeight == null) {
            log.info("没有需要从0开始同步的之前块高");
            return;
        }

        int endHeight = (int) preEndHeight.get("height");

        int height = 0;
        Map<String, Object> preStartHeight = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "preStartHeight", null);
        log.info("preStartHeight:{}", preStartHeight);
        if (preStartHeight != null) {
            height = (int) preStartHeight.get("height");
        }

        int i;
        for (i = height; i < endHeight; i++) {
            log.info("previousHeight当前块高:{}", i);
            Object events = null;
            try {
                events = sdk.getSmartCodeEvent(i);
            } catch (Exception e) {
                log.error("catch exception:",e);
                i--;
            }
            if (!StringUtils.isEmpty(events)) {
                JSONArray eventList = (JSONArray) events;
                for (int j = 0; j < eventList.size(); j++) {
                    JSONObject event = eventList.getJSONObject(j);
                    Map<String, Object> data = new HashMap<>();
                    data.put("height", i);
                    data.put("events", event.toJSONString());
                    kafkaTemplate.send(Constant.KAFKA_TOPIC, JSON.toJSONString(data));
                }
            }
        }
    }

}
