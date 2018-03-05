package com.app.tvproject.mvp.presenter.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.app.tvproject.R;
import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.PublicModel;
import com.app.tvproject.mvp.model.data.BaseEntity;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.EventBusData;
import com.app.tvproject.mvp.model.data.PublishListBean;
import com.app.tvproject.mvp.model.data.TestBean;
import com.app.tvproject.mvp.model.data.UpdateBean;
import com.app.tvproject.mvp.model.data.UpdateUseEqBean;
import com.app.tvproject.mvp.model.data.WeatherBean;
import com.app.tvproject.mvp.presenter.fragment.ImgWithTextFragment;
import com.app.tvproject.mvp.presenter.fragment.NullInfoFragment;
import com.app.tvproject.mvp.presenter.fragment.VideoFragment;
import com.app.tvproject.mvp.view.CustomerView.CustomerVideoView;
import com.app.tvproject.mvp.view.MainActivityDelegate;
import com.app.tvproject.myDao.DaoManager;
import com.app.tvproject.myDao.DaoUtil;
import com.app.tvproject.receiver.NetBroadCastReceiver;
import com.app.tvproject.utils.AppUtil;
import com.app.tvproject.utils.BaiduVoiceUtil;
import com.app.tvproject.utils.ControlVolumeUtil;
import com.app.tvproject.utils.DialogUtil;
import com.app.tvproject.utils.DownLoadFileManager;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.NetUtil;
import com.app.tvproject.utils.PackageUtils;
import com.app.tvproject.utils.SharedPreferencesUtil;
import com.app.tvproject.utils.ToastUtil;
import com.baidu.tts.client.SpeechSynthesizer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;

import static com.app.tvproject.myDao.DaoUtil.deleteAll;
import static com.app.tvproject.myDao.DaoUtil.deleteContentById;
import static com.app.tvproject.myDao.DaoUtil.insertOrReplaceContent;
import static com.app.tvproject.myDao.DaoUtil.insertOrReplaceList;
import static com.app.tvproject.myDao.DaoUtil.loadAllValidInformation;
import static com.app.tvproject.myDao.DaoUtil.loadAllValidNotice;
import static com.app.tvproject.myDao.DaoUtil.queryContentById;
import static com.app.tvproject.utils.PackageUtils.INSTALL_SUCCEEDED;

/**
 * Created by www on 2017/11/16.
 */

public class MainActivity extends ActivityPresenter<MainActivityDelegate> implements NetBroadCastReceiver.NetListener {
    //记录播放进度
    private int position = 0;
    //设备Id
    private long eqId = -1;

    //做定时任务用
    private Timer timer = new Timer();

    private TimerTask noticeTask, informationTask, interCutNoticeTask, interCutInfoTask;
    //百度语音的引擎
    private SpeechSynthesizer mSpeechSynthesizer;

    //音乐播放器
    private MediaPlayer mediaPlayer;
    private VideoFragment videoFragment;
    private CustomerVideoView videoView;

    private ImgWithTextFragment cutImgFragment;
    private VideoFragment cutVideoFragment;
    //记住正在插播的通知或资讯的Id，处理停播用
    private long interCutNoticeId, interCutInfoId;

    //记录插播时之前的页面播放了多久用的
    private long cutNoticeTime = 0, cutInfoTime = 0;

    private Boolean isCutting = false;
    private ContentBean cutBean2 = null;//二次插播的数据

    @Override
    public Class<MainActivityDelegate> getDelegateClass() {
        return MainActivityDelegate.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        DownLoadFileManager.getInstance().stopDownLoad(false);
        viewDelegate.hideMainRl(true);
//        hideUI();
//        try {
//            Runtime.getRuntime().exec("su");
            initAllData();
//        } catch (IOException e) {
//            ToastUtil.s("没有root权限");
//            e.printStackTrace();
//        }

        //把logCat写到本地
//        log2File();
    }

    private void hideUI() {
        Intent i = new Intent("com.android.systembar.disable");
        sendBroadcast(i);
    }

