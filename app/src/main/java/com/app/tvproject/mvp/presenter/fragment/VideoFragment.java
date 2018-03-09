package com.app.tvproject.mvp.presenter.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.app.tvproject.R;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.presenter.activity.MainActivity;
import com.app.tvproject.mvp.view.CustomerView.CustomerVideoView;
import com.app.tvproject.mvp.view.VideoFragmentDelegate;
import com.app.tvproject.utils.LogUtil;

/**
 * Created by www on 2017/11/22.
 * 显示器的需求是一直在前台播放  所以不需要考虑home键再续播的问题
 */

public class VideoFragment extends FragmentPresenter<VideoFragmentDelegate> {
    private CustomerVideoView videoView, cut_videoView;
    private ContentBean contentBean;
    private Boolean isSpots = false;

    @Override
    public Class<VideoFragmentDelegate> getDelegateClass() {
        return VideoFragmentDelegate.class;
    }

    @Override
    protected void onFragmentVisible() {

    }

    public CustomerVideoView getVideoView() {
        if (!isSpots) {
            return videoView;

        } else {
            return cut_videoView;
        }
    }

    //在特定的插播和之前都在播视频的情况下用
    public CustomerVideoView getCut_videoView() {
        return cut_videoView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentBean = bundle.getParcelable("contentBean");
            isSpots = bundle.getBoolean("Spots", false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        videoView = viewDelegate.get(R.id.videoView);
        cut_videoView = viewDelegate.get(R.id.videoView_interCut);
        if (contentBean != null) {
            if (isSpots)
                initVideoView(true, cut_videoView, contentBean.getResourcesUrl());
            else initVideoView(false, videoView, contentBean.getResourcesUrl());
        }
    }

    public void initVideoView(Boolean isSpots, CustomerVideoView mVideoView, String path) {
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.setOnPreparedListener(preparedListener);
        mVideoView.setOnCompletionListener(completionListener);
        mVideoView.setOnErrorListener(errorListener);

        if (isSpots) {
            if (cut_videoView.getVisibility() == View.GONE)
                cut_videoView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
        } else {
            if (videoView.getVisibility() == View.GONE)
                videoView.setVisibility(View.VISIBLE);
            cut_videoView.setVisibility(View.GONE);
        }
        mVideoView.start();
    }


    //在特定的插播和之前都在播视频的情况下用
    public void setMContentBean(ContentBean contentBean) {
        this.contentBean = contentBean;
    }

    //在特定的插播和之前都在播视频的情况下用
    public void setIsSpots(Boolean isSpots) {
        this.isSpots = isSpots;
    }

    private MediaPlayer.OnPreparedListener preparedListener = mp -> {
    };

    private MediaPlayer.OnCompletionListener completionListener = mp -> {

    };


    private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            MainActivity activity = (MainActivity) getActivity();
            if (!isSpots) {
                videoView.stopPlayback();
                if (activity.getInformationTask() != null) {
                    activity.getInformationTask().cancel();
                }
            } else {
                cut_videoView.stopPlayback();
                if (activity.getInterCutInfoTask() != null) {
                    activity.getInterCutInfoTask().cancel();
                }
            }


            activity.nextInformation(false);
            return true;
        }
    };

    @Override
    protected void onFragmentHidden() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (!isSpots) {
                if (videoView != null && videoView.isPlaying()) {
                    videoView.pause();
                    videoView.stopPlayback();
                    LogUtil.w("xiaohui", "视频fragment被销毁");
                }
            } else {
                if (cut_videoView != null && cut_videoView.isPlaying()) {
                    cut_videoView.pause();
                    cut_videoView.stopPlayback();
                    LogUtil.w("xiaohui", "视频fragment被销毁");
                }
            }
        } catch (IllegalStateException e) {

        }
    }
}
