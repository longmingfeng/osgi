package com.gzydt.bundle.metadatamanage.info.api;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "resource_metadata")
/**
 * 资源元数据
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class ResourceMetaData {

    @Id
    // 元数据编号（相当于资源文件的名称）
    private String id;

    @Column
    // 元数据名称
    private String name;

    @Column
    // 元数据类型
    private String type;

    @Column
    // 元数据作者
    private String author;

    @Column
    // 元数据状态（0=停用，1=启用）
    private int state;

    @Column
    // 资源文件路径
    private String path;

    @Column
    // 资源文件类型
    private int resourceType;

    @Column
    // 元数据描述
    private String content;

    public ResourceMetaData() {

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
