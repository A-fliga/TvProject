package com.app.tvproject.mvp.model.data;

import java.util.List;

/**
 * Created by www on 2017/11/16
 * 选择省市区配置信息的bean
 */

public class ChooseSettingsBean {
    /**
     * result : [{"id":22,"names":"yui社区"},{"id":24,"names":"mio社区"},{"id":26,"names":"mio社区"}]
     * msg : 操作成功！
     * code : 0
     */

    public String msg;
    public int code;
    public List<ResultBean> result;

    public static class ResultBean {
        /**
         * id : 22
         * names : yui社区
         */
        public int voice;
        public String dormantStartTime;
        public String 资讯;
        public int 广告;
        public String address;
        public int isDormant;
        public String dormantStopTime;
        public int 通知;
        public long id;
        public String equipmentnumber;
        public String names;
        public int villageId;
    }
}
