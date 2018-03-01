package com.app.tvproject.utils;

import android.text.format.DateFormat;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by www on 2017/11/17.
 */

public class InitDateUtil {
    //初始化日期
    public static void initDate(TextView dateTV) {
        long sysTime = System.currentTimeMillis();
        CharSequence sysTimeStr = DateFormat.format("yyyy年MM月dd日", sysTime);
        dateTV.setText(sysTimeStr);
    }

    //初始化时钟
    public static String initClock(TextView clockTv) {
        long sysTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(sysTime);
        String dateStr = simpleDateFormat.format(date);
        if (clockTv != null)
            clockTv.setText(dateStr);
        return dateStr;
    }

    //初始化农历
    public static void initLunar(TextView lunarTv) {
        Calendar calendar = Calendar.getInstance();
        LunarUtil lunarUtil = new LunarUtil(calendar);
        lunarTv.setText("农历" + lunarUtil.toString());
    }
}
