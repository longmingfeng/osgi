package com.gzydt.bundle.metadatamanage.info.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import com.gzydt.bundle.metadatamanage.info.api.TypeMetaData;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaDataField;

/**
 * 数据库操作工具类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class JDBCUtil {
    /**
     * 执行数据库语句
     * 
     * @param ds
     *            数据源
     * @param type
     *            类型
     * @param metaData
     *            元数据
     * @param list
     *            元数据字段
     * @param isOver
     *            是否覆盖
     * @throws Exception
     */
    public static void executeQuery(DataSource ds, String type, TypeMetaData metaData, List<TypeMetaDataField> list, boolean isOver) throws Exception {
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            statement = conn.createStatement();
            int rowCount = 0;
            if ("com.mysql.jdbc.Driver".equals(type)) {
                rs = statement.executeQuery(isExistTableByMySql(metaData.getTableName()));
                while (rs.next()) {
                    rowCount++;
                }
            } else if ("oracle.jdbc.OracleDriver".equals(type)) {
                rs = statement.executeQuery(isExistTableByOracle(metaData.getTableName()));
                while (rs.next()) {
                    rowCount = rs.getInt(1);
                }
            }
            if (rowCount != 0 && isOver) {
                statement.execute(dropTable(metaData.getTableName()));
            }
            if ("com.mysql.jdbc.Driver".equals(type)) {
                statement.execute(createTableByMySql(metaData, list));
            } else if ("oracle.jdbc.OracleDriver".equals(type)) {
                statement.execute(createTableByOracle(metaData, list));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressWarnings("resource")
    /**
     * 获取字段
     * 
     * @param ds
     *            数据源
     * @param type
     *            类型
     * @param tableName
     *            表名
     * @return 字段集合
     * @throws Exception
     */
    public static List<TypeMetaDataField> getField(DataSource ds, String type, String tableName, String id) throws Exception {
        List<TypeMetaDataField> metaDataFields = new ArrayList<>();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            statement = conn.createStatement();
            if ("com.mysql.jdbc.Driver".equals(type)) {
                statement.executeQuery("use information_schema");
                String sql = "select * from columns where table_name='" + tableName + "'";
                rs = statement.executeQuery(sql);
                while (rs.next()) {
                    TypeMetaDataField metaDataField = new TypeMetaDataField();
                    metaDataField.setId(UUID.randomUUID().toString());
                    metaDataField.setFieldId(rs.getString("column_name"));
                    metaDataField.setMetaDataId(id);
                    metaDataField.setName(rs.getString("column_name"));
                    metaDataField.setIsPrimary(rs.getString("column_key").equals("PRI") ? 1 : 0);
                    metaDataField.setIsNull(rs.getString("is_nullable").equals("YES") ? 0 : 1);
                    metaDataField.setIndexs(0);
                    metaDataField.setContent("");
                    try {
                        metaDataField.setLength(Integer.parseInt(rs.getString("column_type").substring(rs.getString("column_type").indexOf("(") + 1, rs.getString("column_type").indexOf(")"))));
                    } catch (Exception e) {
                        metaDataField.setLength(0);
                    }
                    metaDataField.setType(rs.getString("data_type"));
                    metaDataField.setDefaultValue(rs.getString("column_default"));
                    metaDataFields.add(metaDataField);
                }
            } else if ("oracle.jdbc.OracleDriver".equals(type)) {
                String sql = "select cu.* from user_cons_columns cu, user_constraints au "
                    + "where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name = '" + tableName + "'";
                rs = statement.executeQuery(sql);
                String priKey = "";
                while (rs.next()) {
                    priKey = rs.getString("COLUMN_NAME");
                }
                sql = "select * from user_tab_columns where Table_Name='" + tableName + "'";
                rs = statement.executeQuery(sql);
                while (rs.next()) {
                    TypeMetaDataField metaDataField = new TypeMetaDataField();
                    metaDataField.setId(UUID.randomUUID().toString());
                    metaDataField.setFieldId(rs.getString("COLUMN_NAME"));
                    metaDataField.setMetaDataId(id);
                    metaDataField.setName(rs.getString("COLUMN_NAME"));
                    metaDataField.setIsPrimary(rs.getString("COLUMN_NAME").equals(priKey) ? 1 : 0);
                    metaDataField.setIsNull(rs.getString("NULLABLE").equals("Y") ? 0 : 1);
                    metaDataField.setIndexs(0);
                    metaDataField.setContent("");
                    metaDataField.setLength(Integer.parseInt(rs.getString("DATA_LENGTH")));
                    metaDataField.setType(rs.getString("DATA_TYPE"));
                    metaDataField.setDefaultValue(rs.getString("DATA_DEFAULT").replace("'", ""));
                    metaDataFields.add(metaDataField);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return metaDataFields;
    }

    /**
     * 获取表格
     * 
     * @param ds
     *            数据源
     * @param type
     *            类型
     * @param databaseName
     *            库名称
     * @return 表格名称集合
     * @throws Exception
     */
    public static List<String> getTables(DataSource ds, String type, String databaseName) throws Exception {
        List<String> list = new ArrayList<>();
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            statement = conn.createStatement();
            if ("com.mysql.jdbc.Driver".equals(type)) {
                rs = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + databaseName + "'");
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
            } else if ("oracle.jdbc.OracleDriver".equals(type)) {
                rs = statement.executeQuery("select TABLE_NAME from user_tables ");
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    /**
     * 获取表格内容
     * 
     * @param ds
     *            数据源
     * @param type
     *            类型
     * @param tableName
     *            表名
     * @param fields
     *            字段集合
     * @return 表格内容集合
     * @throws Exception
     */
    public static List<List<String>> getTableValue(DataSource ds, String type, String tableName, List<String> fields) throws Exception {
        List<List<String>> list = new ArrayList<>();
        String sql = "SELECT ";
        for (String field : fields) {
            sql += field + ", ";
        }
        sql = sql.substring(0, sql.length() - 2) + " ";
        sql += "from " + tableName;
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                List<String> tableValue = new ArrayList<>();
                for (String field : fields) {
                    tableValue.add(rs.getString(field));
                }
                list.add(tableValue);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 获取表格内容并分页
     * 
     * @param ds
     *            数据源
     * @param type
     *            类型
     * @param tableName
     *            表名
     * @param fields
     *            字段名集合
     * @param pageSize
     *            页码
     * @param pageNum
     *            页数量
     * @return 表数据集合
     * @throws Exception
     */
    public static List<List<String>> getTableByPage(DataSource ds, String type, String tableName, List<String> fields, int pageSize, int pageNum) throws Exception {
        List<List<String>> list = new ArrayList<>();
        String sql = "SELECT ";
        if ("com.mysql.jdbc.Driver".equals(type)) {
            for (String field : fields) {
                sql += field + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + " ";
            sql += "from " + tableName;
            sql += " limit " + (pageNum - 1) * pageSize + "," + pageSize;
        } else if ("oracle.jdbc.OracleDriver".equals(type)) {
            sql = "SELECT t.* from ( ";
            sql += "SELECT ";
            for (String field : fields) {
                sql += field + ", ";
            }
            sql += "rownum rn ";
            sql += "from " + tableName + " ) t ";
            sql += " where t.rn between " + ((pageNum - 1) * pageSize + 1) + " and " + pageNum * pageSize;
        }
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                List<String> tableValue = new ArrayList<>();
                for (String field : fields) {
                    tableValue.add(rs.getString(field));
                }
                list.add(tableValue);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 是否存在表的语句（mysql）
     * 
     * @param tableName
     *            表名
     * @return SQL语句
     */
    private static String isExistTableByMySql(String tableName) {
        return "SELECT table_name FROM information_schema.TABLES WHERE table_name ='" + tableName + "'";
    }

    /**
     * 是否存在表的语句（oracle）
     * 
     * @param tableName
     *            表名
     * @return SQL语句
     */
    private static String isExistTableByOracle(String tableName) {
        tableName = tableName.toUpperCase();
        return "select count(*) from user_tables where table_name = '" + tableName + "'";
    }

    /**
     * 创建表格（mysql）
     * 
     * @param metaData
     *            元数据信息
     * @param list
     *            元数据字段集合
     * @return sql语句
     */
    private static String createTableByMySql(TypeMetaData metaData, List<TypeMetaDataField> list) {
        String sql = "create table " + metaData.getTableName() + "(";
        for (TypeMetaDataField typeMetaDataField : list) {
            String fieldType = "";
            String defaultValue = "";
            if (typeMetaDataField.getType().equals("String")) {
                fieldType = "varchar(" + typeMetaDataField.getLength() + ")";
                defaultValue = " default '" + (typeMetaDataField.getDefaultValue().equals("") ? "" : typeMetaDataField.getDefaultValue()) + "'";
            } else if (typeMetaDataField.getType().equals("double") || typeMetaDataField.getType().equals("int")) {
                fieldType = typeMetaDataField.getType();
                defaultValue = " default " + (typeMetaDataField.getDefaultValue().equals("") ? "0" : typeMetaDataField.getDefaultValue()) + "";
            } else if (typeMetaDataField.getType().equals("date")) {
                fieldType = typeMetaDataField.getType();
                defaultValue = " default '" + (typeMetaDataField.getDefaultValue().equals("") ? "" : typeMetaDataField.getDefaultValue()) + "'";
            }
            sql += typeMetaDataField.getFieldId() + " " + fieldType + " " +
                " " + (typeMetaDataField.getIsNull() == 1 ? "not null" : "") + defaultValue + " " + (typeMetaDataField.getIsPrimary() == 1 ? "primary key" : "") + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        return sql;
    }

    /**
     * 创建表格（oracle）
     * 
     * @param metaData
     *            元数据信息
     * @param list
     *            元数据字段集合
     * @return sql语句
     */
    private static String createTableByOracle(TypeMetaData metaData, List<TypeMetaDataField> list) {
        String sql = "create table " + metaData.getTableName() + "(";
        for (TypeMetaDataField typeMetaDataField : list) {
            String fieldType = "";
            String defaultValue = "";
            if (typeMetaDataField.getType().equals("String")) {
                fieldType = "varchar2(" + typeMetaDataField.getLength() + ")";
                defaultValue = " default '" + typeMetaDataField.getDefaultValue() + "'";
            } else if (typeMetaDataField.getType().equals("double")) {
                fieldType = "number(" + typeMetaDataField.getLength() + ",3)";
                defaultValue = " default " + typeMetaDataField.getDefaultValue() + "";
            } else if (typeMetaDataField.getType().equals("date")) {
                fieldType = typeMetaDataField.getType();
                defaultValue = " default sysdate ";
            } else if (typeMetaDataField.getType().equals("int")) {
                fieldType = "number(" + typeMetaDataField.getLength() + ")";
                defaultValue = " default " + typeMetaDataField.getDefaultValue() + "";
            }
            sql += typeMetaDataField.getFieldId() + " " + fieldType + " " + defaultValue +
                " " + (typeMetaDataField.getIsNull() == 1 ? "not null" : "") + " " + (typeMetaDataField.getIsPrimary() == 1 ? "primary key" : "") + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += ")";
        return sql;
    }

    /**
     * 删除表格
     * 
     * @param tableName
     *            表名
     * @return sql语句
     */
    private static String dropTable(String tableName) {
        return "drop table " + tableName + "";
    }
}
