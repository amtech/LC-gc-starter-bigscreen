package com.gccloud.starter.lowcode.page.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gccloud.starter.common.entity.SuperEntity;
import com.gccloud.starter.lowcode.page.constant.PageDesignConstant;
import com.gccloud.starter.lowcode.page.dto.BasePageDTO;
import com.gccloud.starter.lowcode.page.entity.type.BasePageDTOTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 页面基本信息表
 *
 * @Author qianxing
 * @Date 2022/06/07
 * @Version 1.0.0
 */
@Data
@TableName(value = "big_screen_page", autoResultMap = true)
@ApiModel
public class PageEntity extends SuperEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "页面中文名称")
    private String name;

    @ApiModelProperty(notes = "页面编码，页面唯一标识符")
    private String code;

    @ApiModelProperty(notes = "父节点编码")
    private String parentCode;

    /**
     * 参考：{@link PageDesignConstant.Type}
     */
    @ApiModelProperty(notes = "页面类型")
    private String type;

    @ApiModelProperty(notes = "页面图标")
    private String icon;

    @ApiModelProperty(notes = "图标颜色")
    private String iconColor;

    @ApiModelProperty(notes = "具体组件配置、JSON格式")
    @TableField(typeHandler = BasePageDTOTypeHandler.class)
    private BasePageDTO config;

    @ApiModelProperty(notes = "备注")
    private String remark;

    @ApiModelProperty(notes = "排序")
    private Integer orderNum;

    @ApiModelProperty(notes = "所属应用编码")
    private String appCode;

    @ApiModelProperty(notes = "出码目录")
    private String codePath;


}
