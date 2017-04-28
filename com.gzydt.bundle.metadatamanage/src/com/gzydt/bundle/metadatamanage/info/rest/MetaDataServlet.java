/**
 *   @author longmingfeng 2017年4月13日 上午9:13:10
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.metadatamanage.info.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import com.gzydt.bundle.database.dao.CommonUtilDao;
import com.gzydt.bundle.database.dao.impl.CommonUtilDaoImpl;
import com.gzydt.bundle.metadatamanage.info.api.MetaDataService;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaData;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaDataField;
import com.gzydt.bundle.metadatamanage.info.util.MetaDataUtil;

import net.sf.json.JSONArray;

/**
 * 元数据接口（根据指定格式的URL，返回对应的元数据）,都是针对类型元数据主表type_metadata及子表type_metadata_field操作的<br>
 * <br>
 * 查询时，pageStart要显示第几页数据，pageNum每页显示多少条,<br>
 * dateParam参数对应的数据库字段为时间类型,多个之间用逗号隔开，格式timename::stime::etime,timename::stime::etime,<br>
 * likeParamNameValue模糊字段,多个之间用逗号隔开，格式key::value,key::value<br>
 * <br>
 * 
 * 查询<br>
 * http://localhost:8080/resource/元数据编号.json<br>
 * http://localhost:8080/resource/元数据编号/元数据属性表id.json<br>
 * http://localhost:8080/resource/元数据编号-Q.json?参数1=?&参数2=?<br>
 * 
 * 更新<br>
 * http://localhost:8080/resource/元数据编号.update?参数1=?&参数2=?<br>
 * http://localhost:8080/resource/元数据编号/元数据属性表id.update?参数1=?&参数2=?<br>
 * <br>
 * 
 * 新增 <br>
 * http://localhost:8080/resource/元数据编号.add?参数1=?&参数2=?<br>
 * http://localhost:8080/resource/元数据编号/元数据属性表主键id.add?参数1=?&参数2=?<br>
 * <br>
 * 
 * 删除<br>
 * http://localhost:8080/resource/元数据编号.delete<br>
 * http://localhost:8080/resource/元数据编号/元数据属性表id.delete<br>
 * <br>
 * 
 * @author longmingfeng 2017年4月14日 上午9:15:35
 */
