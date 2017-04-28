package com.gzydt.bundle.metadatamanage.info.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "type_metadata_field")
/**
 * 类型元数据字段
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class TypeMetaDataField {
    @Id
    // 编号
    private String id;

    @Column
    // 字段编号（相当于表的字段名）
    private String fieldId;

    @Column
    // 对应的类型元数据
    private String metaDataId;

    @Column
    // 名称
    private String name;

    @Column
    // 是否主键（0=否，1=是）
    private int isPrimary;

    @Column
    // 是否为空（0=否，1=是）
    private int isNull;

    @Column
    // 索引值
    private int indexs;

    @Column
    // 描述
    private String content;

    @Column
    // 长度
    private int length;

    @Column
    // 字段类型（int,double,String,date）
    private String type;

    @Column
    // 默认值
    private String defaultValue;

    public TypeMetaDataField() {
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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
