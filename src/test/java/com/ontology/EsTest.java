package com.ontology;

import com.ontology.model.EsPage;
import com.ontology.utils.ElasticsearchUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EsTest {

    @Test
    public void testSearchES() {
        int i = ElasticsearchUtil.searchMaxValue("sync_index", "blockHeight", "height");
        log.info("{}", i);
        if (i == -2147483648) {
            Map<String, Object> map = new HashMap<>();
            map.put("height", 0);
            String s = ElasticsearchUtil.addData(map, "sync_index", "blockHeight", "1");
            log.info("{}", s);
        }
    }

    @Test
    public void testSearchES2() {
        Map<String, Object> map = new HashMap<>();
        map.put("height", 1692733);
        String s = ElasticsearchUtil.addData(map, "sync_index", "blockHeight", "startHeight");
        log.info("{}", s);
    }

}
