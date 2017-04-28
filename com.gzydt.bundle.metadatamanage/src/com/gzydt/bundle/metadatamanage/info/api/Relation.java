package com.gzydt.bundle.metadatamanage.info.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "relation")
public class Relation {
    @Id
    // 关系ID
    private String id;

    @Column
    // 类型元数据A
    private String metadataA;

    @Column
    // 类型元数据A的关联字段
    private String fieldA;

    @Column
    // 类型元数据B
    private String metadataB;

    @Column
    // 类型元数据B的关联字段
    private String fieldB;

    @Column
    // 关系类型
    private int type;

    @Column
    // 元数据描述
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetadataA() {
        return metadataA;
    }

    public void setMetadataA(String metadataA) {
        this.metadataA = metadataA;
    }

    public String getFieldA() {
        return fieldA;
    }

    public void setFieldA(String fieldA) {
        this.fieldA = fieldA;
    }

    public String getMetadataB() {
        return metadataB;
    }

    public void setMetadataB(String metadataB) {
        this.metadataB = metadataB;
    }

    public String getFieldB() {
        return fieldB;
    }

    public void setFieldB(String fieldB) {
        this.fieldB = fieldB;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
