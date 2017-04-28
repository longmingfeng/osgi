package com.gzydt.bundle.target.server.info.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 生成xml数据的java实体bean
 * 
 * @author longmingfeng 2016年12月19日 上午10:37:11
 */
@XStreamAlias("target")
public class Target {
    private String targetId;
    private String targetName;
    private String targetOrg;
    private String principal;
    private String phone;
    private String email;
    private String descript;
    private String serverAddress;
    private String version;
    private String updateTime;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetOrg() {
        return targetOrg;
    }

    public void setTargetOrg(String targetOrg) {
        this.targetOrg = targetOrg;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}
