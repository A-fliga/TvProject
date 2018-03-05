package com.app.tvproject.greenDaoHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.app.tvproject.utils.ToastUtil;
import com.zh.greendao.DaoMaster;

import org.greenrobot.greendao.database.Database;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by www on 2018/3/1.
 * 更新数据库
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }


    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        //注意历史数据库版本
        ToastUtil.l("oldVersion" + oldVersion + "new:" + newVersion);
        switch (oldVersion) {
            case 2:
//                TestBeanDao.createTable(db, false);
            case 8:
//                Map<String,String> keyMap = new HashMap<>();
//                keyMap.put("myString222","myStringtest");
//                MigrationHelper.getInstance().migrate(db,keyMap,TestBeanDao.class);
        }
    }
}
