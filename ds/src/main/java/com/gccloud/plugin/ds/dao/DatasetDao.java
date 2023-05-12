package com.gccloud.plugin.ds.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gccloud.plugin.ds.entity.DatasetEntity;
import com.gccloud.starter.common.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author zhang.zeJun
 * @date 2022-11-14-11:41
 */
@Mapper
public interface DatasetDao extends BaseDao<DatasetEntity> {

    /**
     * 获取数据集分页列表
     *
     * @param page
     * @param name
     * @param datasetType
     * @param typeId
     * @param typeIds
     * @param moduleCode
     * @return
     */
    Page<DatasetEntity> getDataSetPage(
            Page<DatasetEntity> page,
            @Param("name") String name,
            @Param("datasetType") String datasetType,
            @Param("typeId") String typeId,
            @Param("typeIds") Set<String> typeIds,
            @Param(("moduleCode")) String moduleCode);

    /**
     * oracles数据源获取数据集分页列表
     *
     * @param page
     * @param name
     * @param datasetType
     * @param typeId
     * @param typeIds
     * @param moduleCode
     * @return
     */
    Page<DatasetEntity> getOracleDataSetPage(
            Page<DatasetEntity> page,
            @Param("name") String name,
            @Param("datasetType") String datasetType,
            @Param("typeId") String typeId,
            @Param("typeIds") Set<String> typeIds,
            @Param(("moduleCode")) String moduleCode);


    /**
     * 获取数据集列表
     *
     * @param name
     * @param datasetType
     * @param typeId
     * @param typeIds
     * @param moduleCode
     * @return
     */
    List<DatasetEntity> getList(
            @Param("name") String name,
            @Param("datasetType") String datasetType,
            @Param("typeId") String typeId,
            @Param("typeIds") Set<String> typeIds,
            @Param("moduleCode") String moduleCode);

    /**
     * oracles数据源获取数据集列表
     *
     * @param name
     * @param datasetType
     * @param typeId
     * @param typeIds
     * @param moduleCode
     * @return
     */
    List<DatasetEntity> getOracleList(
            @Param("name") String name,
            @Param("datasetType") String datasetType,
            @Param("typeId") String typeId,
            @Param("typeIds") Set<String> typeIds,
            @Param("moduleCode") String moduleCode);
}
