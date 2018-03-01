package com.app.tvproject.constants;

import android.os.Environment;

import java.io.File;

/**
 * Created by www on 2017/11/13.
 * 常量类
 */

public final class Constants {

    //极光别名，不可修改
    public static final String JPUSH_NAME = "touchid";

    public static final String TV_PROJECT_SHARED_NAME = "TV_PROJECT";
    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    public static final long DEFAULT_ID = -1000;

    //选择配置信息的code
    public static final int CHOOSE_SETTINGS_REQUEST_CODE = 1000;

    public static final int CHOOSE_SETTINGS_RESULT_CODE = 1001;

    //SharedPreference的key值，不要重复

    public static final String JI_GUANG_TAG = "JI_GUANG_TAG";//极光别名
    public static final String INFORMATION_POSITION = "INFORMATION_POSITION";//正在播放的资讯内容下标
    public static final String NOTICE_ID = "NOTICE_ID";//正在播放的通知内容id
    public static final String INFORMATION_ID = "INFORMATION_ID";//正在播放的资讯id
    public static final String NOTICE_POSITION = "NOTICE_POSITION";//正在播放的通知的下标
    public static final String LAST_PUSHING_TIME = "LAST_PUSHING_TIME";

    public static final String EQUIPMENT_ID = "EQUIPMENT_ID";//保存设备ID

    public static final String EQ_VOICE = "EQ_VOICE";//音量
    //推送过来为何种类型
    public static final int PUBLISH_TYPE_NOTICE = 3; //通知
    public static final int PUBLISH_TYPE_INFORMATION = 1; //资讯
    public static final int PUBLISH_TYPE_ADVERT = 2; // 广告

    //查询天气的api地址
    public static final String WEATHER_URL = "https://free-api.heweather.com/v5/weather?city=成都&key=acbead0d82a6415dadaa034a89a94837";

    //资讯内容的类型
    public static final int IS_IMAGE = 1;
    public static final int IS_MOVIE = 2;

    //是否属于插播
    public static final int IS_SPOTS = 1;

    public static int time = 0;

    public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().toString() +
            File.separator + "TvProject" + File.separator;

}
