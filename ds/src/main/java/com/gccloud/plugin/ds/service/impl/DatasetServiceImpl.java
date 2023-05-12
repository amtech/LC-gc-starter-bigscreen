package com.gccloud.plugin.ds.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.gccloud.plugin.ds.config.JdbcConfig;
import com.gccloud.plugin.ds.constant.ReportConstant;
import com.gccloud.plugin.ds.dao.DatasetDao;
import com.gccloud.plugin.ds.dto.DataSetQueryDto;
import com.gccloud.plugin.ds.dto.DatasetParamDto;
import com.gccloud.plugin.ds.dto.ExecuteDto;
import com.gccloud.plugin.ds.dto.NameCheckRepeatDto;
import com.gccloud.plugin.ds.entity.DatasetEntity;
import com.gccloud.plugin.ds.service.CategoryTreeService;
import com.gccloud.plugin.ds.service.DatasetProcessService;
import com.gccloud.plugin.ds.service.DatasetService;
import com.gccloud.plugin.ds.service.OriginalTableService;
import com.gccloud.starter.common.exception.GlobalException;
import com.gccloud.starter.common.json.JSON;
import com.gccloud.starter.common.mybatis.page.PageVO;
import com.gccloud.starter.common.utils.GroovyUtils;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author zhang.zeJun
 * @date 2022-11-15-9:48
 */
@Slf4j
@Service
public class DatasetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements DatasetService {

    @Lazy
    @Resource
    private DatasetProcessService datasetProcessService;

    @Lazy
    @Resource
    private OriginalTableService originalTableService;

    @Resource
    private CategoryTreeService categoryTreeService;


    @Override
    public PageVO<DatasetEntity> getPage(DataSetQueryDto dataSetQueryDto) {
        Page<DatasetEntity> page = new Page<>(dataSetQueryDto.getCurrent(), dataSetQueryDto.getSize());
        String typeId = dataSetQueryDto.getTypeId();
        Set<String> idList = new HashSet<>();
        if (!StringUtils.isEmpty(typeId)) {
            idList.add(typeId);
            Set<String> data = categoryTreeService.getAllChildren(idList, typeId);
            idList.addAll(data);
        }
        Page<DatasetEntity> result;
        String dbType = JdbcUtils.getDbType(JdbcConfig.currentUrl).getDb();
        if (DbType.ORACLE.getDb().equals(dbType)) {
            result = baseMapper.getOracleDataSetPage(
                    page,
                    dataSetQueryDto.getName(),
                    dataSetQueryDto.getDatasetType(),
                    dataSetQueryDto.getTypeId(),
                    idList,
                    dataSetQueryDto.getModuleCode());
        } else {
            result = baseMapper.getDataSetPage(
                    page,
                    dataSetQueryDto.getName(),
                    dataSetQueryDto.getDatasetType(),
                    dataSetQueryDto.getTypeId(),
                    idList,
                    dataSetQueryDto.getModuleCode());
        }
        return new PageVO<>(result);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeByIds(String ids) {
        if (StringUtils.isEmpty(ids)) {
            throw new GlobalException("暂无可删除的内容");
        }
        String[] idList = ids.split(",");
        for (String id : idList) {
            DatasetEntity datasetEntity = this.getById(id);
            String datasetType = datasetEntity.getDatasetType();
            if (ReportConstant.DataSetType.ORIGINAL.equals(datasetType)) {
                originalTableService.removeById(datasetEntity.getDatasetRelId());
            }
            if (ReportConstant.DataSetType.CUSTOM.equals(datasetType) || ReportConstant.DataSetType.STORED_PROCEDURE.equals(datasetType)) {
                datasetProcessService.removeById(datasetEntity.getDatasetRelId());
            }
            this.removeById(id);
        }
    }

    @Override
    public Boolean nameCheckRepeat(NameCheckRepeatDto nameCheckRepeatDto) {
        LambdaQueryWrapper<DatasetEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(StringUtils.isNotBlank(nameCheckRepeatDto.getId()), DatasetEntity::getId, nameCheckRepeatDto.getId());
        wrapper.eq(DatasetEntity::getName, nameCheckRepeatDto.getName());
        wrapper.eq(StringUtils.isNotBlank(nameCheckRepeatDto.getModuleCode()), DatasetEntity::getModuleCode, nameCheckRepeatDto.getModuleCode());
        int count = this.count(wrapper);
        return count != 0;
    }


    @Override
    public Object executeDataSet(ExecuteDto executeDto) {
        if (!StringUtils.isEmpty(executeDto.getDataSetId())) {
            String dataSetId = executeDto.getDataSetId();
            DatasetEntity datasetEntity = this.getById(dataSetId);
            if (datasetEntity.getDatasetType().equals(ReportConstant.DataSetType.JSON) || datasetEntity.getDatasetType().equals(ReportConstant.DataSetType.MODEL)) {
                // JSON 数据集和模型数据集
                return datasetEntity.getData();
            }
            if (datasetEntity.getDatasetType().equals(ReportConstant.DataSetType.SCRIPT)) {
                // 脚本数据集
                try {
                    return runScriptDataSet(datasetEntity.getData(), executeDto);
                } catch (Exception e) {
                    throw new GlobalException(e.getMessage());
                }
            }
        } else {
            if (executeDto.getDataSetType().equals(ReportConstant.DataSetType.JSON) || executeDto.getDataSetType().equals(ReportConstant.DataSetType.MODEL)) {
                // JSON 数据集和模型数据集
                return executeDto.getData();
            }
            if (executeDto.getDataSetType().equals(ReportConstant.DataSetType.SCRIPT)) {
                // 脚本数据集
                try {
                    return runScriptDataSet(executeDto.getData(), executeDto);
                } catch (Exception e) {
                    throw new GlobalException(e.getMessage());
                }
            }
        }
        return null;
    }

    private Object runScriptDataSet(String data, ExecuteDto executeDto) {
        JSONObject object = JSON.parseObject(data);
        Map<String, Object> paramMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(executeDto.getParams())) {
            List<DatasetParamDto> params = executeDto.getParams();
            params.forEach(r -> paramMap.put(r.getName(), r.getValue()));
        }
        String script = object.getString("script");
        Class clazz = GroovyUtils.buildClass(script);
        if (clazz == null) {
            throw new GlobalException("脚本编译异常");
        }
        return GroovyUtils.run(script, paramMap);
    }

