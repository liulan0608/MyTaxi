package com.dalimao.mytaxi.common.http;

import java.util.Map;
import java.util.Objects;

/**
 * Created by liulan on 2018/5/12.
 * 定义请求数据的封装类型
 */

public interface IRequest {
    public static final String POST = "POST";
    public static final String GET = "GET";
    void setMethod(String method);
    void setHeader(String key,String value);
    void setBody(String key,String value);
    String getUrl();
    Map<String,String> getHeader();
    String getBody();
}
