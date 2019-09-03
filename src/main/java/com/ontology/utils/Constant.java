package com.ontology.utils;

import java.math.BigDecimal;

/**
 * 常量
 */
public class Constant {

    /**
     * ES索引和类型
     */
    public static final String ES_INDEX_SYNC = "sync_index";
    public static final String ES_TYPE_SYNC = "blockHeight";

    /**
     * sdk手续费
     */
    public static final long GAS_PRICE = 0;
    public static final long GAS_Limit = 20000;


    public static final String ONTID_PREFIX = "did:ont:%s";

    /**
     * 积分精度
     */
    public static final BigDecimal DECIMAL = new BigDecimal(Math.pow(10,9));
}
