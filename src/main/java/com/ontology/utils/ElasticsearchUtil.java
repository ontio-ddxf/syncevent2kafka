package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.ontology.model.EsPage;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: LX
 * @Description:
 * @Date: Created in 11:21 2018/11/6
 * @Modified by:
 */
@Component
@Slf4j
public class ElasticsearchUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchUtil.class);

    @Resource(name = "RHLClient")
    private RestHighLevelClient rhlClient;

    private static RestHighLevelClient client;


    /**
     * @PostContruct是spring框架的注解 spring容器初始化的时候执行该方法
     */
    @PostConstruct
    public void init() {
        client = this.rhlClient;
    }

    /**
     * 创建索引
     *
     * @param index
     * @return
     */
    public static boolean createIndex(String index) throws IOException {
        if (!isIndexExist(index)) {
            LOGGER.info("Index is not exits!");
        }

        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        LOGGER.info("执行建立成功？" + createIndexResponse.isAcknowledged());
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 删除索引
     *
     * @param index
     * @return
     */
    public static boolean deleteIndex(String index) throws IOException {
//        if (!isIndexExist(index)) {
//            LOGGER.info("Index is not exits!");
//        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse delete = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        if (delete.isAcknowledged()) {
            LOGGER.info("delete index " + index + "  successfully!");
        } else {
            LOGGER.info("Fail to delete index " + index);
        }
        return delete.isAcknowledged();
    }

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     */
    public static boolean isIndexExist(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
//        client.exists(request,RequestOptions.DEFAULT);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (exists) {
            LOGGER.info("Index [" + index + "] is exist!");
        } else {
            LOGGER.info("Index [" + index + "] is not exist!");
        }
        return exists;
    }