    private void log2File() {
        try {
            Process p = Runtime.getRuntime().exec("logcat -d");
            final InputStream is = p.getInputStream();
            new Thread() {
                @Override
                public void run() {
                    FileOutputStream os = null;
                    try {
                        File file = new File(Constants.DOWNLOAD_DIR + "logCat");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        os = new FileOutputStream(Constants.DOWNLOAD_DIR + "logCat/" + "logMsg.txt");
                        int len = 0;
                        byte[] buf = new byte[1024];
                        while (-1 != (len = is.read(buf))) {
                            os.write(buf, 0, len);
                            os.flush();
                        }
                    } catch (Exception e) {
                        Log.d("writelog", "read logcat process failed. message: " + e.getMessage());
                    } finally {
                        if (null != os) {
                            try {
                                os.close();
                                os = null;
                            } catch (IOException e) {
                                // Do nothing
                            }
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            Log.d("writelog", "open logcat process failed. message: " + e.getMessage());
        }
    }

    private void checkUpdate(Boolean deleteAll) {
        if (NetUtil.isConnectNoToast()) {
            PublicModel.getInstance().getUpdateInfo(new Subscriber<BaseEntity<UpdateBean>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    noUpdate(deleteAll);
                }

                @Override
                public void onNext(BaseEntity<UpdateBean> updateBeanBaseEntity) {
                    TestBean bean = DaoUtil.getTest();
                    LogUtil.d("dao",bean.getAge()+bean.getName()+bean.getTime()+bean.getMyStringtest333());

                    if (updateBeanBaseEntity.getResult() != null && Float.parseFloat(AppUtil.getVersionName()) < Float.parseFloat(updateBeanBaseEntity.getResult().versionNumber)) {
                        //开始下载更新并安装
                        LogUtil.d("qidong", "更新");
                        new Thread(() -> {
                            Looper.prepare();
                            startUpdate(updateBeanBaseEntity.getResult().resourceUrl, deleteAll);
                        }).start();
                    } else {
                        noUpdate(deleteAll);
                    }
                }
            });
        } else {
            noUpdate(deleteAll);
        }
    }

    private void initAllData() {
        //首次进入页面判断是否配置了设备和推送信息
        if (noSettings()) {
            runOnUiThread(() -> DialogUtil.showDialog(MainActivity.this, selectSettingsDialog));
        } else {
            initServiceData(false);
        }
    }

    private void startUpdate(String apkUrl, Boolean deleteAll) {
        ProgressDialog pd = DialogUtil.showProgressDialog(this);
        runOnUiThread(pd::show);
        if (DownLoadFileManager.getInstance().downLoadApk(pd, apkUrl) && DownLoadFileManager.getInstance().getApkPath() != null) {
            closeDialog(pd);
            if (PackageUtils.install(this, DownLoadFileManager.getInstance().getApkPath()) != INSTALL_SUCCEEDED) {
                noUpdate(deleteAll);
            }
        } else {
            closeDialog(pd);
            noUpdate(deleteAll);
        }
    }

    private void closeDialog(ProgressDialog pd) {
        runOnUiThread(() -> {
            pd.hide();
            pd.dismiss();
        });
    }

//    private int startInstallApk(String apkPath) {
////        boolean result = false;
////        DataOutputStream dataOutputStream = null;
////        BufferedReader errorStream = null;
////        try {
////            ToastUtil.l("正在执行安装");
////            // 申请su权限
////            Process process = Runtime.getRuntime().exec("su");
////            dataOutputStream = new DataOutputStream(process.getOutputStream());
////            // 执行pm install命令
////            String command = "pm install -r " + apkPath + "\n";
////            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
////            dataOutputStream.flush();
////            dataOutputStream.writeBytes("exit\n");
////            dataOutputStream.flush();
////            process.waitFor();
////            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
////            String msg = "";
////            String line;
////            // 读取命令的执行结果
////            while ((line = errorStream.readLine()) != null) {
////                msg += line;
////            }
////            ToastUtil.l(msg);
////            Log.d("TAG", "install msg is " + msg);
////            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
////            if (!msg.contains("Failure")) {
////                result = true;
////            }
////        } catch (Exception e) {
////            ToastUtil.s("安装失败" + e.getMessage());
////            Log.e("TAG", e.getMessage(), e);
////        } finally {
////            try {
////                if (dataOutputStream != null) {
////                    dataOutputStream.close();
////                }
////                if (errorStream != null) {
////                    errorStream.close();
////                }
////            } catch (IOException e) {
////                Log.e("TAG", e.getMessage(), e);
////            }
////        }
//        int result = ;
//        return result;
//    }

    private void initServiceData(Boolean deleteAll) {
        if (ControlVolumeUtil.getVoice() != 0)
            ControlVolumeUtil.setVolume(this);
        //初始化百度语音
        initBaiDuVoice();
        viewDelegate.hideMainRl(true);
//        TestBean bean = new TestBean();
//        bean.setId(1);
//        bean.setAge(10);
//        bean.setName("asd");
//        bean.setTime(6666);
//        bean.setMyStringtest333("我是好阿萨德号阿萨德号");
//        DaoUtil.insertPeopr(bean);
        checkUpdate(deleteAll);
    }

    private void noUpdate(Boolean deleteAll) {
        //显示出视图
        viewDelegate.hideMainRl(false);
        //每次进入app，要拉取一遍服务器的播放列表
        getPublishList(deleteAll, false);

        //注册eventBus
        EventBus.getDefault().register(this);

        //清空数据库和shared及所有缓存文件，测试用
        Button button = viewDelegate.get(R.id.clear);
        button.setOnClickListener(v -> deleteDataAndShared(true, true));

        // 更新设备的连接状态
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateServiceStatus(eqId, 1);
            }
        };
        timer.schedule(task, 0, 1800000);

        //更新一下时间和天气
        getTime();
        getNowWeather();
    }

    private void deleteDataAndShared(Boolean deleteFile, Boolean clearShared) {
        //清空保存的下标和数据库内容
        deleteAll();
        if (clearShared) {
            LogUtil.d("shuaxin", "清除shared");
            SharedPreferencesUtil.resetShared();
        }
        //是否要删掉原来的全部文件
        if (deleteFile)
            DownLoadFileManager.getInstance().deleteFilesByDirectory(DownLoadFileManager.getInstance().getDownloadDir());
    }


    private void initInfo() {
        //取出本地资讯
        List<ContentBean> informationList = loadAllValidInformation();
        if (informationList.size() != 0) {
            nextInformation(true);
        } else {
            setInfoNull(-1);
        }
    }

    private TextView info_tv;

