package com.gccloud.bigscreen.core.module.file.vo;

import com.gccloud.bigscreen.core.module.file.entity.BigScreenFileEntity;
import lombok.Data;

import java.io.Serializable;
import java.text.DecimalFormat;

@Data
public class SysFileVO extends BigScreenFileEntity implements Serializable {
    /**
     * 暂用空间，根据size进行计算出来
     */
    private String space;

    public String getSpace() {
        long size = this.getSize();
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1000));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
    }
}
