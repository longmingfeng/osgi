package com.gzydt.bundle.metadatamanage.info.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "base_metadata")
/**
 * 基础元数据
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class BaseMetaData {
    @Id
    // 元数据ID(相当于metatype文件名,OCD的Id)
    private String id;

    @Column
    // 基础元数据名称
    private String name;

    @Column
    // 元数据类型
    private String type;

    @Column
    // 元数据作者
    private String author;

    @Column
    // 元数据状态（0=停用,1=启用）
    private int state;

    @Column
    // 元数据描述
    private String content;

    public BaseMetaData() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
