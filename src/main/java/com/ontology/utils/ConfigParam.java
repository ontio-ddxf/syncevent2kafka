package com.ontology.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

	/**
	 *  SDK参数
	 */
	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	/**
	 *  清空之前同步的块高记录开关
	 *  (只清除块高记录，不清除区块事件)
	 */
	@Value("${clear.sync.switch}")
	public boolean CLEAR_SYNC_SWITCH;

	/**
	 *  同步之前块高记录开关
	 */
	@Value("${sync.preblock.switch}")
	public boolean SYNC_PREBLOCK_SWITCH;
}