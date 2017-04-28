/**
 *   @author longmingfeng    2017年2月28日  上午11:17:35
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.log.LogService;

import com.gzydt.bundle.database.dao.CommonUtilDao;

/**
 * 原生SQL的增删改查操作
 * 
 * @author longmingfeng 2017年4月14日 上午11:39:28
 */
@SuppressWarnings("restriction")
public class CommonUtilDaoImpl implements CommonUtilDao {

    private volatile LogService log;

    private volatile Connection con;// 连接对象

    private volatile String databaseType;// 数据库类型

    public CommonUtilDaoImpl(Connection con, LogService log) {

        this.con = con;
        try {
            con.setAutoCommit(false);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        this.log = log;

        try {
            databaseType = con.getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean save(String tableName, String primaryKeyName, String primaryKeyType,
        String primaryKeyValue, List<String> column) {

        boolean isSuccess = false;
        ResultSet result = null;
        PreparedStatement pre = null;

        StringBuilder sql = new StringBuilder("insert into " + tableName + " (" + primaryKeyName);

        // 拼接增加的SQL语句
        StringBuilder valueSQL = new StringBuilder();// 后面拼值用到
        if (column != null && column.size() > 0) {

            sql.append(",");

            for (int i = 0, length = column.size(); i < length; i++) {

                if (i < length - 1) {
                    sql.append(column.get(i).split("::")[0] + ",");// 拼接字段名SQL
                    valueSQL.append("'" + column.get(i).split("::")[1] + "',");// 拼接值SQL
                } else {
                    sql.append(column.get(i).split("::")[0] + ")");// 拼接字段名SQL
                    valueSQL.append("'" + column.get(i).split("::")[1] + "')");// 拼接值SQL
                }
            }
        } else {
            // 对表中只有一个字段，且是主键的特殊情况
            sql.append(")");
            valueSQL.append(")");
        }

        // 设置主键
        if ("int".equalsIgnoreCase(primaryKeyType) || "Integer".equalsIgnoreCase(primaryKeyType) || "num".equalsIgnoreCase(primaryKeyType)) {
            try {
                if (primaryKeyValue != null && !"".equals(primaryKeyValue)) {// 后端手动维护，自动输入了主键值

                    sql.append(" values(" + Integer.parseInt(primaryKeyValue));

                } else {

                    // 后端维护，先查询当前表有无记录，有记录，取主键最大值+1，无记录，刚默认为1
                    String stateSql = "select max(" + primaryKeyName + ") num from " + tableName;

                    pre = con.prepareStatement(stateSql);

                    result = pre.executeQuery();

                    if (result.next()) {// 最大ID加1
                        Long num = result.getLong(1) + 1;
                        sql.append(" values(" + num);
                    } else {// 没有记录，主键默认为1
                        sql.append(" values(1");
                    }
                }

                if (column != null && column.size() > 0) {
                    sql.append(",");
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
            } finally {
                close(result);
                close(pre);
            }
        } else if ("String".equalsIgnoreCase(primaryKeyType)) {
            if (primaryKeyValue != null && !"".equals(primaryKeyValue)) {// 后端手动维护，自动输入了主键值

                sql.append(" values('" + primaryKeyValue + "'");

            } else {

                // 后端维护，UUID
                UUID uuid = UUID.randomUUID();
                sql.append(" values('" + uuid.toString() + "'");

            }

            if (column != null && column.size() > 0) {
                sql.append(",");
            }
        }

        // 也可采用？占位符的方法赋值，这里先采用拼接SQL方式
        sql.append(valueSQL.toString());

        try {
            log.log(LogService.LOG_INFO, "保存sql语句：" + sql.toString());

            pre = con.prepareStatement(sql.toString());

            pre.executeUpdate();

            isSuccess = true;

            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(result);
            close(pre);
            close(con);
        }

        return isSuccess;
    }

    public boolean update(String tableName, String primaryKeyName, String primaryKeyValue, List<String> column) {

        boolean isSuccess = false;
        PreparedStatement pre = null;

        StringBuilder sql = new StringBuilder("update " + tableName + " set ");

        // 拼接SQL语句
        String ql = "";
        if (column != null && column.size() > 0) {
            for (int i = 0, length = column.size(); i < length; i++) {
                ql = column.get(i).split("::")[0] + " = '" + column.get(i).split("::")[1] + "'";

                if (i < length - 1) {
                    sql.append(ql + ",");
                } else {
                    sql.append(ql);
                }
            }
        }

        // 加where条件
        sql.append(" where " + primaryKeyName + " = '" + primaryKeyValue + "'");

        // 执行更新
        try {
            log.log(LogService.LOG_INFO, "更新sql语句：" + sql.toString());

            pre = con.prepareStatement(sql.toString());

            pre.executeUpdate();

            isSuccess = true;

            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(pre);
            close(con);
        }

        return isSuccess;
    }

    public JSONArray getDataById(String tableName, String primaryKeyName, String primaryKeyValue) {

        ResultSet result = null;
        PreparedStatement pre = null;

        try {
            String sql = "select * from " + tableName + " where " + primaryKeyName + " = '" + primaryKeyValue + "'";

            pre = con.prepareStatement(sql);

            log.log(LogService.LOG_INFO, "查询sql语句：" + sql);

            result = pre.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSetToList(pre, result);
    }

    public JSONArray getDataList(String pageStart, String num, String tableName, List<String> column,
        List<String> dateParam, List<String> likeParam) {

        ResultSet result = null;
        PreparedStatement pre = null;

        try {

            // 对时间类型的拼接,字段名::开始时间::结束时间,多个字段以逗号隔开（timename::stime::etime,timename::stime::etime）
            StringBuilder dateSql = new StringBuilder();
            if (dateParam != null && dateParam.size() > 0) {

                String[] dateColumn = new String[3];
                String dateColumnName = "";
                String sDate = "";
                String eDate = "";

                for (int i = 0, length = dateParam.size(); i < length; i++) {

                    dateColumn = dateParam.get(i).split("::");

                    dateColumnName = dateColumn[0];
                    sDate = dateColumn[1];
                    eDate = dateColumn[2];

                    if (i < length - 1) {
                        dateSql.append("(" + dateColumnName + " between '" + sDate +
                            "' and '" + eDate + "') and ");
                    } else {
                        dateSql.append("(" + dateColumnName + " between '" + sDate +
                            "' and '" + eDate + "') ");
                    }
                }

            } else {// 没有时间类型

            }

            // 其它查询条件的拼接
            String key = "";
            String value = "";
            StringBuilder wheresql = new StringBuilder();

            if (column != null && column.size() > 0) {
                for (int i = 0, length = column.size(); i < length; i++) {

                    key = column.get(i).split("::")[0];
                    value = column.get(i).split("::")[1];

                    if (i < (length - 1)) {
                        wheresql.append(key + " = '" + value + "' and ");
                    } else {
                        wheresql.append(key + " = '" + value + "'");
                    }
                }
            }

            // 模糊字段的拼接
            if (!"".equals(wheresql.toString())) {
                if (likeParam != null && likeParam.size() > 0) {
                    wheresql.append(" and ");
                }
            }
            if (likeParam != null && likeParam.size() > 0) {
                for (int i = 0, length = likeParam.size(); i < length; i++) {
                    key = likeParam.get(i).split("::")[0];
                    value = likeParam.get(i).split("::")[1];

                    if (i < (length - 1)) {
                        wheresql.append(key + " like '%" + value + "%' and ");
                    } else {
                        wheresql.append(key + " like '%" + value + "%'");
                    }
                }
            }

            // 将时间类型SQL与其它条件的SQL拼接
            String sql = "select 1";

            if (!"".equals(wheresql.toString())) {
                if (!"".equals(dateSql.toString())) {
                    sql = "select * from " + tableName + " where " + wheresql.toString() + " and " + dateSql.toString();
                } else {
                    sql = "select * from " + tableName + " where " + wheresql.toString();
                }
            } else {
                if (!"".equals(dateSql.toString())) {
                    sql = "select * from " + tableName + " where " + dateSql.toString();
                } else {
                    sql = "select * from " + tableName;
                }
            }

            // 分页（也可采用假分页preparedStatement.setMaxRows(num);result.absolute((pageStart-1) * num + 1);）
            if (!"".equals(pageStart) && !"".equals(num)) {
                if ("mysql".equalsIgnoreCase(databaseType)) {// limit 0,5 从第一条开始查，查5条
                    sql += " limit " + (Integer.parseInt(pageStart) - 1) * Integer.parseInt(num) + "," + Integer.parseInt(num);
                } else if ("oracle".equalsIgnoreCase(databaseType)) {
                    sql = "select * from ( select t.*,rownum row_num from (" + sql + ")t )b where b.row_num between "
                        + ((Integer.parseInt(pageStart) - 1) * Integer.parseInt(num) + 1) + " and " + Integer.parseInt(pageStart) * Integer.parseInt(num);
                }
            } else {
                // 不分页
            }

            log.log(LogService.LOG_INFO, "查询sql语句：" + sql);

            pre = con.prepareStatement(sql);
            result = pre.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSetToList(pre, result);
    }

    public boolean delete(String tableName, String primaryKeyName, String[] ids) {

        boolean isSuccess = false;
        ResultSet result = null;
        PreparedStatement pre = null;

        String sql = "select 1";
        try {
            for (String id : ids) {

                sql = "delete from " + tableName + " where " + primaryKeyName + " = '" + id + "'";

                log.log(LogService.LOG_INFO, "删除sql语句：" + sql);

                pre = con.prepareStatement(sql);

                pre.executeUpdate();

                isSuccess = true;

                con.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(result);
            close(pre);
            close(con);
        }

        return isSuccess;

    }

    @SuppressWarnings("resource")
    public boolean deleteParent(String parentTableName, String parentPrimaryKeyName, String[] ids,
        String sonTableName, String sonFkName) {

        boolean isSuccess = false;
        ResultSet result = null;
        PreparedStatement pre = null;

        String sql = "select 1";
        try {
            for (String id : ids) {

                // 先删除子表对应的数据
                sql = "delete from " + sonTableName + " where " + sonFkName + " = '" + id + "'";

                log.log(LogService.LOG_INFO, "deleteParent方法中删除子表sql语句：" + sql);

                pre = con.prepareStatement(sql);

                pre.executeUpdate();

                // 再删除主表相应的数据
                sql = "delete from " + parentTableName + " where " + parentPrimaryKeyName + " = '" + id + "'";

                log.log(LogService.LOG_INFO, "deleteParent方法中删除主表sql语句：" + sql);

                pre = con.prepareStatement(sql);

                pre.executeUpdate();
                
                con.commit();

                isSuccess = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(result);
            close(pre);
            close(con);
        }

        return isSuccess;
    }

    @SuppressWarnings({ "resource" })
    public boolean deleteSon(String sonTableName, String sonPrimaryKeyName, String[] ids,
        String parentTableName, String parentPrimaryKeyName,
        String sonFkName, String sonFkValue) {

        boolean isSuccess = false;
        ResultSet result = null;
        PreparedStatement pre = null;

        String sql = "select 1";
        try {
            for (String id : ids) {

                // 先删除子表对应的数据
                sql = "delete from " + sonTableName + " where " + sonPrimaryKeyName + " = '" + id + "'";
                log.log(LogService.LOG_INFO, "deleteSon方法中删除子表sql语句：" + sql);
                pre = con.prepareStatement(sql);
                pre.executeUpdate();
                con.commit();
                
                // 再查询子表还有没有刚删除外键值一样的数据，有，就不操作，没有，就需要将主表中对应的记录删除
                sql = "select * from " + sonTableName + " where " + sonFkName + " = '" + sonFkValue + "'";
                log.log(LogService.LOG_INFO, "deleteSon方法中查询子表sql语句：" + sql);
                pre = con.prepareStatement(sql);

                if (pre.executeQuery(sql).next()) {// 有，就不操作

                } else {// 没有，就需要将主表中对应的记录删除
                    sql = "delete from " + parentTableName + " where " + parentPrimaryKeyName + " = '" + sonFkValue + "'";
                    log.log(LogService.LOG_INFO, "deleteSon方法中删除主表sql语句：" + sql);
                    pre = con.prepareStatement(sql);
                    pre.executeUpdate();
                    con.commit();
                }

                isSuccess = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(result);
            close(pre);
            close(con);
        }

        return isSuccess;
    }

    /**
     * 将ResultSet结果集转成JSONArray
     * 
     * @param pre
     * @param rs
     * @return
     * @author longmingfeng 2017年2月28日 上午11:25:03
     */
    public JSONArray resultSetToList(PreparedStatement pre, ResultSet result) {

        if (result == null)
            return null;

        JSONArray array = new JSONArray();
        try {
            ResultSetMetaData md = result.getMetaData();// 得到结果集(rs)的结构信息，比如字段数、字段名等

            int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数

            JSONObject json = null;
            while (result.next()) {
                json = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    json.put(md.getColumnName(i), result.getObject(i));
                }
                array.put(json);
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
        } finally {
            close(result);
            close(pre);
            close(con);
        }

        log.log(LogService.LOG_INFO, "返回json数据：" + array);
        return array;
    }

    /**
     * 关闭相应对象
     * 
     * @param close
     * @author longmingfeng 2017年2月28日 下午1:54:23
     */
    public static void close(AutoCloseable close) {
        if (close != null) {
            try {
                close.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
