package com.gzydt.bundle.metadatamanage.info.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "base_metadata_field")
/**
 * 基础元数据字段
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class BaseMetaDataField {
    @Id
    // 字段编号
    private String id;

    @Column
    // 字段的名称（相当于AD其中的一个属性）
    private String fieldId;

    @Column
    // 对应的基础元数据编号
    private String metaDataId;

    @Column
    // 字段名称
    private String name;

    @Column
    // 是否主键（0=否,1=是）
    private int isPrimary;

    @Column
    // 是否为空（0=否，1=是）
    private int isNull;

    @Column
    // 索引值
    private int indexs;

    @Column
    // 行数
    private int rowNum;

    @Column
    // 提示内容
    private String tip;

    @Column
    // 描述
    private String content;

    @Column
    // 类型
    private String type;

    @Column
    // 默认值
    private String defaultValue;

    public BaseMetaDataField() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getMetaDataId() {
        return metaDataId;
    }

    public void setMetaDataId(String metaDataId) {
        this.metaDataId = metaDataId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(int isPrimary) {
        this.isPrimary = isPrimary;
    }

    public int getIsNull() {
        return isNull;
    }

    public void setIsNull(int isNull) {
        this.isNull = isNull;
    }

    public int getIndexs() {
        return indexs;
    }

    public void setIndexs(int indexs) {
        this.indexs = indexs;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
