package com.gccloud.starter.lowcode.page.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gccloud.starter.common.mybatis.service.ISuperService;
import com.gccloud.starter.lowcode.common.utils.AssertUtils;
import com.gccloud.starter.lowcode.page.entity.PageEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author liuchengbiao
 * @version 1.0
 * @date 2022/8/8 15:11
 */
public interface IBasePageService extends ISuperService<PageEntity> {

    /**
     * 获取指定类型页面所有名称
     *
     * @param type
     * @return
     */
    default Set<String> getAllName(String type) {
        AssertUtils.isTrue(StringUtils.isNotBlank(type), "type不能为空");
        LambdaQueryWrapper<PageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PageEntity::getType, type);
        queryWrapper.select(PageEntity::getName);
        List<PageEntity> list = getBaseDao().selectList(queryWrapper);
        Set<String> allName = list.stream().map(PageEntity::getName).collect(Collectors.toSet());
        return allName;
    }

    /**
     * 根据编码查询
     *
     * @param code
     * @return
     */
    default PageEntity getByCode(String code) {
        AssertUtils.isTrue(StringUtils.isNotBlank(code), "页面编码不能为空");
        LambdaQueryWrapper<PageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PageEntity::getCode, code);
        PageEntity pageEntity = getBaseDao().selectOne(queryWrapper);
        return pageEntity;
    }

    /**
     * 名称查重
     * @param entity
     * @return
     */
    default boolean checkNameRepeat(PageEntity entity) {
        AssertUtils.isTrue(StringUtils.isNotBlank(entity.getName()), "名称不能为空");
        LambdaQueryWrapper<PageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PageEntity::getAppCode, entity.getAppCode())
                .eq(PageEntity::getName, entity.getName())
                .ne(StringUtils.isNotBlank(entity.getId()), PageEntity::getId, entity.getId());
        return getBaseDao().selectCount(queryWrapper) > 0;
    }

    /**
     * 名称查重
     * @param appCode
     * @param name
     * @param id
     * @return
     */
    default boolean checkNameRepeat(String appCode, String name, String id) {
        AssertUtils.isTrue(StringUtils.isNotBlank(name), "名称不能为空");
        LambdaQueryWrapper<PageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PageEntity::getAppCode, appCode)
                .eq(PageEntity::getName, name)
                .ne(StringUtils.isNotBlank(id), PageEntity::getId, id);
        return getBaseDao().selectCount(queryWrapper) > 0;
    }


    /**
     * 编码查重
     * @param entity
     * @return
     */
    default boolean checkCodeRepeat(PageEntity entity) {
        AssertUtils.isTrue(StringUtils.isNotBlank(entity.getCode()), "编码不能为空");
        LambdaQueryWrapper<PageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PageEntity::getAppCode, entity.getAppCode())
                .eq(PageEntity::getCode, entity.getCode())
                .ne(StringUtils.isNotBlank(entity.getId()), PageEntity::getId, entity.getId());
        return getBaseDao().selectCount(queryWrapper) > 0;
    }

}
