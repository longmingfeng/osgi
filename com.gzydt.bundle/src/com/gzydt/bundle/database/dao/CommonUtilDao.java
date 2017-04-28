/**
 *   @author longmingfeng    2017年2月28日  上午9:22:59
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.database.dao;

import java.util.List;

import org.json.JSONArray;

/**
 * 提供增删改查接口（操作的都是数据库）
 * 
 * @author longmingfeng 2017年2月28日 上午9:22:59
 */
@SuppressWarnings("restriction")
public interface CommonUtilDao {

    /**
     * 保存数据（单表保存，与主从表保存一样，只不过从表保存时，多传一个外键名及外键值）
     * 
     * @param tableName
     *            表名
     * @param primaryKeyName
     *            主键名
     * @param primaryKeyType
     *            主键类型
     * @param primaryKeyValue
     *            主键值（有可能前端自己维护主键）
     * @param column
     *            表列名，表列名值（以"key::value"形式存储）
     * @author longmingfeng 2017年2月28日 上午10:14:33
     */
    public boolean save(String tableName, String primaryKeyName, String primaryKeyType,
        String primaryKeyValue, List<String> column);

    /**
     * 更新数据（单表更新，与从表的更新一样，只不过从表的外键不允许更新）<br>
     * 一般来讲，主键是不允许更改的，即主表的主键不能更改，那么子表的外键也就不存在要更改了
     * 
     * @param tableName
     *            表名
     * @param primaryKeyName
     *            主键名
     * @param primaryKeyValue
     *            主键值
     * @param column
     *            表列名，表列名值（以"key::value"形式存储）
     * @author longmingfeng 2017年2月28日 上午11:14:33
     */
    public boolean update(String tableName, String primaryKeyName, String primaryKeyValue, List<String> column);

    /**
     * 根据主键查询数据
     * 
     * @param tableName
     *            表名
     * @param primaryKeyName
     *            主键名
     * @param primaryKeyValue
     *            主键值
     * @author longmingfeng 2017年2月28日 上午11:15:33
     */
    public JSONArray getDataById(String tableName, String primaryKeyName, String primaryKeyValue);

    /**
     * 分页查找数据（单表） （当pageStart与num都为空字符串时,则不分页）
     * 
     * @param pageStart
     *            第几页
     * @param num
     *            每页显示几条
     * @param tableName
     *            表名
     * @param column
     *            表列名，表列名值（以"key::value"形式存储）
     * @param dateParam
     *            时间字段名（数据库为DATE,TIME类型），如果没有，传空字符串。字段名::开始时间::结束时间（timename::stime::etime）)
     * @param likeParam
     *            模糊查询的字段名::模糊查询的字段值（以"key::value"形式存储）
     * @author longmingfeng 2017年2月28日 上午11:15:33
     */
    public JSONArray getDataList(String pageStart, String num, String tableName,
        List<String> column, List<String> dateParam,
        List<String> likeParam);

    /**
     * 删除数据（单表）
     * 
     * @param tableName
     *            表名
     * @param primaryKeyName
     *            主键名
     * @param id
     *            主键值集合
     * @author longmingfeng 2017年2月28日 上午11:15:33
     */
    public boolean delete(String tableName, String primaryKeyName, String[] ids);

    /**
     * 删除主表数据（级联删除时，需要先删除子表对应所有数据，再删主表数据）
     * 
     * @param tableName
     *            主表名
     * @param primaryKeyName
     *            主表的主键名
     * @param ids
     *            主表的主键值集合
     * @param sonTableName
     *            子表表名
     * @param sonFkName
     *            子表的外键名，两表的关联字段名
     * @author longmingfeng 2017年3月21日 上午11:15:33
     */
    public boolean deleteParent(String tableName, String primaryKeyName,
        String[] ids, String sonTableName, String sonFkName);

    /**
     * 删除子表数据
     * 
     * @param sonTableName
     *            子表名
     * @param sonPrimaryKeyName
     *            子表的主键名
     * @param id
     *            子表主键值集合
     * @param tableName
     *            父表名
     * @param primaryKeyName
     *            父表主键名
     * @param sonFkName
     *            子表外键名
     * @param sonFkValue
     *            子表的外键值
     * @author longmingfeng 2017年3月21日 上午11:15:33
     */
    public boolean deleteSon(String sonTableName, String sonPrimaryKeyName,
        String[] ids, String tableName, String primaryKeyName,
        String sonFkName, String sonFkValue);

}
