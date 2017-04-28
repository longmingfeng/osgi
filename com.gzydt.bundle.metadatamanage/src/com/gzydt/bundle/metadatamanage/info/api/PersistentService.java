package com.gzydt.bundle.metadatamanage.info.api;

import java.util.List;

/**
 * 同步提取接口类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public interface PersistentService {

    /**
     * 同步方法
     * 
     * @param url
     *            数据库驱动Url
     * @param user
     *            用户名
     * @param password
     *            密码
     * @param type
     *            数据库类型
     * @param metaData
     *            表信息
     * @param list
     *            字段信息
     */
    public boolean synchronous(String url, String user, String password,
        String type, TypeMetaData metaData, List<TypeMetaDataField> list);

    /**
     * 提取方法
     * 
     * @param url
     *            数据库驱动Url
     * @param user
     *            用户名
     * @param password
     *            密码
     * @param type
     *            数据库类型
     * @param tableName
     *            表名
     */
    public List<TypeMetaDataField> extract(String url, String user, String password,
        String type, String tableName, String id);

    /**
     * 获取所有表格方法
     * 
     * @param url
     *            数据库驱动Url
     * @param user
     *            用户名
     * @param password
     *            密码
     * @param type
     *            数据库类型
     */
    public List<String> getTables(String url, String user, String password,
        String type);

    /**
     * 获取表格字段信息
     * 
     * @param url
     *            数据库驱动Url
     * @param user
     *            用户名
     * @param password
     *            密码
     * @param type
     *            数据库类型
     * @param tableName
     *            表格名
     * @return
     */
    public List<String> getTableFiled(String url, String user, String password,
        String type, String tableName, String id);

    public List<List<String>> getTableValue(String url, String user, String password,
        String type, String tableName, List<String> fields);

    public List<List<String>> getTableValueByPage(String url, String user, String password,
        String type, String tableName, List<String> fields, int pageSize, int pageNum);
}
