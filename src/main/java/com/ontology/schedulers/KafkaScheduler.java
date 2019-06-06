package com.ontology.schedulers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ontology.utils.ElasticsearchUtil;
import com.ontology.utils.SDKUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
@EnableScheduling
public class KafkaScheduler extends BaseScheduler {

    private String indexName = "sync_index";
    private String heightType = "blockHeight";
    @Autowired
    private SDKUtil sdk;
    @Autowired
    private KafkaTemplate kafkaTemplate;


    /**
     * 同步链上信息
     */
//    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
//    public void synchronizeData() {
//        log.info("synchronizeData schedule : {}", Thread.currentThread().getName());
//        int i = 0;
//        try {
//            int blockHeight = sdk.getBlockHeight();
//            int currentHeight;
//
//            Integer height = ElasticsearchUtil.searchMaxValue(indexName, heightType, "height");
//            if (height == -2147483648) {
//                // height不存在
//                currentHeight = 0;
//                Map<String, Object> map = new HashMap<>();
//                map.put("height", 0);
//                ElasticsearchUtil.addData(map, indexName, heightType, "startHeight");
//            } else {
//                currentHeight = height;
//            }
//
//            log.info("最大块高：{}", blockHeight);
//            log.info("开始块高：{}", currentHeight);
//
//            for (i = currentHeight; i <= blockHeight; i++) {
//                log.info("当前块高:{}", i);
//                Object events = sdk.getSmartCodeEvent(i);
//                if (!StringUtils.isEmpty(events)) {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("height", i);
//                    data.put("events", events);
//                    ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("topic-block-event", JSON.toJSONString(data));
//
//                }
//            }
//            Map<String, Object> map = new HashMap<>();
//            map.put("height", i);
//            ElasticsearchUtil.addData(map, indexName, heightType, "startHeight");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("{}", i);
//            if (i != 0) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("height", i);
//                ElasticsearchUtil.addData(map, indexName, heightType, "startHeight");
//            }
//        }
//    }
    @Scheduled(initialDelay = 5000, fixedDelay = 6000)
    public void synchronizeData() {
        log.info("synchronizeData schedule : {}", Thread.currentThread().getName());
        int i = 0;
        try {
            int blockHeight = sdk.getBlockHeight();
            int currentHeight;

            Integer height = ElasticsearchUtil.searchMaxValue(indexName, heightType, "height");
            if (height == -2147483648) {
                // height不存在
                currentHeight = 0;
                Map<String, Object> map = new HashMap<>();
                map.put("height", 0);
                ElasticsearchUtil.addData(map, indexName, heightType, "startHeight");
            } else {
                currentHeight = height;
            }

            log.info("最大块高：{}", blockHeight);
            log.info("开始块高：{}", currentHeight);

            for (i = currentHeight; i <= blockHeight; i++) {
                log.info("当前块高:{}", i);
                Object events = sdk.getSmartCodeEvent(i);
                if (!StringUtils.isEmpty(events)) {
                    JSONArray eventList = (JSONArray) events;
                    for (int j = 0; j < eventList.size(); j++) {
                        JSONObject event = eventList.getJSONObject(j);
                        Map<String, Object> data = new HashMap<>();
                        data.put("height", i);
                        data.put("events", event.toJSONString());
                        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("topic-test-event", JSON.toJSONString(data));
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("height", i);
            ElasticsearchUtil.addData(map, indexName, heightType, "startHeight");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("{}", i);
            if (i != 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("height", i);
                ElasticsearchUtil.addData(map, indexName, heightType, "startHeight");
            }
        }
    }

//    @Scheduled(initialDelay = 5000, fixedDelay = 3600)
//    public void synchronizePreviousData1() {
//        Map<String, Object> previousHeight = ElasticsearchUtil.searchDataById(indexName, heightType, "previousHeight", null);
//        log.info("previousHeight:{}", previousHeight);
//        if (previousHeight != null) {
//            return;
//        }
//        for (int i = 0; i <= 1790434; i++) {
//            log.info("previousHeight当前块高:{}", i);
//            Object events = null;
//            try {
//                events = sdk.getSmartCodeEvent(i);
//            } catch (Exception e) {
//                e.printStackTrace();
//                i--;
//            }
//            if (!StringUtils.isEmpty(events)) {
//                Map<String, Object> data = new HashMap<>();
//                data.put("height", i);
//                data.put("events", events);
//                ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("topic-block-event", JSON.toJSONString(data));
//
//            }
//        }
//        Map<String, Object> map = new HashMap<>();
//        map.put("height", 1790434);
//        ElasticsearchUtil.addData(map, indexName, heightType, "previousHeight");
//    }

//    @Scheduled(initialDelay = 5000, fixedDelay = 3600000)
    public void synchronizePreviousData() {
        Map<String, Object> previousHeight = ElasticsearchUtil.searchDataById(indexName, heightType, "previousHeight", null);
        log.info("previousHeight:{}", previousHeight);
        if (previousHeight != null) {
            return;
        }
        for (int i = 0; i <= 1790434; i++) {
            log.info("previousHeight当前块高:{}", i);
            Object events = null;
            try {
                events = sdk.getSmartCodeEvent(i);
            } catch (Exception e) {
                e.printStackTrace();
                i--;
            }
            if (!StringUtils.isEmpty(events)) {
                JSONArray eventList = (JSONArray) events;
                for (int j = 0; j < eventList.size(); j++) {
                    JSONObject event = eventList.getJSONObject(j);
                    Map<String, Object> data = new HashMap<>();
                    data.put("height", i);
                    data.put("events", event.toJSONString());
                    ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("topic-block-event", JSON.toJSONString(data));

                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("height", 1790434);
        ElasticsearchUtil.addData(map, indexName, heightType, "previousHeight");
    }

}
