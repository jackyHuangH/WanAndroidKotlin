package com.zenchn.apilib.entity;

import java.io.Serializable;

/**
 * @author HZJ
 */

public class UserInfoEntity implements Serializable {

    private static final long serialVersionUID = 4075623417052041363L;


    /**
     * username : 13675890723
     * realName : 黄
     * inspectHouseTotal : 0
     * inspectExecuteTotal : 0
     * inspectAdoptTotal : 0
     * inspectNonAdoptTotal : 0
     * inspectOrgName : 瑞邦
     * inspectAdoptPercentage : 0
     */

    private String username;
    private String realName;
    private Integer inspectHouseTotal;
    private Integer inspectExecuteTotal;
    private Integer inspectAdoptTotal;
    private Integer inspectNonAdoptTotal;
    private String inspectOrgName;
    private Float inspectAdoptPercentage;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getInspectHouseTotal() {
        return inspectHouseTotal;
    }

    public void setInspectHouseTotal(Integer inspectHouseTotal) {
        this.inspectHouseTotal = inspectHouseTotal;
    }

    public Integer getInspectExecuteTotal() {
        return inspectExecuteTotal;
    }

    public void setInspectExecuteTotal(Integer inspectExecuteTotal) {
        this.inspectExecuteTotal = inspectExecuteTotal;
    }

    public Integer getInspectAdoptTotal() {
        return inspectAdoptTotal;
    }

    public void setInspectAdoptTotal(Integer inspectAdoptTotal) {
        this.inspectAdoptTotal = inspectAdoptTotal;
    }

    public Integer getInspectNonAdoptTotal() {
        return inspectNonAdoptTotal;
    }

    public void setInspectNonAdoptTotal(Integer inspectNonAdoptTotal) {
        this.inspectNonAdoptTotal = inspectNonAdoptTotal;
    }

    public String getInspectOrgName() {
        return inspectOrgName;
    }

    public void setInspectOrgName(String inspectOrgName) {
        this.inspectOrgName = inspectOrgName;
    }

    public Float getInspectAdoptPercentage() {
        return inspectAdoptPercentage;
    }

    public void setInspectAdoptPercentage(Float inspectAdoptPercentage) {
        this.inspectAdoptPercentage = inspectAdoptPercentage;
    }

    @Override
    public String toString() {
        return "UserInfoEntity{" +
                "username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", inspectHouseTotal=" + inspectHouseTotal +
                ", inspectExecuteTotal=" + inspectExecuteTotal +
                ", inspectAdoptTotal=" + inspectAdoptTotal +
                ", inspectNonAdoptTotal=" + inspectNonAdoptTotal +
                ", inspectOrgName='" + inspectOrgName + '\'' +
                ", inspectAdoptPercentage=" + inspectAdoptPercentage +
                '}';
    }
}
