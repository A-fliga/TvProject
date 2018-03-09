package com.app.tvproject.mvp.model;

import com.app.tvproject.http.HttpClient;
import com.app.tvproject.mvp.model.data.BaseEntity;
import com.app.tvproject.mvp.model.data.ChooseSettingsBean;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.EqInformationBean;
import com.app.tvproject.mvp.model.data.PublishListBean;
import com.app.tvproject.mvp.model.data.UpdateBean;
import com.app.tvproject.mvp.model.data.UpdateUseEqBean;
import com.app.tvproject.mvp.model.data.WeatherBean;
import com.app.tvproject.utils.NetUtil;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by www on 6/7/2017.
 * 公共Model类
 */
public class PublicModel implements IModel {


    private PublicModel() {
    }

    private static PublicModel model;

    public static PublicModel getInstance() {
        if (null == model)
            model = new PublicModel();
        return model;
    }

    /**
     * 获取省市等设备信息配置
     *
     * @param subscriber
     * @param type
     * @param id
     */
    public void getSettingsData(Subscriber<ChooseSettingsBean> subscriber, String type, String id) {
        if (NetUtil.isConnect()) {
            HttpClient.getInstance().getSettingsData(subscriber, type, id);
        }
    }

    /**
     * 更新设备可用状态
     */
    public void setEquipmentUsed(Subscriber<UpdateUseEqBean> subscriber, String eqId, String use) {
        if (NetUtil.isConnect()) {
            HttpClient.getInstance().setEquipmentUsed(subscriber, eqId, use);
        }
    }

    /**
     * 获取服务器正在播放的内容集合
     *
     * @return
     */
    public void getPublishList(Subscriber<PublishListBean> subscriber, String eqId, String publishTypeId) {
        HttpClient.getInstance().getPublishList(subscriber, eqId, publishTypeId);
    }


    /**
     * 更新设备连接状态
     */
    public void updateEqStatus(Subscriber<UpdateUseEqBean> subscriber, String eqId, String status) {
        HttpClient.getInstance().updateEqStatus(subscriber, eqId, status);
    }

    /**
     * 获取天气
     */
    public void getWeather(Subscriber<WeatherBean> subscriber) {
        HttpClient.getInstance().getWeather(subscriber);
    }

    /**
     * 获取推送的内容详情
     *
     * @param subscriber
     * @param pdId
     * @param eqId
     */
    public void getPublishContent(Subscriber<BaseEntity<ContentBean>> subscriber, String pdId, String eqId) {
        HttpClient.getInstance().getPublishContent(subscriber, pdId, eqId);
    }


    /**
     * 下载文件
     */
    public synchronized void dowLoadFile(Subscriber<ResponseBody> subscriber, String url) {
        HttpClient.getInstance().dowLoadFile(subscriber, url);
    }

    /**
     * 更新APP
     */
    public void getUpdateInfo(Subscriber<BaseEntity<UpdateBean>> subscriber){
        HttpClient.getInstance().getUpdateInfo(subscriber);
    }

    /**
     * 获取设备相关信息
     */
    public void getEqInfo(Subscriber<BaseEntity<EqInformationBean>> subscriber, String eqId) {
        if(NetUtil.isConnect()){
            HttpClient.getInstance().getEqInfo(subscriber,eqId);
        }
    }
}