public class MetaDataServlet extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private LogService log;

    private volatile BundleContext context;

    private volatile MetaDataService dao;

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/javascript;charset=UTF-8");

        String pathInfo = req.getPathInfo();

        Map<String, String[]> map = req.getParameterMap();

        // 数据源名(与com.gzydt.bundle.metadatamanage.info组件中所使用的数据源保持一致，即usimDS2)
        String dataSourceName = MetaDataUtil.dataSourceName;

        // 主表的表名
        String tableName = "type_metadata";
        // 主表的主键名
        String primaryKeyName = "id";

        // 子表名
        String sonTableName = "type_metadata_field";
        // 子表主键名
        String sonPrimaryKeyName = "id";
        // 子表外键名
        String sonFkName = "metaDataId";

        // 第几页（默认第一页）
        String pageStart = (map.get("pageStart") == null || map.get("pageStart").length == 0) ? "1" : map.get("pageStart")[0];

        // 每面显示几条(这里默认10条)
        String pageNum = (map.get("pageNum") == null || map.get("pageNum").length == 0) ? "10" : map.get("pageNum")[0];

        // 查询时，参数对应的数据库字段为时间类型的字段名::开始时间::结束时间,多个字段以逗号隔开（timename::stime::etime,timename::stime::etime）
        String dateParam = (map.get("dateParam") == null || map.get("dateParam").length == 0) ? "" : map.get("dateParam")[0];
        List<String> dateParamList = !"".equals(dateParam) ? Arrays.asList(dateParam.split(",")) : new ArrayList<String>();

        // 模糊字段，可能有多个,多个之间用逗号隔开（以key::value,key::value方式传递）key为字段名，value为模糊字段值
        String likeParamNameValue = (map.get("likeParamNameValue") == null || map.get("likeParamNameValue").length == 0) ? "" : map.get("likeParamNameValue")[0];
        List<String> likeParam = !"".equals(likeParamNameValue) ? Arrays.asList(likeParamNameValue.split(",")) : new ArrayList<String>();

        List<String> paramList = new ArrayList<String>();
        for (String set : map.keySet()) {
            if (!"pageStart".equals(set)
                && !"pageNum".equals(set)
                && !"dateParam".equals(set)
                && !"likeParamNameValue".equals(set)) {
                paramList.add(set + "::" + map.get(set)[0]);// 用双冒号隔开，避免在后面拆分时，与时间类型的冲突
            }
        }

        JSONArray jsonArray = new JSONArray();
        boolean isSuccess = false;

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao util = new CommonUtilDaoImpl(con, log);

        int num = pathInfo.split("/").length - 1;// pathInfo包含/的个数

        String id = null;// http://localhost:8080/resource/资源编号.json 中的资源编号

        String metaDataID = null;// http://localhost:8080/resource/资源编号/资源id.json 中的资源id

        if (num == 1) {
            // 主表一条数据与子表多条数据放到jsonArray
            if (pathInfo.contains(".json")) {
                id = pathInfo.substring(1, pathInfo.indexOf(".json"));
            } else if (pathInfo.contains(".add")) {
                id = pathInfo.substring(1, pathInfo.indexOf(".add"));
            } else if (pathInfo.contains(".delete")) {
                id = pathInfo.substring(1, pathInfo.indexOf(".delete"));
            } else if (pathInfo.contains(".update")) {
                id = pathInfo.substring(1, pathInfo.indexOf(".update"));
            }

        } else if (num == 2) {
            // 子表一条数据
            // 子表主键，TypeMetaDataField表的主键
            if (pathInfo.contains(".json")) {
                id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1, pathInfo.indexOf(".json"));
            } else if (pathInfo.contains(".add")) {
                id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1, pathInfo.indexOf(".add"));
            } else if (pathInfo.contains(".delete")) {
                id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1, pathInfo.indexOf(".delete"));
            } else if (pathInfo.contains(".update")) {
                id = pathInfo.substring(pathInfo.lastIndexOf("/") + 1, pathInfo.indexOf(".update"));
            }
            // 子表外键，TypeMetaDataField表的外键
            metaDataID = pathInfo.substring(1, pathInfo.lastIndexOf("/"));
        }

        if (num == 1 && pathInfo.contains(".add")) {// 新增主表
            log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号.add");
            isSuccess = util.save(tableName, primaryKeyName, "String", id, paramList);

        } else if (num == 2 && pathInfo.contains(".add")) {// 新增子表
            log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号/资源id.add");
            paramList.add("metaDataId::" + metaDataID);
            isSuccess = util.save(sonTableName, sonPrimaryKeyName, "String", id, paramList);

        } else if (num == 1 && pathInfo.contains(".delete")) {// 删除主表记录
            log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号.delete");
            isSuccess = util.deleteParent(tableName, primaryKeyName, id.split(","), sonTableName, sonFkName);
        } else if (num == 2 && pathInfo.contains(".delete")) {// 删除子表记录
            log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号/资源id.delete");
            isSuccess = util.deleteSon(sonTableName, sonPrimaryKeyName, id.split(","), tableName, primaryKeyName, sonFkName,
                metaDataID);

        } else if (num == 1 && pathInfo.contains(".update")) {// 修改主表
            log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号.update");
            isSuccess = util.update(tableName, primaryKeyName, id, paramList);
        } else if (num == 2 && pathInfo.contains(".update")) {// 修改子表
            log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号/资源id.update");
            isSuccess = util.update(sonTableName, sonPrimaryKeyName, id, paramList);

        } else if (num == 1 && pathInfo.contains(".json")) {// 查询主表

            if (!pathInfo.contains("-Q")) {
                // 主表一条数据与子表多条数据放到jsonArray
                log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号.json");
                id = pathInfo.substring(1, pathInfo.indexOf(".json"));

            } else {
                // 带查询条件的(即类型元数据属性表中的查询条件，主表就一条记录)
                log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号-Q.json");
                id = pathInfo.substring(1, pathInfo.indexOf("-Q.json"));
            }

            TypeMetaData typeMetaData = dao.getVOById(TypeMetaData.class, id);

            if (typeMetaData != null) {
                paramList.add("metaDataId::" + id);
                List<TypeMetaDataField> typeMetaDataFieldList = dao.getDataList(TypeMetaDataField.class, Integer.parseInt(pageStart), Integer.parseInt(pageNum), paramList, dateParamList, likeParam);

                JSONArray jsonS = JSONArray.fromObject(typeMetaDataFieldList);

                jsonArray = JSONArray.fromObject(typeMetaData);

                jsonArray.add(jsonS);
            }

        } else if (num == 2 && pathInfo.contains(".json")) {// 查询子表
            log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号/资源id.json");

            paramList.add("id::" + id);
            paramList.add("metaDataId::" + metaDataID);

            List<TypeMetaDataField> list = dao.getDataList(TypeMetaDataField.class, 0, 0, paramList, null, null);

            if (list.size() > 0) {
                jsonArray.add(list);
            }
        }

        PrintWriter out = resp.getWriter();
        if (pathInfo.contains(".json")) {
            out.print(MetaDataUtil.formatJson(jsonArray.toString()));
        } else {
            out.print(isSuccess);
        }
        out.close();

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
