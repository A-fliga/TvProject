package com.app.tvproject.myDao;

import android.content.Context;

import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.data.ContentBean;
import com.app.tvproject.mvp.model.data.TestBean;
import com.app.tvproject.utils.DownLoadFileManager;
import com.app.tvproject.utils.LogUtil;
import com.app.tvproject.utils.SharedPreferencesUtil;
import com.zh.greendao.ContentBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by www on 2017/11/19.
 */

public class DaoUtil {
    private static DaoManager mManager;

    public static void initDao(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成content记录的插入，如果表未创建，先创建content表
     *
     * @return
     */
    public static boolean insertOrReplaceContent(ContentBean contentBean) {
        boolean flag;
        flag = mManager.getDaoSession().getContentBeanDao().insertOrReplace(contentBean) != -1;
        LogUtil.i("insert content :" + flag + "-->" + contentBean.toString());
        return flag;
    }

    /**
     * 插入数组
     *
     * @param beanList
     */
    public static void insertOrReplaceList(List<ContentBean> beanList) {
        mManager.getDaoSession().getContentBeanDao().insertOrReplaceInTx(beanList);
    }

    /**
     * 插入多条数据，在子线程操作
     *
     * @return
     */
    public static boolean insertMultContent(final List<ContentBean> contentBeanList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (ContentBean contentBean : contentBeanList) {
                        mManager.getDaoSession().getContentBeanDao().insertOrReplace(contentBean);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除全部数据
     *
     * @param
     */
    public static void deleteTableData() {
        mManager.getDaoSession().getContentBeanDao().deleteAll();
    }

    /**
     * /**
     * 修改一条数据
     *
     * @return
     */
    public static boolean updateContent(ContentBean contentBean) {
        boolean flag = false;
        try {
            mManager.getDaoSession().getContentBeanDao().update(contentBean);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     *
     * @return
     */
    public static boolean deleteContent(ContentBean contentBean) {
        boolean flag = false;
        try {
            //按照内容删除
            mManager.getDaoSession().getContentBeanDao().delete(contentBean);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除多条记录
     *
     * @return
     */
    public static boolean deleteContentList(List<ContentBean> contentBean) {
        boolean flag = false;
        try {
            //按照内容删除
            mManager.getDaoSession().getContentBeanDao().deleteInTx(contentBean);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 根据Id删除
     */
    public static boolean deleteContentById(long id) {
        boolean flag = false;
        ContentBean contentBean = queryContentById(id);
        if (contentBean != null) {
            if (contentBean.getPublishTypeId() == Constants.PUBLISH_TYPE_INFORMATION || contentBean.getPublishTypeId() == Constants.PUBLISH_TYPE_ADVERT) {
                DownLoadFileManager.getInstance().addDeleteTask(queryContentById(id).getResourcesUrl());
                if(contentBean.getTransformsound() != 1 && contentBean.getBgm() != null && !contentBean.getBgm().isEmpty()){
                    DownLoadFileManager.getInstance().addDeleteTask(queryContentById(id).getBgm());
                }
            }
            try {
                //按照内容删除
                mManager.getDaoSession().getContentBeanDao().deleteByKey(id);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;

    }

    /**
     * 删除所有记录
     *
     * @return
     */
    public static boolean deleteAll() {
        boolean flag = false;
        try {
            mManager.getDaoSession().getContentBeanDao().deleteAll();
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public static List<ContentBean> queryAllContent() {
        return mManager.getDaoSession().getContentBeanDao().loadAll();
    }

    /**
     * 根据主键id查询记录
     *
     * @param key
     * @return
     */
    public static ContentBean queryContentById(long key) {
        return mManager.getDaoSession().getContentBeanDao().load(key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public static List<ContentBean> queryContentByNativeSql(String sql, String[] conditions) {
        return mManager.getDaoSession().getContentBeanDao().queryRaw(sql, conditions);
    }


    /**
     * 使用queryBuilder进行查询
     *
     * @return
     */
    public static List<ContentBean> queryContentByQueryBuilder(long id) {
        QueryBuilder<ContentBean> queryBuilder = mManager.getDaoSession().getContentBeanDao().queryBuilder();
        return queryBuilder.where(ContentBeanDao.Properties.Id.eq(id)).list();
    }

    /**
     * 查询所有还在有效期的资讯
     *
     * @return
     */
    public static List<ContentBean> loadAllValidInformation() {
        long informationId = SharedPreferencesUtil.getInformationId();
        LogUtil.w("ceshi", "上次播的id：" + informationId);
        List<ContentBean> unValidList;
        QueryBuilder<ContentBean> queryBuilder = mManager.getDaoSession().getContentBeanDao().queryBuilder();
        if (informationId != 0 && informationId != -1) {
            unValidList = queryBuilder.where(queryBuilder.and(queryBuilder.or(ContentBeanDao.Properties.PublishTypeId.eq(Constants.PUBLISH_TYPE_INFORMATION),
                    ContentBeanDao.Properties.PublishTypeId.eq(Constants.PUBLISH_TYPE_ADVERT)),
                    ContentBeanDao.Properties.Endtime.lt(System.currentTimeMillis()), ContentBeanDao.Properties.Id.notEq(informationId))).list();
        } else {
            unValidList = queryBuilder.where(queryBuilder.and(queryBuilder.or(ContentBeanDao.Properties.PublishTypeId.eq(Constants.PUBLISH_TYPE_INFORMATION),
                    ContentBeanDao.Properties.PublishTypeId.eq(Constants.PUBLISH_TYPE_ADVERT)),
                    ContentBeanDao.Properties.Endtime.lt(System.currentTimeMillis()))).list();
        }
        if (unValidList.size() != 0) {
            LogUtil.d("qidong", "deleteContentList");
            deleteContentList(unValidList);
        }
        SharedPreferencesUtil.saveInfoPosition(loadAllInformation().indexOf(queryContentById(informationId)));
        return loadAllInformation();
    }

    /**
     * 查询所有还在有效期的通知
     *
     * @return
     */
    public static List<ContentBean> loadAllValidNotice() {
        long noticeId = SharedPreferencesUtil.getNoticeId();
//        LogUtil.w("ceshi", "查询数据库noticeId为：" + noticeId);
        List<ContentBean> unValidList;
        QueryBuilder<ContentBean> queryBuilder = mManager.getDaoSession().getContentBeanDao().queryBuilder();
        if (noticeId != 0 && noticeId != -1) {
            unValidList = queryBuilder.where(queryBuilder.and(ContentBeanDao.Properties.PublishTypeId.eq(Constants.PUBLISH_TYPE_NOTICE),
                    ContentBeanDao.Properties.Endtime.lt(System.currentTimeMillis()), ContentBeanDao.Properties.Id.notEq(noticeId))).list();
        } else {
            unValidList = queryBuilder.where(queryBuilder.and(ContentBeanDao.Properties.PublishTypeId.eq(Constants.PUBLISH_TYPE_NOTICE),
                    ContentBeanDao.Properties.Endtime.lt(System.currentTimeMillis()))).list();
        }
        if (unValidList.size() != 0) {
            deleteContentList(unValidList);
        }
//        LogUtil.w("ceshi", "保存通知的新下标：" + loadAllNotice().indexOf(queryContentById(noticeId)));
        //查询正在播放的内容在最新的list数组的位置并保存（不能简单的+1求余来确定现在播放的position，会出错）
        SharedPreferencesUtil.saveNoticePosition(loadAllNotice().indexOf(queryContentById(noticeId)));
        return loadAllNotice();
    }


    //无其他条件查询所有noticeList
    //TODO  要加入updateTime对比  根据upDataTime正序查询
    private static List<ContentBean> loadAllNotice() {
        return mManager.getDaoSession().getContentBeanDao().queryBuilder().where(ContentBeanDao.Properties.
                PublishTypeId.eq(Constants.PUBLISH_TYPE_NOTICE)).orderAsc(ContentBeanDao.Properties.Sort)
                .orderDesc(ContentBeanDao.Properties.Audiencebelongto).list();
    }

    //无其他条件查询所有information
    private static List<ContentBean> loadAllInformation() {
        QueryBuilder<ContentBean> queryBuilder = mManager.getDaoSession().getContentBeanDao().queryBuilder();
        return queryBuilder.where(queryBuilder.or(ContentBeanDao.Properties.
                PublishTypeId.eq(Constants.PUBLISH_TYPE_INFORMATION), ContentBeanDao.Properties.
                PublishTypeId.eq(Constants.PUBLISH_TYPE_ADVERT))).orderAsc(ContentBeanDao.Properties.Sort)
                .orderDesc(ContentBeanDao.Properties.Audiencebelongto).list();
    }

    public static void insertPeopr(TestBean testBean){
        mManager.getDaoSession().getTestBeanDao().insertOrReplace(testBean);
    }
    public static TestBean getTest(){
        return mManager.getDaoSession().getTestBeanDao().load(1L);
    }
}