    private Boolean noSettings() {
        String alias = SharedPreferencesUtil.getJpushAlias();
        eqId = SharedPreferencesUtil.getEqId();


        Button btn = (Button) findViewById(R.id.clear_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
            }
        });

        info_tv = (TextView) findViewById(R.id.info_tv);
        if (eqId != 0 && eqId != -1) {
            info_tv.setText("设备ID" + eqId);
        }

        return (alias == null || alias.isEmpty() || eqId == -1);
    }

    private void getPublishList(Boolean deleteAll, Boolean clearShared) {
        if (NetUtil.isConnectNoToast())
            getPublishDetailList(eqId, deleteAll, clearShared);
        else {
            // 没网络就只播放本地数据库的内容
            startLocalDataPlay();
        }
    }

    private void initNotice() {
        //取出通知
        List<ContentBean> notice = loadAllValidNotice();
        if (notice.size() != 0) {
            nextNotice(true);
        } else viewDelegate.setNoticeNull();
    }

    //开始播放时长倒计时并循环数组
    private synchronized void countDownNotice(long duration) {
        noticeTask = new TimerTask() {
            @Override
            public void run() {
                cutNoticeTime = 0;
                nextNotice(false);
            }
        };
        if (duration >= 0)
            timer.schedule(noticeTask, duration * 1000);
        else timer.schedule(noticeTask, 15000);
        cutNoticeTime = System.currentTimeMillis();
    }

    // 这里要注意正在播放的内容的下标的处理,noAddPosition：是否不加1，true为不加
    private synchronized void nextNotice(boolean noAddPosition) {
        List<ContentBean> noticeList = loadAllValidNotice();
//        LogUtil.w("ceshi", "开始下一条：noticeList.size()=" + noticeList.size() + " " + noAddPosition);
        if (noticeList.size() == 0) {
            viewDelegate.setNoticeNull();
        } else {
            //获取准确的下一个要播放的position
            int nextPosition = noAddPosition ? SharedPreferencesUtil.getNoticePosition() :
                    (SharedPreferencesUtil.getNoticePosition() + 1) % noticeList.size();
            if (nextPosition == -1)
                nextPosition = 0;
//            LogUtil.w("ceshi", "应该播的坐标：" + nextPosition);
            ContentBean contentBean = noticeList.get(nextPosition);
            if (contentBean != null) {
                contentBean.setSpots(0);
                viewDelegate.startMarquee(contentBean);
//                LogUtil.w("ceshi", "正在播放的ID：" + contentBean.getId() + contentBean.getHeadline());
                SharedPreferencesUtil.saveNoticeId(contentBean.getId());
//                LogUtil.w("ceshi", "开始进入倒计时");
                countDownNotice(contentBean.getDuration());
            }
            //避免当前bean为空但list还有内容的意外情况
            else if (loadAllValidNotice().size() != 0) {
                nextNotice(false);
            }
        }
    }


    private void updateServiceStatus(long eqId, int status) {
        PublicModel.getInstance().updateEqStatus(new Subscriber<UpdateUseEqBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(UpdateUseEqBean updateUseEqBean) {
                LogUtil.w("lianjie", updateUseEqBean.toString());
            }
        }, String.valueOf(eqId), String.valueOf(status));
    }

    private void startLocalDataPlay() {
        //注册listener
        NetBroadCastReceiver.setNetChangeListener(this);
        //取出资讯内容
        initInfo();
        //取出通知
        initNotice();
    }

    private DialogInterface.OnClickListener selectSettingsDialog = (dialog, which) -> {
        switch (which) {
            case -1:
                dialog.dismiss();
                Intent intent = new Intent(this, ChooseSettingsActivity.class);
                startMyActivityForResult(intent, Constants.CHOOSE_SETTINGS_REQUEST_CODE);
                break;
            case -2:
                finish();
                break;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CHOOSE_SETTINGS_REQUEST_CODE && resultCode == Constants.CHOOSE_SETTINGS_RESULT_CODE) {
            //第一次初始化要获取服务器端正在播放的内容集合
            eqId = data.getLongExtra("eqId", -1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    info_tv.setText("设备ID" + eqId);
                }
            });

            String voice = data.getStringExtra("voice");
            if (eqId != -1) {
                initServiceData(true);
                ControlVolumeUtil.saveVoice(voice);
                ControlVolumeUtil.setVolume(this);
                setEquipUsed(eqId, 1);
            }
        } else finish();
    }

    private void getPublishDetailList(long equipId, Boolean deleteAll, Boolean clearShared) {

        PublicModel.getInstance().getPublishList(new Subscriber<PublishListBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                DownLoadFileManager.getInstance().stopDownLoad(false);
                startLocalDataPlay();
            }

            @Override
            public void onNext(PublishListBean publishListBean) {
                DownLoadFileManager.getInstance().stopDownLoad(false);
                List<ContentBean> serverList = new ArrayList<>();
                serverList.addAll(publishListBean.result);
                //是否要清空数据
                if (deleteAll)
                    refreshData(serverList);
                else
                    compareWithServer(serverList, clearShared);
                NetBroadCastReceiver.setNetChangeListener(MainActivity.this);
            }
        }, String.valueOf(equipId), null);
    }


    private void refreshData(List<ContentBean> serverList) {
        deleteDataAndShared(true, true);
//        for (int i = 0; i < serverList.size(); i++) {
//            if (serverList.get(i).size() != 0) {
        insertOrReplaceList(serverList);
//            }
//        }
        startDownLoad();
        initInfo();
        initNotice();
    }


    /**
     * 先拿到pushList, 然后取出本地所有的资讯成list<资讯1> 把本地所有的缓存和share删了（除了设备号）。
     * 把pushlist保存到本地数据库，提取出pushlist里所有的资讯成list<资讯2>,把两个list的id遍历出来成list<id1>,list<id2>
     * 遍历list<id2>，
     * 如果list<id1>里面有，则在list<资讯1> indexOf出这个id对应的contentBean，比较updateTime,如果2大于1则去下载，else 检查
     * 1中的imgUrl对应的文件是否存在，不存在，走下载，存在，则替换掉2中的imgUrl
     * 如果list<id1>中没有，则根据id查询contentBean，去下载imgUrl
     * 之后调用init方法 搞定
     */
    private void compareWithServer(List<ContentBean> serverList, Boolean clearShared) {
        //之前本地的集合和集合Id
        List<ContentBean> beforeList = loadAllValidInformation();
        List<Long> beforeId = new ArrayList<>();
        for (int i = 0; i < beforeList.size(); i++) {
            beforeId.add(beforeList.get(i).getId());
        }
        //服务器得到的集合和Id
        List<ContentBean> allList = new ArrayList<>();
//        for (int i = 0; i < serverList.size(); i++) {
//            if (serverList.get(i).size() != 0) {
        allList.addAll(serverList);
//            }
//        }
        if (allList.size() == 0) {
            setInfoNull(-1);
        } else {

            List<ContentBean> afterList = new ArrayList<>();
            List<ContentBean> noticeList = new ArrayList<>();
            for (int i = 0; i < allList.size(); i++) {
                if (allList.get(i).getPublishTypeId() == Constants.PUBLISH_TYPE_INFORMATION || allList.get(i).getPublishTypeId() == Constants.PUBLISH_TYPE_ADVERT)
                    afterList.add(allList.get(i));
                if (allList.get(i).getPublishTypeId() == Constants.PUBLISH_TYPE_NOTICE) {
                    noticeList.add(allList.get(i));
                }
            }

            List<Long> afterId = new ArrayList<>();
            for (int i = 0; i < afterList.size(); i++) {
                afterId.add(afterList.get(i).getId());
            }

            //要找出相同的Id和对应下标
            List<Long> sameId = new ArrayList<>();
            List<Integer> sameIdIndex = new ArrayList<>();
            for (int j = 0; j < afterId.size(); j++) {
                for (int i = 0; i < beforeId.size(); i++) {
                    if (beforeId.get(i).equals(afterId.get(j))) {
                        LogUtil.w("测试刷新", "有相同的id，id值为：" + afterId.get(j) + "标题为:" + queryContentById(afterId.get(j)).getHeadline() + "在after的下标为:" + j);
                        sameId.add(afterId.get(j));
                        sameIdIndex.add(j);
                    }
                }
            }

            //有相同id的，要比较它们的sortTime，如果发现有编辑过，那就保留网址，没编辑过，替换网址成local地址
            for (int i = 0; i < sameId.size(); i++) {
                ContentBean beforeBean = queryContentById(sameId.get(i));
                String[] imgUrl = beforeBean.getResourcesUrl().replaceAll(" ", "").split(",");
                ContentBean afterBean = afterList.get(sameIdIndex.get(i));
                LogUtil.w("测试刷新", "相同Id它们的sort为：beforeBean：" + beforeBean.getSort() + " afterBean:" + afterBean.getSort());
                if (beforeBean.getSort() >= afterBean.getSort()) {
                    if (isFileAllExists(imgUrl)) {
                        LogUtil.w("测试刷新", "更换网址为本地");
                        afterBean.setResourcesUrl(beforeBean.getResourcesUrl());
                    }
                }
                //编辑过，要把原来的多余数据删了
                else {
                    DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getResourcesUrl());
                    if (hasBgm(beforeBean))
                        DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getBgm());
                }
            }
            beforeId.removeAll(sameId);
            for (int i = 0; i < beforeId.size(); i++) {
                ContentBean beforeBean = queryContentById(beforeId.get(i));
                DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getResourcesUrl());
                if (hasBgm(beforeBean))
                    DownLoadFileManager.getInstance().addDeleteTask(beforeBean.getBgm());
            }
            deleteDataAndShared(false, clearShared);
            insertOrReplaceList(afterList);
            insertOrReplaceList(noticeList);
            startDownLoad();
            initInfo();
            initNotice();
            LogUtil.d("qidong", "对比完成");
        }
    }

    //对比文件是否都存在
    private Boolean isFileAllExists(String[] imgUrl) {
        Boolean allExists = true;
        for (String anImgUrl : imgUrl) {
            File file = new File(anImgUrl);
            if (!file.exists())
                allExists = false;
        }
        return allExists;
    }

    private void startDownLoad() {
        List<ContentBean> infoList = loadAllValidInformation();
        for (int i = 0; i < infoList.size(); i++) {
            ContentBean contentBean = infoList.get(i);
            String[] urlList = contentBean.getResourcesUrl().replaceAll(" ", "").split(",");
            for (int j = 0; j < urlList.length; j++) {
                DownLoadFileManager.getInstance().addDownloadTask(j, contentBean);
            }
        }
    }


    //定时刷新时间、日期
    private void getTime() {
        viewDelegate.initDate();
        viewDelegate.initLunar();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (viewDelegate != null)
                        viewDelegate.initClock();
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }


    //定时更新天气 2小时更新一次
    private void getNowWeather() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    PublicModel.getInstance().getWeather(new Subscriber<WeatherBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.w("weather", e.toString());
                        }

                        @Override
                        public void onNext(WeatherBean weatherBean) {
                            viewDelegate.initWeather(weatherBean.result);
                        }
                    });
                });
            }
        };

        timer.schedule(task, 0, 7200000);
    }

    //初始化百度语音的东西
    private void initBaiDuVoice() {
        BaiduVoiceUtil.initialEnv();
        mSpeechSynthesizer = BaiduVoiceUtil.initTTs();
    }


    /**
     * 接受更新ui的事件
     *
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessageThread(EventBusData eventBusData) {
//        ToastUtil.l("收到消息了,收到消息了,收到消息了,收到消息了,收到消息了" +
//                "收到消息了,收到消息了,收到消息了");
        String action = eventBusData.getAction();
        long contentId = eventBusData.getContent_id();
        switch (action) {
            //处理内容信息
            case "pushNotice":
                info_tv.setText("设备Id:" + eqId + " 收到的内容Id:" + contentId);
                getPublishContent(contentId);
                break;
            //处理设备信息
            case "newEquipmentNotice":
                //控制系统音量0-15
//                ControlVolumeUtil.setControlVolume(this, eventBusData.getVoice(), false);
                break;
            //处理清空缓存信息
            case "emptyNotice":
                //清空内容缓存
                deleteDataAndShared(true, true);
                break;
            case "updateNotice":
                Log.d("sss", "updateNotice");
                getPublishContent(contentId);
                break;

            //是否要刷新列表
            case "changeSort":
                DownLoadFileManager.getInstance().stopDownLoad(true);
                stopVoiceAndVideo();
                if (noticeTask != null)
                    noticeTask.cancel();
                if (informationTask != null)
                    informationTask.cancel();
                if (interCutInfoTask != null)
                    interCutInfoTask.cancel();
                if (interCutNoticeTask != null)
                    interCutNoticeTask.cancel();
                getPublishList(false, true);
                break;
            //停播
            case "stopPlay":
                ContentBean contentBean = queryContentById(contentId);
                if (contentBean != null) {
                    switch (contentBean.getPublishTypeId()) {
                        //停播的是通知
                        case Constants.PUBLISH_TYPE_NOTICE:
                            stopNotice(contentId);
                            break;
                        //停播的是资讯
                        case Constants.PUBLISH_TYPE_INFORMATION:
                        case Constants.PUBLISH_TYPE_ADVERT:
                            DownLoadFileManager.getInstance().setStopId(contentId);
                            stopInformation(contentId, contentBean);
                            DownLoadFileManager.getInstance().setStopId(-1);
                            break;
                    }
                }
        }

    }


    private void stopInformation(long contentId, ContentBean stopBean) {
        LogUtil.w("ceshi", "停播： informationId" + SharedPreferencesUtil.getInformationId() + "   stopId" + contentId);
        //如果停播的资讯正在播放，并且在插播的内容不在播放，要取消原来的跳转任务并且立即切换下一个内容,这里记得一定要先切换再删数据库，要不查询下标会不对
        if (SharedPreferencesUtil.getInformationId() == contentId) {
            if (informationTask != null)
                informationTask.cancel();
            nextInformation(false);
        }
        if (contentId == interCutInfoId) {
            interCutInfoId = -1;
            ContentBean beforeInfoBean = queryContentById(SharedPreferencesUtil.getInformationId());
            showOrHideContent(queryContentById(contentId).getImgormo(), beforeInfoBean, stopBean);
            if (cutInfoTime != 0 && loadAllValidInformation().size() != 1) {
                countDownInformation((beforeInfoBean.getDuration() * 1000 - cutInfoTime + 1000) / 1000);
            }
            if (interCutInfoTask != null)
                interCutInfoTask.cancel();
        }
        ContentBean contentBean = queryContentById(contentId);
        deleteContentById(contentId);
        if (loadAllValidInformation().size() == 0) {
            setInfoNull(contentBean.getImgormo());
        }
    }

    private void setInfoNull(int isImg) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        NullInfoFragment nullInfoFragment = new NullInfoFragment();
        if (isImg == Constants.IS_IMAGE)
            transaction.replace(R.id.img_frameLayout, nullInfoFragment).commit();
        if (isImg == Constants.IS_MOVIE)
            transaction.replace(R.id.videoFrameLayout, nullInfoFragment).commit();
        if (isImg == -1) {
            viewDelegate.setImgFrameVisibility(false);
            viewDelegate.setVideoFrameVisibility(false);
        }
        viewDelegate.setTitle("");
        viewDelegate.setTagContent("");
        stopVoiceAndVideo();
    }

    private void stopNotice(long contentId) {
//        LogUtil.w("ceshi", "停播： noticeId" + SharedPreferencesUtil.getNoticeId() + "   stopId" + contentId);
        //如果停播的通知正在播放，并且在插播的内容不在播放，要取消原来的跳转任务并且立即切换下一个内容,这里一定要先切换再删数据库，如果先删，next方法里查询新的position会错误
        if (SharedPreferencesUtil.getNoticeId() == contentId) {
            if (noticeTask != null)
                noticeTask.cancel();
            nextNotice(false);
        }
        //如果停播的是插播的内容，要判断插播的内容是否在播放，是的话马上替换成原来的内容
        if (contentId == interCutNoticeId) {
            interCutNoticeId = -1;
            ContentBean beforeNoticeBean = queryContentById(SharedPreferencesUtil.getNoticeId());
            viewDelegate.startMarquee(beforeNoticeBean);
            if (cutNoticeTime != 0 && loadAllValidNotice().size() != 1) {
                countDownNotice((beforeNoticeBean.getDuration() * 1000 - cutNoticeTime + 1500) / 1000);
            }
            if (interCutNoticeTask != null)
                interCutNoticeTask.cancel();
        }
        deleteContentById(contentId);
        if (loadAllValidNotice().size() == 0)
            viewDelegate.setNoticeNull();
    }


    /**
     * 通知设备已被使用
     */
    private void setEquipUsed(long id, int used) {
        PublicModel.getInstance().setEquipmentUsed(new Subscriber<UpdateUseEqBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(UpdateUseEqBean baseEntity) {
                ToastUtil.s("已选中当前设备信息");
            }

        }, String.valueOf(id), String.valueOf(used));
    }


    private void getPublishContent(long contentId) {
        PublicModel.getInstance().getPublishContent(new Subscriber<BaseEntity<ContentBean>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.w(e.toString());
            }

            @Override
            public void onNext(BaseEntity<ContentBean> contentBeanBaseEntity) {
                startShowContent(contentBeanBaseEntity.getResult());
            }
        }, String.valueOf(contentId), String.valueOf(eqId));
    }


    /**
     * 开始播放内容
     */
    private void startShowContent(ContentBean contentBean) {
        if (System.currentTimeMillis() < contentBean.getEndtime()) {
            //获取播放的指定时间
            String time = contentBean.getPlayTime();
            //当定时的时候播放完成后才加入播放列表
            if (time != null && !time.isEmpty()) {
                // TODO 这里要加入定时播放的任务
//                getAlarmTime( contentBean);
            } else {
                //不需要定时
                notAtTime(contentBean);
            }
        }
    }


    private void notAtTime(ContentBean contentBean) {

        //获取推送的类型  1资讯 2广告 3通知
        int publishType = contentBean.getPublishTypeId();
        //推送过来为通知类
        if (publishType == Constants.PUBLISH_TYPE_NOTICE) {
            if (contentBean.getSpots() == Constants.IS_SPOTS && loadAllValidNotice().size() > 0) {
                //开始插播通知
                startInterCutNotice(contentBean);
            } else
                isNotice(contentBean);
        }
        //为资讯类
        if (publishType == Constants.PUBLISH_TYPE_INFORMATION || publishType == Constants.PUBLISH_TYPE_ADVERT) {
            if (contentBean.getSpots() == Constants.IS_SPOTS && loadAllValidInformation().size() > 0) {
                //开始插播资讯
                if (!isCutting) {
                    startInterCutInfo(contentBean);
                } else {
                    ToastUtil.l("请等待当前插播内容播放完毕");
                    cutBean2 = contentBean;
                }
            } else if (contentBean.getResourcesUrl() != null && !contentBean.getResourcesUrl().isEmpty()) {
                isInformation(contentBean);
            }
        }
    }

    private Boolean hasBgm(ContentBean contentBean) {
        return contentBean.getTransformsound() != 1 && contentBean.getBgm() != null && !contentBean.getBgm().isEmpty();
    }

    private synchronized void startInterCutInfo(ContentBean contentBean) {
        ControlVolumeUtil.setVolume(this);
        if (informationTask != null)
            informationTask.cancel();
        ContentBean beforeBean = queryContentById(SharedPreferencesUtil.getInformationId());
        //插播的类型
        int cutType = contentBean.getImgormo();

        //判断之前在播放的是什么内容
        if (beforeBean != null) {
            switch (beforeBean.getImgormo()) {
                //原本播放的是图文
                case Constants.IS_IMAGE:
                    if (mSpeechSynthesizer != null)
                        mSpeechSynthesizer.pause();
                    if (hasBgm(beforeBean) && mediaPlayer != null) {
                        LogUtil.d("idceshi", "activity里的id：" + mediaPlayer.toString());
                        mediaPlayer.pause();
                    }
                    viewDelegate.setImgFrameVisibility(false);
                    break;
                //原本播放的是视频
                case Constants.IS_MOVIE:
                    if (cutType == Constants.IS_IMAGE)
                        viewDelegate.setVideoFrameVisibility(false);
                    if (videoFragment != null) {
                        videoView = videoFragment.getVideoView();
                        LogUtil.w("idceshi", "main里原来的view的id" + videoView.getCurrentPosition() + videoView.toString());
                        if (videoView != null) {
                            videoView.pause();
                            position = videoView.getCurrentPosition();
                            LogUtil.w("seek", "暂停播放：" + position);
                        }
                    }
                    break;
            }
        }
        cutInfoTime = System.currentTimeMillis() - cutInfoTime;
        insertOrReplaceContent(contentBean);
        interCutInfoId = contentBean.getId();

        //判断插播的是什么内容
        switch (cutType) {
            //插播的是图文
            case Constants.IS_IMAGE:
                viewDelegate.setTagContent(contentBean.getTagName());
                viewDelegate.setCutImgVisibility(true);
                setLogoAndTitle(true, contentBean.getHeadline());
                ImgWithTextFragment imgFragment = new ImgWithTextFragment();
                cutImgFragment = imgFragment;
                beginInterCutTransaction(true, imgFragment, contentBean);
                if (hasBgm(contentBean)) {
                    DownLoadFileManager.getInstance().addDownLoadBgm(contentBean);
                }
                break;
            //插播的是视频
            case Constants.IS_MOVIE:
                viewDelegate.setTagContent(null);
                if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_IMAGE) {
                    setLogoAndTitle(false, contentBean.getHeadline());
                    cutVideoFragment = new VideoFragment();
                    viewDelegate.setVisibility(false);
                    beginInterCutTransaction(false, cutVideoFragment, contentBean);
                }
                if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_MOVIE) {
                    cutVideoFragment = videoFragment;
                    cutVideoFragment.setMContentBean(contentBean);
                    cutVideoFragment.setIsSpots(true);
                    CustomerVideoView cutVideoView = cutVideoFragment.getCut_videoView();
                    LogUtil.w("idceshi", "要开始替换插播的id" + videoView.getCurrentPosition() + " " + cutVideoView.toString());
                    cutVideoFragment.initVideoView(true, cutVideoView, contentBean.getResourcesUrl());
                }
                break;
        }

        interCutInfoTask = new TimerTask() {
            @Override
            public void run() {
                showOrHideContent(cutType, beforeBean, contentBean);
                if (cutInfoTime != 0 && loadAllValidInformation().size() != 1) {
                    countDownInformation((beforeBean.getDuration() * 1000 - cutInfoTime + 1000) / 1000);
                }
                if (beforeBean.getImgormo() == Constants.IS_IMAGE)
                    viewDelegate.setTagContent(beforeBean.getTagName());
                else viewDelegate.setTagContent(null);
                //插播结束后置成-1
                interCutInfoId = -1;
                //如果插播完只有它，那就当正常数据处理
                if (loadAllValidInformation().size() == 1) {
                    SharedPreferencesUtil.saveInformationId(contentBean.getId());
                    nextInformation(false);
                }
            }
        };
        timer.schedule(interCutInfoTask, contentBean.getDuration() * 1000);
        String[] imgUrl = contentBean.getResourcesUrl().replaceAll(" ", "").split(",");
        for (int i = 0; i < imgUrl.length; i++) {
            DownLoadFileManager.getInstance().addDownloadTask(i, contentBean);
        }
    }

    private void showOrHideContent(int cutType, ContentBean beforeBean, ContentBean cutBean) {
        //插播的是图片，完后要把插播的视图隐藏
        if (cutType == Constants.IS_IMAGE) {
            viewDelegate.setCutImgVisibility(false);
            if (mSpeechSynthesizer != null)
                mSpeechSynthesizer.stop();
            if (hasBgm(cutBean)) {
                MediaPlayer mediaPlayer = cutImgFragment.getMediaPlayer();
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        }
        //插播的是视频，完后要把插播的视图隐藏
        if (cutType == Constants.IS_MOVIE) {
            if (cutVideoFragment != null) {
                CustomerVideoView cutVideo = cutVideoFragment.getVideoView();
                if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_MOVIE) {
                    //一定要恢复标志位
                    cutVideoFragment.setIsSpots(false);
                }
                if (cutVideo != null) {
                    cutVideo.pause();
                    cutVideo.stopPlayback();
                    runOnUiThread(() -> cutVideo.setVisibility(View.GONE));
                }
            }
        }
        if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_IMAGE) {
            setLogoAndTitle(true, beforeBean.getHeadline());
            viewDelegate.setImgFrameVisibility(true);
            if (mSpeechSynthesizer != null) {
                if (beforeBean.getTransformsound() == 1) {
                    String text = beforeBean.getContent().replaceAll(" ", "").replaceAll("\r|\n", "");
                    String[] data = text.split("\\*");
                    for (String aData : data) {
                        mSpeechSynthesizer.speak(aData);
                    }
                }
            }
            if (hasBgm(beforeBean) && mediaPlayer != null) {
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.start();
            }
        }
        if (beforeBean != null && beforeBean.getImgormo() == Constants.IS_MOVIE) {
            setLogoAndTitle(false, beforeBean.getHeadline());
            viewDelegate.setVideoFrameVisibility(true);
            if (videoView != null) {
                //如果插播的是视频，而且之前播的也是视频，要把之前被隐藏的view显示出来
                runOnUiThread(() -> videoView.setVisibility(View.VISIBLE));
                LogUtil.w("idceshi", "插播完后继续播放的id" + videoView.getCurrentPosition() + videoView.toString());
                if (position != 0) {
                    videoView.seekTo(position);
                    LogUtil.w("seek", "恢复播放：" + position);
                }
                videoView.start();
                position = 0;
            }
        }
        isCutting = false;
        EventBus.getDefault().post("CuttingFinish");
    }

    /**
     * 接受更新ui的事件
     *
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CuttingFinish(String status) {
        if (status.equals("CuttingFinish") && cutBean2 != null) {
            startInterCutInfo(cutBean2);
            cutBean2 = null;
        }
    }

    private void beginInterCutTransaction(Boolean isImg, Fragment fragment, ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("contentBean", contentBean);
        if (!isImg)
            bundle.putBoolean("Spots", true);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.setArguments(bundle);
        if (isImg) {
            transaction.replace(R.id.img_interCut_frameLayout, fragment).commitAllowingStateLoss();
        } else {
            transaction.replace(R.id.videoFrameLayout, fragment).commitAllowingStateLoss();
        }
    }


    private void startInterCutNotice(ContentBean contentBean) {
        //插播步骤：取消之前的定时任务，马上替换当前内容，完成之后再设置成之前的内容
        if (noticeTask != null)
            noticeTask.cancel();
        //
        cutNoticeTime = System.currentTimeMillis() - cutNoticeTime;
//        LogUtil.w("shijian", "插播过来距离上次的时间：" + cutNoticeTime);
        viewDelegate.startMarquee(contentBean);
        insertOrReplaceContent(contentBean);
        interCutNoticeId = contentBean.getId();
        interCutNoticeTask = new TimerTask() {
            @Override
            public void run() {
                ContentBean beforeNoticeBean = queryContentById(SharedPreferencesUtil.getNoticeId());
                viewDelegate.startMarquee(beforeNoticeBean);
                if (cutNoticeTime != 0 && loadAllValidNotice().size() != 1) {
//                    LogUtil.w("shijian", "回复上次的时间：" + (contentBean.getDuration() * 1000 - cutNoticeTime + 1500) / 1000);
                    countDownNotice((beforeNoticeBean.getDuration() * 1000 - cutNoticeTime + 1500) / 1000);
                }
                //插播结束后置成-1
                interCutNoticeId = -1;
                //如果插播完只有它，那就当正常数据处理
                if (loadAllValidNotice().size() == 1) {
                    SharedPreferencesUtil.saveNoticeId(contentBean.getId());
                    nextNotice(false);
                }
            }
        };
        timer.schedule(interCutNoticeTask, contentBean.getDuration() * 1000);
    }


    //为资讯类的操作
    private void isInformation(ContentBean contentBean) {
        LogUtil.d("load", loadAllValidInformation().size() + "");
        insertOrReplaceContent(contentBean);
        LogUtil.w("ceshi", "这个新数据插在了队伍的" + loadAllValidInformation().indexOf(contentBean) + "处");
        LogUtil.w("ceshi", loadAllValidInformation().size() + "  " + SharedPreferencesUtil.getInformationId());
        //如果本来没有存过资讯 或者 收到的资讯ID和正在播放的ID相同（替换操作），则马上更新界面
        LogUtil.d("load", loadAllValidInformation().size() + "");
        for (int i = 0; i < loadAllValidInformation().size(); i++) {
            LogUtil.d("load" + loadAllValidInformation().get(i).toString());
        }
        if (SharedPreferencesUtil.getInformationId() == contentBean.getId()) {
            if (informationTask != null) {
                informationTask.cancel();
            }
            SharedPreferencesUtil.saveInformationId(contentBean.getId());
            nextInformation(true);

        } else if (loadAllValidInformation().size() == 1) {
            SharedPreferencesUtil.saveInformationId(contentBean.getId());
            nextInformation(true);
        }

        String[] imgUrl = contentBean.getResourcesUrl().replaceAll(" ", "").split(",");
        for (int i = 0; i < imgUrl.length; i++) {
            DownLoadFileManager.getInstance().addDownloadTask(i, contentBean);
        }
        //下载背景音乐
        if (hasBgm(contentBean)) {
            DownLoadFileManager.getInstance().addDownLoadBgm(contentBean);
        }
    }


    private void isNotice(ContentBean contentBean) {
        //如果本来没有存过通知 或者 收到的通知ID和正在播放的ID相同（替换操作），则马上更新界面
        insertOrReplaceContent(contentBean);
//        LogUtil.w("ceshi", loadAllValidNotice().size() + "  " + SharedPreferencesUtil.getNoticeId());
        if (SharedPreferencesUtil.getNoticeId() == contentBean.getId()) {
            if (noticeTask != null) {
                noticeTask.cancel();
            }
            SharedPreferencesUtil.saveNoticeId(contentBean.getId());
            nextNotice(true);

        } else if (loadAllValidNotice().size() == 1) {
            SharedPreferencesUtil.saveNoticeId(contentBean.getId());
            nextNotice(true);
        }
    }


    //下一条资讯
    public synchronized void nextInformation(boolean noAddPosition) {
        if (interCutInfoTask != null)
            interCutInfoTask.cancel();
        ControlVolumeUtil.setVolume(this);
        List<ContentBean> informationList = loadAllValidInformation();
        LogUtil.w("ceshi", "开始下一条：informationList.size()=" + informationList.size() + " " + noAddPosition);
        if (informationList.size() == 0) {
            setInfoNull(-1);
        } else {
            int nextPosition = noAddPosition ? SharedPreferencesUtil.getInfoPosition() :
                    (SharedPreferencesUtil.getInfoPosition() + 1) % informationList.size();
            if (nextPosition == -1)
                nextPosition = 0;
            LogUtil.w("ceshi", "应该播的资讯坐标：" + nextPosition);
            ContentBean contentBean = informationList.get(nextPosition);
            LogUtil.w("download", "看看之前的数据" + contentBean.getResourcesUrl());
            if (contentBean != null) {
                showInfoContent(contentBean);
//                String[] url = contentBean.getResourcesUrl().split(",");
//                boolean hasHttp = false;
//                boolean hasLocal = false;
//                List<Integer> httpIndex = new ArrayList<>();
//                List<Integer> localIndex = new ArrayList<>();
//                for (int i = 0; i < url.length; i++) {
//                    if (url[i].replaceAll(" ", "").substring(0, 4).equals("http")) {
//                        hasHttp = true;
//                        httpIndex.add(i);
//                        LogUtil.w("download", "有未下载的：第" + i + "个,连接：" + url[i]);
//                    } else {
//                        hasLocal = true;
//                        localIndex.add(i);
//                    }
//                }
//                //如果imgUrl还有http的 要判断一下网络情况，然后根据具体情况处理
//                if (hasHttp) {
//                    if (NetUtil.isConnectNoToast()) {
//                        //有网，有未下载的，去下载
//                        for (int i = 0; i < httpIndex.size(); i++) {
//                            LogUtil.w("download", "有网 ，准备去下载：" + url[httpIndex.get(i)]);
////                            DownLoadFileManager.getInstance().addDownloadTask(httpIndex.get(i), contentBean);
//                        }
//                        showInfoContent(contentBean);
//                    } else {
//                        if (hasLocal) {
//                            LogUtil.w("download", "断网后，有缓存过，看看现在的数据" + queryContentById(contentBean.getId()).getResourcesUrl());
//                            showInfoContent(contentBean);
//                        } else {
//                            //没网并且没有已缓存过的，直接下一个
//                            LogUtil.w("download", "无网 ，也没有缓存过的");
//                            SharedPreferencesUtil.saveInformationId(contentBean.getId());
//                            nextInformation(false);
//                        }
//                    }
//                } else {
//                    //如果都已缓存本地，则直接播放
//                    LogUtil.w("download", "都已缓存本地");
//                    showInfoContent(contentBean);
//                }
            } else if (loadAllValidInformation().size() != 0) {
                nextInformation(false);
            }
        }

    }

    private void showInfoContent(ContentBean contentBean) {
        countDownInformation(contentBean.getDuration());
        showInformation(contentBean);
        LogUtil.w("ceshi", "正在播放的ID：" + contentBean.getId() + contentBean.getHeadline() + ",,占队伍的" + loadAllValidInformation().indexOf(contentBean));
        SharedPreferencesUtil.saveInformationId(contentBean.getId());
        LogUtil.w("ceshi", "开始进入倒计时");
    }

    private void showInformation(ContentBean contentBean) {
        if (contentBean != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    info_tv.setText("设备Id:" + eqId + " 正在播的Id:" + contentBean.getId());
                }
            });
            contentBean.setSpots(0);
            if (contentBean.getImgormo() == Constants.IS_IMAGE)
                viewDelegate.setTagContent(contentBean.getTagName());
            else viewDelegate.setTagContent(null);
            switch (contentBean.getImgormo()) {
                //是纯图或者图文内容
                case Constants.IS_IMAGE:
                    setLogoAndTitle(true, contentBean.getHeadline());
                    ImgWithTextFragment imgFragment = new ImgWithTextFragment();
                    beginTransaction(true, imgFragment, contentBean);
                    break;
                //是视频
                case Constants.IS_MOVIE:
                    //要隐藏标题
                    setLogoAndTitle(false, contentBean.getHeadline());
                    videoFragment = new VideoFragment();
                    beginTransaction(false, videoFragment, contentBean);
                    break;
            }
        }
    }

    public TimerTask getInformationTask() {
        return informationTask == null ? null : informationTask;
    }

    public TimerTask getInterCutInfoTask() {
        return interCutInfoTask == null ? null : interCutInfoTask;
    }

    private void setLogoAndTitle(Boolean isImg, String content) {
        viewDelegate.setTitleType(isImg);
        viewDelegate.setLogo(isImg);
        if (isImg) {
            viewDelegate.setTitle(content);
        } else viewDelegate.setTitle("");
    }

    private void beginTransaction(Boolean isImg, Fragment fragment, ContentBean contentBean) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("contentBean", contentBean);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment.setArguments(bundle);
        viewDelegate.setVisibility(isImg);
        //切换页面时要把之前的停止
        stopVoiceAndVideo();
        if (isImg) {
            transaction.replace(R.id.img_frameLayout, fragment).commitAllowingStateLoss();
        } else {
            transaction.replace(R.id.videoFrameLayout, fragment).commitAllowingStateLoss();
        }
    }

    private void stopVoiceAndVideo() {
        if (videoFragment != null) {
            VideoView videoView = videoFragment.getVideoView();
            if (videoView != null && videoView.isPlaying()) {
                videoView.pause();
                videoView.stopPlayback();
            }
        }
        if (getSpeechSynthesizer() != null) {
            mSpeechSynthesizer.stop();
        }
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {

        }
    }


    private synchronized void countDownInformation(long duration) {
        LogUtil.w("ceshi", "资讯倒计时的时间：" + duration);
        informationTask = new TimerTask() {
            @Override
            public void run() {
                cutInfoTime = 0;
                nextInformation(false);
            }
        };
        if (duration >= 0)
            timer.schedule(informationTask, duration * 1000);
        if (duration < 0)
            timer.schedule(informationTask, 15000);
        cutInfoTime = System.currentTimeMillis();
    }

    public SpeechSynthesizer getSpeechSynthesizer() {
        return mSpeechSynthesizer == null ? null : mSpeechSynthesizer;
    }

    public MediaPlayer getMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();//销毁百度语音单例对象
        }
        if (eqId != -1)
            updateServiceStatus(eqId, 0);

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {

        }
        //退出app停止下载
        DownLoadFileManager.getInstance().stopDownLoad(true);
        //退出应用后解绑eventBus
        EventBus.getDefault().unregister(this);
        //关闭数据库
        DaoManager.getInstance().closeConnection();
    }

    @Override
    public void netChange(Boolean isConnect) {
        if (isConnect) {
            stopVoiceAndVideo();
            if (noticeTask != null)
                noticeTask.cancel();
            if (informationTask != null)
                informationTask.cancel();
            if (interCutInfoTask != null)
                interCutInfoTask.cancel();
            if (interCutNoticeTask != null)
                interCutNoticeTask.cancel();
            getPublishList(false, false);
        }
    }
}
