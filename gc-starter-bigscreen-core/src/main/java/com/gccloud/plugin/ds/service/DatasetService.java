package com.gccloud.plugin.ds.service;

import com.gccloud.plugin.ds.dto.DataSetQueryDto;
import com.gccloud.plugin.ds.dto.ExecuteDto;
import com.gccloud.plugin.ds.dto.NameCheckRepeatDto;
import com.gccloud.plugin.ds.entity.DatasetEntity;
import com.gccloud.starter.common.mybatis.page.PageVO;
import com.gccloud.starter.common.mybatis.service.ISuperService;

import java.util.List;

/**
 * @author zhang.zeJun
 * @date 2022-11-15-9:47
 */
public interface DatasetService extends ISuperService<DatasetEntity> {

    /**
     * 主数据集分页查询
     *
     * @param dataSetQueryDto
     * @return
     */
    PageVO<DatasetEntity> getPage(DataSetQueryDto dataSetQueryDto);

    /**
     * 数据集删除
     *
     * @param ids
     */
    void removeByIds(String ids);

    /**
     * 查重
     *
     * @param nameCheckRepeatDto
     * @return
     */
    Boolean nameCheckRepeat(NameCheckRepeatDto nameCheckRepeatDto);

    /**
     * 数据集执行，获取数据
     *
     * @param executeDto
     * @return
     */
    Object executeDataSet(ExecuteDto executeDto);

    /**
     * 主数据集新增或修改
     *
     * @param datasetEntity
     */
    void addOrUpdate(DatasetEntity datasetEntity);

    /**
     * 获取数据集列表
     *
     * @param dataSetQueryDto
     * @return
     */
    List<DatasetEntity> getList(DataSetQueryDto dataSetQueryDto);

    /**
     * 根据数据集ID获取数据集详情
     *
     * @param dataSetId
     * @return
     */
    Object getDataSetDetailById(String dataSetId);

}
