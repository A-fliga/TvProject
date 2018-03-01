package com.app.tvproject.mvp.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Administrator on 2017/10/16 0016.
 */
@Entity
public class ContentBean implements Parcelable {
    /**
     * id : 60
     * publishTypeId : 2
     * publishTagId : 1
     * publisher : yui
     * duration : 10
     * headline : 引力波的发现
     * starttime : 1507651200000
     * endtime : 1510761600000
     * content : 引力波的发现再次证明爱因斯坦理论的正确性
     * resourcesUrl : resource/comm/image/2017/10/16/1508120899016524.jpg
     * imgormo : null
     * transformsound : 1
     * updateBy : null
     * creatBy : null
     * creatTime : 1508122577000
     * updateTime : 1508132780000
     * playTime : 10:10
     * playCount : 1
     * belongto : 1
     * belongtoId : 4
     * status : 5
     * sort : null
     * audiencebelongto : 0
     * audiencebelongtoId : 60
     * delstatus : 1
     */
    @Id
    private long id;
    private int publishTypeId;
    private int publishTagId;
    private String publisher;
    private int duration;
    private String headline;
    private long starttime;
    private long endtime;
    private String content;
    private String resourcesUrl;
    private int imgormo;
    private int transformsound;
    private String updateBy;
    private String creatBy;
    private long creatTime;
    private long updateTime;
    private String playTime;
    private int playCount;
    private int belongto;
    private int belongtoId;
    private int status;
    private long sort;
    private int audiencebelongto;
    private int audiencebelongtoId;
    private int delstatus;
    private String tagName;
    private int spots;
    private String bgm;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPublishTypeId() {
        return this.publishTypeId;
    }

    public void setPublishTypeId(int publishTypeId) {
        this.publishTypeId = publishTypeId;
    }

    public int getPublishTagId() {
        return this.publishTagId;
    }

