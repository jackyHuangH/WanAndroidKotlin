package com.zenchn.apilib.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author:Hzj
 * @date :2018/9/30/030
 * desc  ：信息 数据实体
 * record：
 */
public class MsgEntity implements Parcelable {

    /**
     * messageId : 9DE19EE46EBD4AD89ECC47F3A5F54DDF
     * createTime : 2018-11-30 11:03:47
     * messageTypeId : 00000000000000000000000000050101
     * messageContent : 平台给您指派了一条临时任务，任务编号：【LS181130000002】、对应建筑：【运河昌平园】，请查收，并安排巡检!
     * sendTime : 2018-11-30 11:03:47
     * receiveStaffId : 00000000000000000000000000000003
     * status : 0
     * messageTypeName : 临时任务
     * receiveStaffName : null
     * statusName : 未读
     */

    private String messageId;
    private String createTime;
    private String messageTypeId;
    private String messageContent;
    private String sendTime;
    private Integer status;
    private String messageTypeName;
    private String statusName;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(String messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessageTypeName() {
        return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "MsgEntity{" +
                "messageId='" + messageId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", messageTypeId='" + messageTypeId + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", sendTime='" + sendTime + '\'' +
                ", status=" + status +
                ", messageTypeName='" + messageTypeName + '\'' +
                ", statusName='" + statusName + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.messageId);
        dest.writeString(this.createTime);
        dest.writeString(this.messageTypeId);
        dest.writeString(this.messageContent);
        dest.writeString(this.sendTime);
        dest.writeValue(this.status);
        dest.writeString(this.messageTypeName);
        dest.writeString(this.statusName);
    }

    public MsgEntity() {
    }

    protected MsgEntity(Parcel in) {
        this.messageId = in.readString();
        this.createTime = in.readString();
        this.messageTypeId = in.readString();
        this.messageContent = in.readString();
        this.sendTime = in.readString();
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.messageTypeName = in.readString();
        this.statusName = in.readString();
    }

    public static final Creator<MsgEntity> CREATOR = new Creator<MsgEntity>() {
        @Override
        public MsgEntity createFromParcel(Parcel source) {
            return new MsgEntity(source);
        }

        @Override
        public MsgEntity[] newArray(int size) {
            return new MsgEntity[size];
        }
    };
}
