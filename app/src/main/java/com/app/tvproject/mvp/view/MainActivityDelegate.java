package com.app.tvproject.mvp.view;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.tvproject.R;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.WeatherBean;
import com.app.tvproject.utils.InitDateUtil;
import com.app.tvproject.utils.LogUtil;

/**
 * Created by www on 2017/11/16.
 */

public class MainActivityDelegate extends ViewDelegate {
    private HorizontalScrollView mNoticeScrollView;
    private TextView notice_content, clockTv, lunarTv, dateTv, weatherTv, tv_logo_title, tv_tag, tv_title;
    private MainActivityDelegate.TranRunnableQueen tranRunnableQueen;
    private ImageView img_logo;
    private FrameLayout imgFrameLayout, videoFrameLayout, img_interCut_frameLayout;
    private RelativeLayout top_rl;

    @Override
    public void onDestroy() {

    }


    @Override
    public int getRootLayoutId() {
        return R.layout.activity_main;
    }

    public void hideMainRl(Boolean hide) {
        getActivity().runOnUiThread(() -> get(R.id.main_rl).setVisibility(hide ? View.GONE : View.VISIBLE));
    }

    //初始化日期
    public void initDate() {
        getActivity().runOnUiThread(() -> InitDateUtil.initDate(dateTv));

    }

    //初始化时钟
    public void initClock() {
        ClockRun run = new ClockRun();
        getActivity().runOnUiThread(run);

    }

    private class ClockRun implements Runnable {

        @Override
        public void run() {
            String clock = InitDateUtil.initClock(clockTv);
            if (clock.equals("00:00:00") || clock.equals("00:00:05")) {
                initDate();
                initLunar();
            }
        }
    }

    @Override
    public void initWidget() {
        super.initWidget();
        dateTv = get(R.id.right_data_tv);
        clockTv = get(R.id.right_time_tv);
        lunarTv = get(R.id.right_calendar_tv);
        weatherTv = get(R.id.right_air_tv);
        notice_content = get(R.id.tv_no_content);
        mNoticeScrollView = get(R.id.marquee_tv);
        tv_logo_title = get(R.id.tv_logo_title);
        img_logo = get(R.id.img_logo);
        tv_tag = get(R.id.tv_tag);
        tv_title = get(R.id.tv_title);
        imgFrameLayout = get(R.id.img_frameLayout);
        videoFrameLayout = get(R.id.videoFrameLayout);
        top_rl = get(R.id.top_rl);
        img_interCut_frameLayout = get(R.id.img_interCut_frameLayout);
        bringFront();
    }

    public void setVisibility(Boolean isImg) {
        getActivity().runOnUiThread(() -> {
            if (isImg) {
                imgFrameLayout.setVisibility(View.VISIBLE);
                videoFrameLayout.setVisibility(View.GONE);
            } else {
                imgFrameLayout.setVisibility(View.GONE);
                videoFrameLayout.setVisibility(View.VISIBLE);
                top_rl.bringToFront();
            }
        });
    }

    public void bringFront() {
        top_rl.bringToFront();
    }

    //控制原有的img视图隐藏
    public void setImgFrameVisibility(Boolean show) {
        getActivity().runOnUiThread(() -> {
            if (show)
                imgFrameLayout.setVisibility(View.VISIBLE);
            else imgFrameLayout.setVisibility(View.GONE);
        });
    }

    //控制插播的图文显示隐藏
    public void setCutImgVisibility(Boolean show) {
        getActivity().runOnUiThread(() -> {
            if (show) {
                img_interCut_frameLayout.setVisibility(View.VISIBLE);
            } else img_interCut_frameLayout.setVisibility(View.GONE);

        });
    }

    //控制原有的video视图隐藏
    public void setVideoFrameVisibility(Boolean show) {
        getActivity().runOnUiThread(() -> {
            if (show)
                videoFrameLayout.setVisibility(View.VISIBLE);
            else videoFrameLayout.setVisibility(View.GONE);

        });
    }


    //设置logo下方字体的颜色
    public void setTitleType(Boolean isImg) {
        getActivity().runOnUiThread(() -> {
            if (isImg) {
                tv_logo_title.setTextColor(getActivity().getResources().getColor(R.color.color_black));
            } else
                tv_logo_title.setTextColor(getActivity().getResources().getColor(R.color.color_title));
        });

    }

    //设置logo
    public void setLogo(Boolean isImg) {
        getActivity().runOnUiThread(() -> {
            if (isImg) {
                img_logo.setImageResource(R.drawable.logo_icon);
            } else img_logo.setImageResource(R.drawable.logo_video);
        });

    }

    //设置logo下方内容标签
    public void setTagContent(String content) {
        getActivity().runOnUiThread(() -> {
            if (content != null) {
                tv_tag.setText(content);
                if (content.replaceAll(" ", "").isEmpty())
                    tv_tag.setVisibility(View.GONE);
                else tv_tag.setVisibility(View.VISIBLE);
            } else tv_tag.setText("");
        });
    }

    //设置资讯标题
    public void setTitle(String content) {
        getActivity().runOnUiThread(() -> {
            tv_title.setText(content);
            if (content.replaceAll(" ", "").isEmpty())
                tv_title.setVisibility(View.GONE);
            else tv_title.setVisibility(View.VISIBLE);
        });

    }

    //初始化农历
    public void initLunar() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InitDateUtil.initLunar(lunarTv);
            }
        });

    }

    //初始化天气
    public void initWeather(WeatherBean.ResultBean weatherBean) {
        if (weatherBean.HeWeather5.get(0).now != null && weatherBean.HeWeather5.get(0).aqi != null) {
            getActivity().runOnUiThread(() -> weatherTv.setText(weatherBean.HeWeather5.get(0).now.tmp + "℃ " + weatherBean.HeWeather5.get(0).aqi.city.qlty));
        }
    }


    private long duration = 0;

    //开始跑马灯动画
    public void startMarquee(ContentBean contentBean) {
        getActivity().runOnUiThread(() -> {
            //这里设置字体颜色是为了切换的时候看起来不会闪一下
            notice_content.setTextColor(getActivity().getResources().getColor(R.color.color_black_gray));
            notice_content.setText(contentBean.getContent().replaceAll(" ", "").replaceAll("\r|\n", ""));
            duration = contentBean.getDuration();
            if (tranRunnableQueen == null) {
                tranRunnableQueen = new TranRunnableQueen();
            }
            notice_content.post(tranRunnableQueen);
        });
    }


    //无通知的视图
    public void setNoticeNull() {
        getActivity().runOnUiThread(() -> notice_content.setText(""));
    }

    /**
     * 跑马灯动画
     */

    private class TranRunnableQueen implements Runnable {
        @Override
        public void run() {
            TranslateAnimation mRightToLeftAnim = new TranslateAnimation(mNoticeScrollView.getWidth(), -notice_content.getWidth(), 0, 0);
            mRightToLeftAnim.setRepeatCount(Animation.INFINITE);
            mRightToLeftAnim.setInterpolator(new LinearInterpolator());
//            LogUtil.w("daojishi", "动画倒计时：" + duration);
            mRightToLeftAnim.setDuration(duration * 1000);
            mRightToLeftAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    notice_content.setTextColor(getActivity().getResources().getColor(R.color.color_white));
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            notice_content.startAnimation(mRightToLeftAnim);
        }
    }

}
