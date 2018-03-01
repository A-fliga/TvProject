package com.app.tvproject.mvp.presenter.activity;

import android.content.Intent;
import android.os.Bundle;

import com.app.tvproject.BuildConfig;
import com.app.tvproject.application.MyApplication;
import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.adapter.ChooseSettingsAdapter;
import com.app.tvproject.mvp.model.PublicModel;
import com.app.tvproject.mvp.model.data.ChooseSettingsBean;
import com.app.tvproject.mvp.view.ChooseSettingsActivityDelegate;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.NetUtil;
import com.app.tvproject.utils.ProgressDialogUtil;
import com.app.tvproject.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by www on 2017/11/16.
 * 选择省市配置信息的presenter
 */

public class ChooseSettingsActivity extends ActivityPresenter<ChooseSettingsActivityDelegate> {
    private int type = 1;
    private ChooseSettingsAdapter adapter;
    private List<Long> idArr = new ArrayList<>();

    @Override
    public Class<ChooseSettingsActivityDelegate> getDelegateClass() {
        return ChooseSettingsActivityDelegate.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取省
        getSettings(type, Constants.DEFAULT_ID);
    }

    /**
     * type	是	int	类型 1省 2市 3区 4县 5社区 6物业 7设备
     * id是	long	父ID 当type为1传空
     *
     * @param type
     * @param id
     */
    private void getSettings(int type, long id) {
        ProgressDialogUtil.instance().startLoad();
        PublicModel.getInstance().getSettingsData(new Subscriber<ChooseSettingsBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                LogUtil.w("测试", "" + e);
                ProgressDialogUtil.instance().stopLoad();
            }

            @Override
            public void onNext(ChooseSettingsBean chooseSettingsBean) {
                ProgressDialogUtil.instance().stopLoad();
                adapter = new ChooseSettingsAdapter(ChooseSettingsActivity.this, chooseSettingsBean.result);
                viewDelegate.initSettingsView(adapter);
                //设置item点击监听
                adapter.setOnItemClickListener(itemClickListener);
            }
        }, String.valueOf(type), id == Constants.DEFAULT_ID ? null : String.valueOf(id));
    }

    private ChooseSettingsAdapter.OnItemClickListener itemClickListener = resultBean -> {
        //如果点击的是设备ID，则更新设备状态，保存极光别名且回传设备id
        if (resultBean.equipmentnumber != null) {
            initSettings(resultBean);
        } else {
            //点击子项后 type要自加，保存已点击的id回退栈
            type++;
            idArr.add(resultBean.id);
            getSettings(type, resultBean.id);
        }
    };

    private void initSettings(ChooseSettingsBean.ResultBean resultBean) {
        long id = resultBean.id;
        String alias;
////        if (BuildConfig.DEBUG) {
//            alias = "CS_touchid" + id;
////        } else {
        alias = Constants.JPUSH_NAME + id;
//        }
        //设置极光别名
        MyApplication.getAppContext().setAlisa(alias);
        //保存设备id
        SharedPreferencesUtil.saveEqId(this, id);
        backToMain(resultBean);
    }

    private void backToMain(ChooseSettingsBean.ResultBean resultBean) {
        Intent intent = new Intent();
        intent.putExtra("eqId", resultBean.id);
        intent.putExtra("voice", resultBean.voice);
        setResult(Constants.CHOOSE_SETTINGS_RESULT_CODE, intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        if (type <= 1)
            finish();
        else {
            if (NetUtil.isConnect()) {
                //点back删掉回退栈里最后一个id数据
                type--;
                idArr.remove(idArr.size() - 1);
                getSettings(type, idArr.size() == 0 ? Constants.DEFAULT_ID : idArr.get(idArr.size() - 1));
            }
        }
    }
}
