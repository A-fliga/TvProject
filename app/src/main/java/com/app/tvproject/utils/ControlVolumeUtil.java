package com.app.tvproject.utils;

import android.content.Context;
import android.media.AudioManager;

import com.app.tvproject.application.MyApplication;
import com.app.tvproject.mvp.model.data.EqVoiceBean;
import com.app.tvproject.simplecache.ACache;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by www on 2017/12/1.
 */

public class ControlVolumeUtil {
    private static ACache aCache;

    /**
     * 控制系统音量
     */
    public static void setVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(STREAM_MUSIC, getVoice(), 0);
    }

    public static void saveVoice(String voice) {
        aCache = ACache.get(MyApplication.getAppContext());
        String[] volume = voice.replaceAll(" ", "").split(",");
        if (volume.length >= 4) {
            EqVoiceBean voiceBean = new EqVoiceBean();
            List<EqVoiceBean.VoiceBean> beanList = new ArrayList<>();
            for (int i = 0; i < volume.length; i++) {
                if (i == volume.length - 1) {
                    voiceBean.setPublicVoice(Integer.parseInt(volume[i]));
                } else if (i % 3 == 0) {
                    EqVoiceBean.VoiceBean bean = new EqVoiceBean.VoiceBean();
                    bean.setStartTime(volume[i]);
                    bean.setEndTime(volume[i + 1]);
                    bean.setVoice(Integer.parseInt(volume[i + 2]));
                    beanList.add(bean);
                }
            }
            voiceBean.setVoiceList(beanList);
            aCache.put("voiceBean", voiceBean);
        }
    }

    public static int getVoice() {
        aCache = ACache.get(MyApplication.getAppContext());
        EqVoiceBean voiceBean = (EqVoiceBean) aCache.getAsObject("voiceBean");
        List<EqVoiceBean.VoiceBean> beanList = voiceBean.getVoiceList();
        int voice = 0;
        for (int i = 0; i < beanList.size(); i++) {
            EqVoiceBean.VoiceBean bean = beanList.get(i);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");//年-月-日 时-分
            try {
                Date startTime = dateFormat.parse(bean.getStartTime());//开始时间
                Date endTime = dateFormat.parse(bean.getEndTime());//结束时间
                String time = InitDateUtil.initClock(null);
                Date nowTime = dateFormat.parse(time);
                //在这个时间范围内，就获取这个音量值
                if (nowTime.getTime() <= endTime.getTime() && nowTime.getTime() >= startTime.getTime()) {
                    voice = bean.getVoice();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (voice != 0) {
            return voice;
        } else
            return voiceBean.getPublicVoice();
    }

}