    @Override
    public void addOrUpdate(DatasetEntity datasetEntity) {
        if (StringUtils.isEmpty(datasetEntity.getTypeId())) {
            datasetEntity.setTypeId(null);
        }
        if (!StringUtils.isEmpty(datasetEntity.getId())) {
            this.updateById(datasetEntity);
        } else {
            this.save(datasetEntity);
        }
    }

    @Override
    public List<DatasetEntity> getList(DataSetQueryDto dataSetQueryDto) {
        String typeId = dataSetQueryDto.getTypeId();
        Set<String> idList = new HashSet<>();
        if (!StringUtils.isEmpty(typeId)) {
            idList.add(typeId);
            Set<String> data = categoryTreeService.getAllChildren(idList, typeId);
            idList.addAll(data);
        }
        String dbType = JdbcUtils.getDbType(JdbcConfig.currentUrl).getDb();
        if (DbType.ORACLE.getDb().equals(dbType)) {
            return this.baseMapper.getOracleList(
                    dataSetQueryDto.getName(),
                    dataSetQueryDto.getDatasetType(),
                    dataSetQueryDto.getTypeId(),
                    idList,
                    dataSetQueryDto.getModuleCode());
        }
        return this.baseMapper.getList(
                dataSetQueryDto.getName(),
                dataSetQueryDto.getDatasetType(),
                dataSetQueryDto.getTypeId(),
                idList,
                dataSetQueryDto.getModuleCode());
    }

    @Override
    public Object getDataSetDetailById(String dataSetId) {

        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(
                DatasetEntity::getId,
                DatasetEntity::getName,
                DatasetEntity::getTypeId,
                DatasetEntity::getRemark,
                DatasetEntity::getDatasetType,
                DatasetEntity::getModuleCode,
                DatasetEntity::getDatasetRelId,
                DatasetEntity::getData
        );
        queryWrapper.eq(DatasetEntity::getId, dataSetId);
        return this.getOne(queryWrapper);
    }
}