//    /**
//     * @Author: LX
//     * @Description: 判断inde下指定type是否存在
//     * @Date: 2018/11/6 14:46
//     * @Modified by:
//     */
//    public boolean isTypeExist(String index, String type) throws IOException {
//        return isIndexExist(index)
//                ? client.indices().(index).setTypes(type).execute().actionGet().isExists()
//                : false;
//    }

    /**
     * 数据添加
     *
     * @param obj   要增加的数据
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     * @return
     */
    public static String addData(Map obj, String index, String type, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, type, id);
        indexRequest.source(JSON.toJSONString(obj), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        LOGGER.info("addData response status:{},id:{}", indexResponse.status().getStatus(), indexResponse.getId());
        return indexResponse.getId();
    }

    /**
     * 数据添加
     *
     * @param obj   要增加的数据
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @return
     */
    public static String addData(Map obj, String index, String type) throws IOException {
        return addData(obj, index, type, UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
    }

    /**
     * 通过ID删除数据
     *
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     */
    public static void deleteDataById(String index, String type, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
        LOGGER.info("deleteDataById response status:{},id:{}", response.status().getStatus(), response.getId());
    }

    /**
     * 通过ID 更新数据
     *
     * @param obj   要增加的数据
     * @param index 索引，类似数据库
     * @param type  类型，类似表
     * @param id    数据ID
     * @return
     */
    public static void updateDataById(Map obj, String index, String type, String id) throws IOException {

        UpdateRequest updateRequest = new UpdateRequest();

        updateRequest.index(index).type(type).id(id).doc(obj);

        client.update(updateRequest);

    }

    /**
     * 通过ID获取数据
     *
     * @param index  索引，类似数据库
     * @param type   类型，类似表
     * @param id     数据ID
     * @param fields 需要显示的字段，逗号分隔（缺省为全部字段）
     * @return
     */
    public static Map<String, Object> searchDataById(String index, String type, String id, String fields) throws IOException {

        GetRequest getRequest = new GetRequest(index, type, id);

        if (!StringUtils.isEmpty(fields)) {
            String[] includes = fields.split(",");
            String[] excludes = Strings.EMPTY_ARRAY;
            FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
            getRequest.fetchSourceContext(fetchSourceContext);
        }

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return getResponse.getSource();


    }


    /**
     * 使用分词查询,并分页
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param startPage      当前页
     * @param pageSize       每页显示条数
     * @param query          查询条件
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightField 高亮字段
     * @return
     */
    public static EsPage searchDataPage(String index, String type, int startPage, int pageSize, QueryBuilder query, String fields, String sortField, String highlightField) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(query);

        if (!StringUtils.isEmpty(fields)) {
            sourceBuilder.fetchSource(fields.split(","), null);
        }

        //排序字段
        if (!StringUtils.isEmpty(sortField)) {
            sourceBuilder.sort(sortField, SortOrder.DESC);
        }

        // 高亮（xxx=111,aaa=222）
        if (!StringUtils.isEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();

            //highlightBuilder.preTags("<span style='color:red' >");//设置前缀
            //highlightBuilder.postTags("</span>");//设置后缀

            // 设置高亮字段
            highlightBuilder.field(highlightField);
            sourceBuilder.highlighter(highlightBuilder);
        }

        // 分页应用
        sourceBuilder.from(startPage * pageSize).size(pageSize);

        // 设置是否按查询匹配度排序
        sourceBuilder.explain(true);

        // 执行搜索,返回搜索响应信息
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        long totalHits = response.getHits().totalHits;
        long length = response.getHits().getHits().length;

        LOGGER.debug("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (response.status().getStatus() == 200) {
            // 解析对象
            List<Map<String, Object>> sourceList = setSearchResponse(response, highlightField);

            return new EsPage(startPage, pageSize, (int) totalHits, sourceList);
        }

        return null;

    }


    /**
     * 使用分词查询
     *
     * @param index          索引名称
     * @param type           类型名称,可传入多个type逗号分隔
     * @param query          查询条件
     * @param size           文档大小限制
     * @param fields         需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField      排序字段
     * @param highlightField 高亮字段
     * @return
     */
    public static List<Map<String, Object>> searchListData(
            String index, String type, QueryBuilder query, Integer size,
            String fields, String sortField, String highlightField) throws IOException {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(query);

        if (!StringUtils.isEmpty(fields)) {
            sourceBuilder.fetchSource(fields.split(","), null);
        }

        //排序字段
        if (!StringUtils.isEmpty(sortField)) {
            sourceBuilder.sort(sortField, SortOrder.DESC);
        }

        // 高亮（xxx=111,aaa=222）
        if (!StringUtils.isEmpty(highlightField)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();

            //highlightBuilder.preTags("<span style='color:red' >");//设置前缀
            //highlightBuilder.postTags("</span>");//设置后缀

            // 设置高亮字段
            highlightBuilder.field(highlightField);
            sourceBuilder.highlighter(highlightBuilder);
        }

        // 设置是否按查询匹配度排序
        sourceBuilder.explain(true);

        // 执行搜索,返回搜索响应信息
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        long totalHits = response.getHits().totalHits;
        long length = response.getHits().getHits().length;

        LOGGER.debug("共查询到[{}]条数据,处理数据条数[{}]", totalHits, length);

        if (response.status().getStatus() == 200) {
            // 解析对象
            return setSearchResponse(response, highlightField);
        }
        return null;

    }


    /**
     * 高亮结果集 特殊处理
     *
     * @param searchResponse
     * @param highlightField
     */
    private static List<Map<String, Object>> setSearchResponse(SearchResponse searchResponse, String highlightField) {
        List<Map<String, Object>> sourceList = new ArrayList<Map<String, Object>>();
        StringBuffer stringBuffer = new StringBuffer();

        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            searchHit.getSourceAsMap().put("id", searchHit.getId());

            if (!StringUtils.isEmpty(highlightField)) {

                System.out.println("遍历 高亮结果集，覆盖 正常结果集" + searchHit.getSourceAsMap());
                Text[] text = searchHit.getHighlightFields().get(highlightField).getFragments();

                if (text != null) {
                    for (Text str : text) {
                        stringBuffer.append(str.string());
                    }
//遍历 高亮结果集，覆盖 正常结果集
                    searchHit.getSourceAsMap().put(highlightField, stringBuffer.toString());
                }
            }
            sourceList.add(searchHit.getSourceAsMap());
        }

        return sourceList;
    }

    /**
     * 查询最大值
     *
     * @param index
     * @param type
     * @param field
     */
    public static int searchMaxValue(String index, String type, String field) throws IOException {
        AggregationBuilder termsBuilder = AggregationBuilders.max("max").field(field);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(termsBuilder);

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Max max = response.getAggregations().get("max");
        int value = (int) max.getValue();
        return value;
    }

}