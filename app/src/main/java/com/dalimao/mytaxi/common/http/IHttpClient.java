package com.dalimao.mytaxi.common.http;

/**
 * Created by liulan on 2018/5/12.
 */

public interface IHttpClient {
    IResponse get(IRequest request,boolean forceCache);
    IResponse post(IRequest request,boolean forceCache);
}
