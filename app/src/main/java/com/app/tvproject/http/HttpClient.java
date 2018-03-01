package com.app.tvproject.http;

import com.app.tvproject.BuildConfig;
import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.data.BaseEntity;
import com.app.tvproject.mvp.model.data.ChooseSettingsBean;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.PublishListBean;
import com.app.tvproject.mvp.model.data.UpdateBean;
import com.app.tvproject.mvp.model.data.UpdateUseEqBean;
import com.app.tvproject.mvp.model.data.WeatherBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by www on 2017/11/13.
 */

public final class HttpClient {

    /**
     * HttpClient 对象
     */
    private static volatile HttpClient sHttpClient;

    /**
     * mmApi 接口
     */
    private final Api mApi;
    private Gson mGson;

    /**
     * 私有的构造方法
     */
    private HttpClient(String host) {
        //                        FileUtil.writeLog(message);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger.DEFAULT);
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        mApi = retrofit.create(Api.class);
    }

    /**
     * @return return {@link HttpClient} 单例
     */
    public static HttpClient getInstance() {
        if (sHttpClient == null) {
            synchronized (HttpClient.class) {
                if (sHttpClient == null) {
                    sHttpClient = new HttpClient(BuildConfig.HOST+"/wzt/appTs/");
                }
            }
        }
        return sHttpClient;
    }

    /**
     * 线程切换
     *
     * @param o   {@link Observable}
     * @param s   {@link Subscriber}
     * @param <T> 可变类型
     */
    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    public RequestBody getMapRequestBody(HashMap<String, String> params) {
        return RequestBody.create(MediaType.parse(Constants.CONTENT_TYPE), getGson().toJson(params));
    }

    public RequestBody getObjRequestBody(Object o) {
        return RequestBody.create(MediaType.parse(Constants.CONTENT_TYPE), getGson().toJson(o));
    }

    private Gson getGson() {
        if (mGson == null)
            mGson = new GsonBuilder().disableHtmlEscaping().create();
        return mGson;
    }

    /**
     * 获取省市区设备等信息
     * @param subscriber
     * @param type
     * @param id
     */
    public void getSettingsData(Subscriber<ChooseSettingsBean> subscriber, String type, String id ){
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("type",type);
        if(id != null)
            queryMap.put("id",id);
        queryMap.put("eqType", "2");
        Observable observable = mApi.getSettingsData(queryMap);
        toSubscribe(observable,subscriber);
    }


    /**
     * 更新设备为已使用状态
     * @param subscriber
     * @param eqId
     * @param use
     */
    public void setEquipmentUsed(Subscriber<UpdateUseEqBean> subscriber, String eqId, String use){
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("eqId",eqId);
        queryMap.put("use",use);
        Observable observable = mApi.setEquipmentUsed(queryMap);
        toSubscribe(observable,subscriber);
    }

    /**
     * 获取服务器正在播放的内容集合
     * @return
     */
    public void getPublishList(Subscriber<PublishListBean> subscriber, String eqId, String publishTypeId){
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("eqId",eqId);
        if(publishTypeId != null)
            queryMap.put("publishTypeId",publishTypeId);
        Observable observable = mApi.getPublishList(queryMap);
        toSubscribe(observable,subscriber);
    }

    /**
     * 更新设备连接状态
     */

    public void updateEqStatus(Subscriber<UpdateUseEqBean> subscriber,String eqId, String status){
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("eqId",eqId);
        queryMap.put("status",status);
        Observable observable = mApi.updateEqStatus(queryMap);
        toSubscribe(observable,subscriber);
    }

    /**
     * 获取天气
     */
    public void getWeather(Subscriber<WeatherBean> subscriber){
        Observable observable = mApi.getWeather();
        toSubscribe(observable,subscriber);
    }




    /**
     * 获取推送的内容详情
     * @param subscriber
     * @param pdId
     * @param eqId
     */
    public void getPublishContent(Subscriber<BaseEntity<ContentBean>> subscriber,String pdId,String eqId){
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("pdId",pdId);
        queryMap.put("eqId",eqId);
        Observable observable = mApi.getPublishContent(queryMap);
        toSubscribe(observable,subscriber);
    }

    /**
     * 下载文件
     */
    public void dowLoadFile(Subscriber<ResponseBody> subscriber,String url){
        Observable observable = mApi.dowLoadFile(url);
        toSubscribe(observable,subscriber);
    }

    /**
     * 更新APP
     */
    public void getUpdateInfo(Subscriber<BaseEntity<UpdateBean>> subscriber){
        HashMap<String,String> queryMap = new HashMap<>();
        queryMap.put("type","2");
        Observable observable = mApi.getUpdateInfo(queryMap);
        toSubscribe(observable,subscriber);
    }
}
