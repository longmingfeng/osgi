package com.gzydt.bundle.metadatamanage.info.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "type_metadata")
/**
 * 类型元数据
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class TypeMetaData {

    @Id
    // 类型元数据编号（相当于表名）
    private String id;

    @Column
    // 元数据名称
    private String name;

    @Column
    // 表名称
    private String tableName;

    @Column
    // 所属部门
    private String org;

    @Column
    // 作者
    private String author;

    @Column
    // 状态（0=停用，1=启用）
    private int state;

    @Column
    // 对应的资源元数据编号
    private String resourceMetaDataId;

    @Column
    // 描述
    private String content;

    @Column
    // 类型
    private String type;

    @Lob
    private String customize;

    public TypeMetaData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getResourceMetaDataId() {
        return resourceMetaDataId;
    }

    public void setResourceMetaDataId(String resourceMetaDataId) {
        this.resourceMetaDataId = resourceMetaDataId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomize() {
        return customize;
    }

    public void setCustomize(String customize) {
        this.customize = customize;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
