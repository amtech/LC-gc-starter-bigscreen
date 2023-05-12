package com.gccloud.plugin.ds.dao;

import com.gccloud.plugin.ds.entity.DatasourceConfig;
import com.gccloud.starter.common.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author pan.shun
 * @since 2021/9/6 14:58
 */
@Mapper
public interface DatasourceConfigDao extends BaseDao<DatasourceConfig> {

}
