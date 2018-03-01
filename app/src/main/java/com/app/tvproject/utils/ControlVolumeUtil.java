package com.app.tvproject.utils;

import android.content.Context;
import android.media.AudioManager;

import static android.media.AudioManager.STREAM_MUSIC;

/**
 * Created by www on 2017/12/1.
 */

public class ControlVolumeUtil {
    /**
     * 控制系统音量
     */
    public static void setControlVolume(Context context, int volume, Boolean isEq) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(STREAM_MUSIC, volume, 0);
        if (isEq) {
            SharedPreferencesUtil.saveVoice(volume);
        }
    }

}
