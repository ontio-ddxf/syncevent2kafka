package com.ontology;

import com.ontology.model.EsPage;
import com.ontology.utils.Constant;
import com.ontology.utils.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EsTest {

    @Test
    public void testSearchES() {
//        int i = ElasticsearchUtil.searchMaxValue("sync_index", "blockHeight", "height");
//        log.info("{}", i);
//        if (i == -2147483648) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("height", 0);
//            String s = ElasticsearchUtil.addData(map, "sync_index", "blockHeight", "1");
//            log.info("{}", s);
//        }
    }

    @Test
    public void setStartHeight() throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("height", 397530);//397530
        map.put("orderId", "yes");

        String s = ElasticsearchUtil.addData(map, Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "123");
        log.info("{}", s);
    }

    @Test
    public void deleteDoc() throws IOException {
        ElasticsearchUtil.deleteDataById(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "pppppp");
    }

    @Test
    public void createIndex() throws IOException {
        ElasticsearchUtil.createIndex(Constant.ES_INDEX_SYNC);
    }

    @Test
    public void deleteIndex() throws IOException {
        ElasticsearchUtil.deleteIndex(Constant.ES_INDEX_SYNC);
    }

    @Test
    public void exist() throws IOException {
        ElasticsearchUtil.isIndexExist(Constant.ES_INDEX_SYNC);
    }

    @Test
    public void searchById() throws IOException {
        Map<String, Object> map = ElasticsearchUtil.searchDataById(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "123", null);
        log.info("map:{}",map);
    }

    @Test
    public void search() throws IOException {
        BoolQueryBuilder bool = new BoolQueryBuilder();
//        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("height", 1135965);
//        bool.must(matchQueryBuilder);
        List<Map<String, Object>> maps = ElasticsearchUtil.searchListData(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, bool, null, null, "height", null);
        log.info("map:{}",maps);
    }

    @Test
    public void searchPage() throws IOException {
        BoolQueryBuilder bool = new BoolQueryBuilder();
//        QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("height", 1135965);
//        bool.must(matchQueryBuilder);
        EsPage height = ElasticsearchUtil.searchDataPage(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, 1, 1, bool, null, "height", null);
        log.info("map:{}",height);
    }

    @Test
    public void searchMax() throws IOException {
        int height = ElasticsearchUtil.searchMaxValue(Constant.ES_INDEX_SYNC, Constant.ES_TYPE_SYNC, "height");
        log.info("map:{}",height);
    }
}