    public void setPublishTagId(int publishTagId) {
        this.publishTagId = publishTagId;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getHeadline() {
        return this.headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public long getStarttime() {
        return this.starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return this.endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getResourcesUrl() {
        return this.resourcesUrl;
    }

    public void setResourcesUrl(String resourcesUrl) {
        this.resourcesUrl = resourcesUrl;
    }

    public int getImgormo() {
        return this.imgormo;
    }

    public void setImgormo(int imgormo) {
        this.imgormo = imgormo;
    }

    public int getTransformsound() {
        return this.transformsound;
    }

    public void setTransformsound(int transformsound) {
        this.transformsound = transformsound;
    }

    public String getUpdateBy() {
        return this.updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getCreatBy() {
        return this.creatBy;
    }

    public void setCreatBy(String creatBy) {
        this.creatBy = creatBy;
    }

    public long getCreatTime() {
        return this.creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getPlayTime() {
        return this.playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public int getPlayCount() {
        return this.playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getBelongto() {
        return this.belongto;
    }

    public void setBelongto(int belongto) {
        this.belongto = belongto;
    }

    public int getBelongtoId() {
        return this.belongtoId;
    }

    public void setBelongtoId(int belongtoId) {
        this.belongtoId = belongtoId;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getSort() {
        return this.sort;
    }

    public void setSort(long sort) {
        this.sort = sort;
    }

    public int getAudiencebelongto() {
        return this.audiencebelongto;
    }

    public void setAudiencebelongto(int audiencebelongto) {
        this.audiencebelongto = audiencebelongto;
    }

    public int getAudiencebelongtoId() {
        return this.audiencebelongtoId;
    }

    public void setAudiencebelongtoId(int audiencebelongtoId) {
        this.audiencebelongtoId = audiencebelongtoId;
    }

    public int getDelstatus() {
        return this.delstatus;
    }

    public void setDelstatus(int delstatus) {
        this.delstatus = delstatus;
    }


    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.publishTypeId);
        dest.writeInt(this.publishTagId);
        dest.writeString(this.publisher);
        dest.writeInt(this.duration);
        dest.writeString(this.headline);
        dest.writeLong(this.starttime);
        dest.writeLong(this.endtime);
        dest.writeString(this.content);
        dest.writeString(this.resourcesUrl);
        dest.writeInt(this.imgormo);
        dest.writeInt(this.transformsound);
        dest.writeString(this.updateBy);
        dest.writeString(this.creatBy);
        dest.writeLong(this.creatTime);
        dest.writeLong(this.updateTime);
        dest.writeString(this.playTime);
        dest.writeInt(this.playCount);
        dest.writeInt(this.belongto);
        dest.writeInt(this.belongtoId);
        dest.writeInt(this.status);
        dest.writeLong(this.sort);
        dest.writeInt(this.audiencebelongto);
        dest.writeInt(this.audiencebelongtoId);
        dest.writeInt(this.delstatus);
        dest.writeString(this.tagName);
        dest.writeInt(this.spots);
    }

    public int getSpots() {
        return this.spots;
    }

    public void setSpots(int spots) {
        this.spots = spots;
    }

    public String getBgm() {
        return this.bgm;
    }

    public void setBgm(String bgm) {
        this.bgm = bgm;
    }

    protected ContentBean(Parcel in) {
        this.id = in.readLong();
        this.publishTypeId = in.readInt();
        this.publishTagId = in.readInt();
        this.publisher = in.readString();
        this.duration = in.readInt();
        this.headline = in.readString();
        this.starttime = in.readLong();
        this.endtime = in.readLong();
        this.content = in.readString();
        this.resourcesUrl = in.readString();
        this.imgormo = in.readInt();
        this.transformsound = in.readInt();
        this.updateBy = in.readString();
        this.creatBy = in.readString();
        this.creatTime = in.readLong();
        this.updateTime = in.readLong();
        this.playTime = in.readString();
        this.playCount = in.readInt();
        this.belongto = in.readInt();
        this.belongtoId = in.readInt();
        this.status = in.readInt();
        this.sort = in.readLong();
        this.audiencebelongto = in.readInt();
        this.audiencebelongtoId = in.readInt();
        this.delstatus = in.readInt();
        this.tagName = in.readString();
        this.spots = in.readInt();
    }

    @Generated(hash = 1089862505)
    public ContentBean(long id, int publishTypeId, int publishTagId,
            String publisher, int duration, String headline, long starttime,
            long endtime, String content, String resourcesUrl, int imgormo,
            int transformsound, String updateBy, String creatBy, long creatTime,
            long updateTime, String playTime, int playCount, int belongto,
            int belongtoId, int status, long sort, int audiencebelongto,
            int audiencebelongtoId, int delstatus, String tagName, int spots,
            String bgm) {
        this.id = id;
        this.publishTypeId = publishTypeId;
        this.publishTagId = publishTagId;
        this.publisher = publisher;
        this.duration = duration;
        this.headline = headline;
        this.starttime = starttime;
        this.endtime = endtime;
        this.content = content;
        this.resourcesUrl = resourcesUrl;
        this.imgormo = imgormo;
        this.transformsound = transformsound;
        this.updateBy = updateBy;
        this.creatBy = creatBy;
        this.creatTime = creatTime;
        this.updateTime = updateTime;
        this.playTime = playTime;
        this.playCount = playCount;
        this.belongto = belongto;
        this.belongtoId = belongtoId;
        this.status = status;
        this.sort = sort;
        this.audiencebelongto = audiencebelongto;
        this.audiencebelongtoId = audiencebelongtoId;
        this.delstatus = delstatus;
        this.tagName = tagName;
        this.spots = spots;
        this.bgm = bgm;
    }

    @Generated(hash = 1643641106)
    public ContentBean() {
    }


    public static final Creator<ContentBean> CREATOR = new Creator<ContentBean>() {
        @Override
        public ContentBean createFromParcel(Parcel source) {
            return new ContentBean(source);
        }

        @Override
        public ContentBean[] newArray(int size) {
            return new ContentBean[size];
        }
    };
}

