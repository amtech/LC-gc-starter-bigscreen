package com.gccloud.bigscreen.core.module.chart.components;

import com.gccloud.bigscreen.core.module.chart.bean.Chart;
import com.gccloud.bigscreen.core.constant.PageDesignConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/3/13 16:44
 */
@Data
public class ScreenSvgsChart extends Chart {

    @ApiModelProperty(notes = "类型")
    private String type = PageDesignConstant.BigScreen.Type.SVGS;

    @ApiModelProperty(notes = "图标")
    private String icon;

}
