package com.gccloud.starter.lowcode.page.bigscreen.controller;

import com.gccloud.starter.common.config.GlobalConfig;
import com.gccloud.starter.common.constant.GlobalConst;
import com.gccloud.starter.common.exception.GlobalException;
import com.gccloud.starter.common.utils.BeanConvertUtils;
import com.gccloud.starter.common.validator.ValidatorUtils;
import com.gccloud.starter.common.validator.group.Insert;
import com.gccloud.starter.common.validator.group.Update;
import com.gccloud.starter.common.vo.R;
import com.gccloud.starter.lowcode.common.vo.MixinsResp;
import com.gccloud.starter.lowcode.page.bigscreen.dto.BigScreenPageDTO;
import com.gccloud.starter.lowcode.page.bigscreen.service.IBigScreenPageService;
import com.gccloud.starter.lowcode.page.bigscreen.vo.StaticFileVO;
import com.gccloud.starter.lowcode.page.entity.PageEntity;
import com.gccloud.starter.lowcode.webjars.Webjars;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/3/13 10:55
 */
@Slf4j
@RestController
@RequestMapping("/bigScreen/design")
@Api(tags = "大屏页设计")
public class BigScreenPageController {

    @Resource
    private IBigScreenPageService bigScreenPageService;
    @Resource
    private GlobalConfig globalConfig;

    @GetMapping("/info/code/{code}")
    @ApiOperation(value = "大屏页详情", position = 10, produces = MediaType.APPLICATION_JSON_VALUE)
    public MixinsResp<BigScreenPageDTO> info(@PathVariable("code") String code) {
        PageEntity bigScreen = bigScreenPageService.getByCode(code);
        BigScreenPageDTO bigScreenPageDTO = (BigScreenPageDTO) bigScreen.getConfig();
        BeanConvertUtils.convert(bigScreen, bigScreenPageDTO);
        MixinsResp<BigScreenPageDTO> resp = new MixinsResp<BigScreenPageDTO>().setData(bigScreenPageDTO);
        resp.setCode(GlobalConst.Response.Code.SUCCESS);
        return resp;
    }

    @PostMapping("/add")
    @ApiOperation(value = "从空白新增大屏页", position = 20, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<String> add(@RequestBody BigScreenPageDTO bigScreenPageDTO) {
        ValidatorUtils.validateEntity(bigScreenPageDTO, Insert.class);
        bigScreenPageService.add(bigScreenPageDTO);
        if (StringUtils.isBlank(bigScreenPageDTO.getParentCode())) {
            bigScreenPageDTO.setParentCode("0");
        }
        return R.success(bigScreenPageDTO.getCode());
    }

    @PostMapping("/add/template")
    @ApiOperation(value = "从模板新增大屏页", position = 20, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<String> addByTemplate(@RequestBody BigScreenPageDTO bigScreenPageDTO) {
        String code = bigScreenPageService.addByTemplate(bigScreenPageDTO);
        if (StringUtils.isBlank(bigScreenPageDTO.getParentCode())) {
            bigScreenPageDTO.setParentCode("0");
        }
        return R.success(code);
    }

    @PostMapping("/get/template")
    @ApiOperation(value = "根据模板获取配置", position = 20, produces = MediaType.APPLICATION_JSON_VALUE)
    public MixinsResp<BigScreenPageDTO> getByTemplate(@RequestBody BigScreenPageDTO bigScreenPageDTO) {
        BigScreenPageDTO config = bigScreenPageService.getConfigByTemplate(bigScreenPageDTO);
        MixinsResp<BigScreenPageDTO> resp = new MixinsResp<BigScreenPageDTO>().setData(config);
        resp.setCode(GlobalConst.Response.Code.SUCCESS);
        return resp;
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改大屏页", position = 30, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<String> update(@RequestBody BigScreenPageDTO bigScreenPageDTO) {
        ValidatorUtils.validateEntity(bigScreenPageDTO, Update.class);
        if (StringUtils.isBlank(bigScreenPageDTO.getParentCode())) {
            bigScreenPageDTO.setParentCode("0");
        }
        bigScreenPageService.update(bigScreenPageDTO);
        return R.success(bigScreenPageDTO.getCode());
    }

    @PostMapping("/delete/{code}")
    @ApiOperation(value = "删除大屏页", position = 40, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<Void> delete(@PathVariable String code) {
        PageEntity bigScreenPage = bigScreenPageService.getByCode(code);
        if (bigScreenPage == null) {
            return R.success();
        }
        bigScreenPageService.deleteByCode(code);
        return R.success();
    }

    @PostMapping("/copy/{code}")
    @ApiOperation(value = "复制大屏页", position = 50, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<String> copy(@PathVariable String code) {
        PageEntity bigScreenPage = bigScreenPageService.getByCode(code);
        if (bigScreenPage == null) {
            throw new GlobalException("大屏页不存在");
        }
        String newCode = bigScreenPageService.copy(code);
        return R.success(newCode);
    }

    @GetMapping("/bg/list")
    @ApiOperation(value = "背景图片列表", position = 60, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<StaticFileVO>> getBgList() {
        List<String> staticFileList = Webjars.BIG_SCREEN_BG;
        List<StaticFileVO> bgList = Lists.newArrayList();
        String urlPrefix = globalConfig.getFile().getUrlPrefix() + "bigScreenBg/";
        for (String fileName : staticFileList) {
            StaticFileVO fileVO = new StaticFileVO();
            fileVO.setUrl(urlPrefix + fileName);
            fileVO.setName(fileName);
            bgList.add(fileVO);
        }
        return R.success(bgList);
    }

    @GetMapping("/map/list/{level}")
    @ApiOperation(value = "地图数据列表", position = 60, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<StaticFileVO>> getMapJsonList(@PathVariable("level") String level) {
        List<String> staticFileList = Lists.newArrayList();
        if ("country".equals(level)) {
            staticFileList = Webjars.COUNTRY_MAP_DATA;
        }
        if ("province".equals(level)) {
            staticFileList = Webjars.PROVINCE_MAP_DATA;
        }
        List<StaticFileVO> bgList = Lists.newArrayList();
        String urlPrefix = "static/chinaMap/" + level + "/";
        for (String fileName : staticFileList) {
            StaticFileVO fileVO = new StaticFileVO();
            fileVO.setUrl(fileName);
            fileVO.setName(fileName.replace(".json", ""));
            bgList.add(fileVO);
        }
        return R.success(bgList);
    }




}