package com.app.tvproject.http;

import com.app.tvproject.mvp.model.data.BaseEntity;
import com.app.tvproject.mvp.model.data.ChooseSettingsBean;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.EqInformationBean;
import com.app.tvproject.mvp.model.data.PublishListBean;
import com.app.tvproject.mvp.model.data.UpdateBean;
import com.app.tvproject.mvp.model.data.UpdateUseEqBean;
import com.app.tvproject.mvp.model.data.WeatherBean;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by www on 2017/11/13.
 * 定义网络请求接口
 */

public interface Api {

    /**
     * 获取省市区设备等信息
     * @param map
     * @return
     */
    @POST("getCommunity")
    Observable<ChooseSettingsBean> getSettingsData(@QueryMap Map<String, String> map);


    /**
     *  更新设备是否被占用
     */

    @POST("updateUse")
    Observable<UpdateUseEqBean> setEquipmentUsed(@QueryMap Map<String, String> map);


    /**
     * 获取服务器正在播放的内容集合
     * @param map
     * @return
     */
    @POST("getPublishDetailList")
    Observable<PublishListBean> getPublishList(@QueryMap Map<String, String> map);


    /**
     *    获取显示内容信息
     */
    @POST("getPublishDetail")
    Observable<BaseEntity<ContentBean>> getPublishContent(@QueryMap Map<String, String> map);


    /**
     *  更新设备的连接状态
     */

    @POST("updateConnect")
    Observable<UpdateUseEqBean> updateEqStatus(@QueryMap Map<String, String> map);

    /**下载文件
     *
     * @param url
     * @return
     */
    @GET
    @Streaming
    Observable<ResponseBody> dowLoadFile(@Url String url);

    /**
     * 获取天气
     * @return
     */
    @POST("getWeather")
    Observable<WeatherBean> getWeather();

    /**
     * 更新app
     */
    @GET("getTouchUpdate")
    Observable<BaseEntity<UpdateBean>> getUpdateInfo(@QueryMap Map<String, String> map);


    /**
     * 获取设备相关信息
     */
    @POST("getEqDetail")
    Observable<BaseEntity<EqInformationBean>> getEqInfo(@QueryMap Map<String,String> map);
}
