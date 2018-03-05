package com.app.tvproject.mvp.model.data;


import java.io.Serializable;
import java.util.List;

/**
 * Created by www on 2018/3/2.
 */
public class EqVoiceBean implements Serializable {
    private static final long serialVersionUID = 1000000L;
    private List<VoiceBean> voiceList;
    private int publicVoice;

    public List<VoiceBean> getVoiceList() {
        return voiceList;
    }

    public void setVoiceList(List<VoiceBean> voiceList) {
        this.voiceList = voiceList;
    }

    public int getPublicVoice() {
        return publicVoice;
    }

    public void setPublicVoice(int publicVoice) {
        this.publicVoice = publicVoice;
    }

    public static class VoiceBean implements Serializable {
        private static final long serialVersionUID = 1000001L;
        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getVoice() {
            return voice;
        }

        public void setVoice(int voice) {
            this.voice = voice;
        }

        private String startTime;
        private String endTime;
        private int voice;
    }

}
