package com.app.tvproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.tvproject.application.MyApplication;
import com.app.tvproject.constants.Constants;

/**
 * Created by Administrator on 2017/10/13 0013.
 */

public class SharedPreferencesUtil {
    private static SharedPreferences sharedPreference = null;
    private static SharedPreferences.Editor editor = null;


    /**
     * 保存极光别名
     */
    public static void saveJpushAlias(String id) {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.putString(Constants.JI_GUANG_TAG, id);
        editor.apply();
    }

    /**
     * 取出别名
     *
     * @param
     * @return
     */
    public static String getJpushAlias() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference.getString(Constants.JI_GUANG_TAG, null);
    }

    /**
     * 保存设备音量
     */
    public static void saveVoice(int voice) {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.putInt(Constants.EQ_VOICE, voice);
        editor.apply();
    }


    /**
     * 取出音量
     *
     * @param
     * @return
     */
    public static int getVoice() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference.getInt(Constants.EQ_VOICE, 0);
    }


    /**
     * 保存正在播放的资讯ID
     */
    public static void saveInformationId(long informationId) {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.putLong(Constants.INFORMATION_ID, informationId);
        editor.apply();
    }

    /**
     * 保存正在播放的资讯数组下标
     */
    public static void saveInfoPosition(int position) {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.putInt(Constants.INFORMATION_POSITION, position);
        editor.apply();
    }

    /**
     * 保存正在播放的通知id
     */

    public static void saveNoticeId(long noticeId) {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.putLong(Constants.NOTICE_ID, noticeId);
        editor.apply();
    }


    /**
     * 保存正在播放的通知数组下标
     *
     * @param position
     */
    public static void saveNoticePosition(int position) {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.putInt(Constants.NOTICE_POSITION, position);
        editor.apply();
    }


    /**
     * 取出正在播放的通知id
     */

    public static long getNoticeId() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference.getLong(Constants.NOTICE_ID, 0);
    }

    /**
     * 取出正在播放的通知数组下标
     *
     * @param
     * @return
     */
    public static int getNoticePosition() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference.getInt(Constants.NOTICE_POSITION, 0);
    }

    /**
     * 取出正在播放的资讯数组下标
     *
     * @param
     * @return
     */
    public static int getInfoPosition() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference.getInt(Constants.INFORMATION_POSITION, 0);
    }

    /**
     * 取出正在播放的资讯ID
     *
     * @param
     * @return
     */
    public static long getInformationId() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference.getLong(Constants.INFORMATION_ID, 0);
    }


    /**
     * 清空除了设备以外的下标
     */
    public static void resetShared() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.remove(Constants.INFORMATION_ID);
        editor.remove(Constants.INFORMATION_POSITION);
        editor.remove(Constants.NOTICE_ID);
        editor.remove(Constants.NOTICE_POSITION);
        editor.apply();
    }

    /**
     * 保存设备id
     */
    public static void saveEqId(Context context, long eqId) {
        if (sharedPreference == null) {
            sharedPreference = context
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        editor = sharedPreference.edit();
        editor.putLong(Constants.EQUIPMENT_ID, eqId);
        editor.apply();
    }

    /**
     * 取出设备ID
     *
     * @param
     * @return
     */
    public static long getEqId() {
        if (sharedPreference == null) {
            sharedPreference = MyApplication.getContext()
                    .getSharedPreferences(Constants.TV_PROJECT_SHARED_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreference.getLong(Constants.EQUIPMENT_ID, -1);
    }

}
