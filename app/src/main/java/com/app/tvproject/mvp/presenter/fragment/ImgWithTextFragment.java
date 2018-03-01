package com.app.tvproject.mvp.presenter.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.presenter.activity.MainActivity;
import com.app.tvproject.mvp.view.ImgWithTextDelegate;
import com.app.tvproject.utils.BaiduVoiceUtil;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.NetUtil;
import com.baidu.tts.client.SpeechSynthesizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by www on 2017/11/22.
 */

public class ImgWithTextFragment extends FragmentPresenter<ImgWithTextDelegate> {
    private ContentBean contentBean;
    private SpeechSynthesizer speechSynthesizer;
    private MediaPlayer mediaPlayer;

    @Override
    public Class<ImgWithTextDelegate> getDelegateClass() {
        return ImgWithTextDelegate.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentBean = bundle.getParcelable("contentBean");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        //要转语音
        if (contentBean.getTransformsound() == 1) {
            if (contentBean.getSpots() == Constants.IS_SPOTS) {
                speechSynthesizer = BaiduVoiceUtil.initTTs();
            }
            else speechSynthesizer = activity.getSpeechSynthesizer();
            String text = contentBean.getContent().replaceAll(" ", "").replaceAll("\r|\n", "");
            String[] data = text.split("\\*");
            for (String aData : data) {
                speechSynthesizer.speak(aData);
            }
        }
        //如果不需要转语音，且有背景音乐，就去播放背景音乐
        else if (contentBean.getBgm() != null && !contentBean.getBgm().isEmpty()) {
            if (contentBean.getSpots() == Constants.IS_SPOTS)
                mediaPlayer = new MediaPlayer();
            else {
                mediaPlayer = activity.getMediaPlayer();
                LogUtil.d("idceshi","fragment里的id："+mediaPlayer.toString());
            }
            try {
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(contentBean.getBgm());
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    return false;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        setBannerImgLoader(contentBean);
    }


    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    private void setBannerImgLoader(ContentBean contentBean) {
        List<String> imgUrlList = new ArrayList<>();
        String[] imgUrl = contentBean.getResourcesUrl().replaceAll(" ", "").split(",");
        for (String anImgUrl : imgUrl) {
            if (!NetUtil.isConnectNoToast()) {
                if (!(anImgUrl.replaceAll(" ", "").substring(0, 4).equals("http")) && !anImgUrl.isEmpty()) {
                    imgUrlList.add(anImgUrl);
                }
            } else {
                if (!anImgUrl.isEmpty()) {
                    imgUrlList.add(anImgUrl);
                }
            }
        }
        viewDelegate.showImgBanner(imgUrlList);
    }

    @Override
    protected void onFragmentVisible() {

    }

    @Override
    protected void onFragmentHidden() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.w("xiaohui", "图文fragment被销毁");
        if (speechSynthesizer != null) {
            speechSynthesizer.stop();
        }
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {

        }
    }
}
