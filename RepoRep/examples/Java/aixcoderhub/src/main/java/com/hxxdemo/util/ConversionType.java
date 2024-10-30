package com.hxxdemo.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class ConversionType implements Serializable {

    /*
        落地页Url
     */
    private String logidUrl;
    /*
        转化类型
     */
    private Integer convertType;

    public String getLogidUrl() {
        return logidUrl;
    }

    public void setLogidUrl(String logidUrl) {
        this.logidUrl = logidUrl;
    }

    public Integer getConvertType() {
        return convertType;
    }

    public void setConvertType(Integer convertType) {
        this.convertType = convertType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
