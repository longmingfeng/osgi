/**
 *   @author longmingfeng 2017年4月13日 上午9:13:10
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.metadatamanage.info.rest;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

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
@Path("")
public class MetaDataRest {

    @Context
    private HttpServletRequest request;

    @Context
    private HttpServletResponse response;

    private LogService log;

    private volatile BundleContext context;

    private volatile MetaDataService dao;

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

    /**
     * 根据类型元数据据ID查询本身信息及子表所有属性,不带条件查询
     * 
     * @param type_meta_data_id
     *            类型元数据ID
     * @return
     * @author longmingfeng 2017年4月20日 下午1:58:13
     */
    @SuppressWarnings("unchecked")
    @GET
    @Path("{type_meta_data_id}.json")
    @Produces({ "application/json" })
    public Response getTypeFileMany(@PathParam("type_meta_data_id") String type_meta_data_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号.json");

        response.setCharacterEncoding("UTF-8");

        Map<String, String[]> map = request.getParameterMap();

        // 第几页（默认第一页）
        String pageStart = (map.get("pageStart") == null || map.get("pageStart").length == 0) ? "1" : map.get("pageStart")[0];

        // 每面显示几条(这里默认10条)
        String pageNum = (map.get("pageNum") == null || map.get("pageNum").length == 0) ? "10" : map.get("pageNum")[0];

        List<String> paramList = new ArrayList<String>();
        JSONArray jsonArray = new JSONArray();

        TypeMetaData typeMetaData = dao.getVOById(TypeMetaData.class, type_meta_data_id);

        if (typeMetaData != null) {
            paramList.add("metaDataId::" + type_meta_data_id);
            List<TypeMetaDataField> typeMetaDataFieldList = dao.getDataList(TypeMetaDataField.class, Integer.parseInt(pageStart), Integer.parseInt(pageNum), paramList, null, null);

            JSONArray jsonS = JSONArray.fromObject(typeMetaDataFieldList);

            jsonArray = JSONArray.fromObject(typeMetaData);

            jsonArray.add(jsonS);
        }
        return Response.ok(MetaDataUtil.formatJson(jsonArray.toString())).build();
    }

    /**
     * 根据类型元数据据ID查询本身信息及子表所有属性,带条件查询
     * 
     * @param type_meta_data_id
     *            类型元数据ID
     * @return
     * @author longmingfeng 2017年4月20日 下午1:58:13
     */
    @SuppressWarnings("unchecked")
    @GET
    @Path("{type_meta_data_id}-Q.json")
    @Produces({ "application/json" })
    public Response getTypeFileManyQ(@PathParam("type_meta_data_id") String type_meta_data_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/资源编号-Q.json");

        response.setCharacterEncoding("UTF-8");

        Map<String, String[]> map = request.getParameterMap();

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

        TypeMetaData typeMetaData = dao.getVOById(TypeMetaData.class, type_meta_data_id);

        if (typeMetaData != null) {

            paramList.add("metaDataId::" + type_meta_data_id);

            List<TypeMetaDataField> typeMetaDataFieldList = dao.getDataList(TypeMetaDataField.class, Integer.parseInt(pageStart), Integer.parseInt(pageNum), paramList, dateParamList, likeParam);

            JSONArray jsonS = JSONArray.fromObject(typeMetaDataFieldList);

            jsonArray = JSONArray.fromObject(typeMetaData);

            jsonArray.add(jsonS);
        }
        return Response.ok(MetaDataUtil.formatJson(jsonArray.toString())).build();
    }

    /**
     * 根据类型元数据ID及子表主键ID，查询子表信息，数据只有一条
     * 
     * @param type_meta_data_id
     *            类型元数据ID
     * @param type_meta_data_filed_id
     *            类型元数据子表主键ID
     * @return
     * @author longmingfeng 2017年4月20日 下午1:59:55
     */
    @SuppressWarnings("unchecked")
    @GET
    @Path("{type_meta_data_id}/{type_meta_data_filed_id}.json")
    @Produces({ "application/json" })
    public Response getTypeFileOne(@PathParam("type_meta_data_id") String type_meta_data_id,
        @PathParam("type_meta_data_filed_id") String type_meta_data_filed_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/元数据编号/元数据属性表id.json");

        response.setCharacterEncoding("UTF-8");

        List<String> paramList = new ArrayList<String>();

        JSONArray jsonArray = new JSONArray();

        paramList.add("id::" + type_meta_data_filed_id);
        paramList.add("metaDataId::" + type_meta_data_id);

        List<TypeMetaDataField> list = dao.getDataList(TypeMetaDataField.class, 0, 0, paramList, null, null);

        if (list.size() > 0) {
            jsonArray.add(list);
        }

        return Response.ok(MetaDataUtil.formatJson(jsonArray.toString())).build();
    }

    /**
     * 更新类型元数据主表
     * 
     * @param type_meta_data_id
     * @return
     * @author longmingfeng 2017年4月20日 下午2:11:14
     */
    @GET
    @Path("{type_meta_data_id}.update")
    public Response updataParent(@PathParam("type_meta_data_id") String type_meta_data_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/元数据编号.update");

        response.setCharacterEncoding("UTF-8");

        Map<String, String[]> map = request.getParameterMap();

        List<String> paramList = new ArrayList<String>();
        for (String set : map.keySet()) {
            paramList.add(set + "::" + map.get(set)[0]);// 用双冒号隔开，避免在后面拆分时，与时间类型的冲突
        }

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao util = new CommonUtilDaoImpl(con, log);

        boolean isSuccess = util.update(tableName, primaryKeyName, type_meta_data_id, paramList);

        return Response.ok("更新" + isSuccess).build();
    }

    /**
     * 更新类型元数据属性子表
     * 
     * @param type_meta_data_id
     *            类型元数据ID
     * @param type_meta_data_filed_id
     *            类型元数据子表主键ID
     * @return
     * @author longmingfeng 2017年4月20日 下午2:11:14
     */
    @GET
    @Path("{type_meta_data_id}/{type_meta_data_filed_id}.update")
    public Response updataSon(@PathParam("type_meta_data_id") String type_meta_data_id,
        @PathParam("type_meta_data_filed_id") String type_meta_data_filed_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/元数据编号/元数据属性表id.update");

        response.setCharacterEncoding("UTF-8");

        Map<String, String[]> map = request.getParameterMap();

        List<String> paramList = new ArrayList<String>();
        for (String set : map.keySet()) {
            paramList.add(set + "::" + map.get(set)[0]);// 用双冒号隔开，避免在后面拆分时，与时间类型的冲突
        }

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao util = new CommonUtilDaoImpl(con, log);

        boolean isSuccess = util.update(sonTableName, sonPrimaryKeyName, type_meta_data_filed_id, paramList);

        return Response.ok("更新" + isSuccess).build();
    }

    /**
     * 新增类型元数据主表
     * 
     * @param type_meta_data_id
     * @return
     * @author longmingfeng 2017年4月20日 下午2:11:14
     */
    @GET
    @Path("{type_meta_data_id}.add")
    public Response addParent(@PathParam("type_meta_data_id") String type_meta_data_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/元数据编号.add");

        response.setCharacterEncoding("UTF-8");

        Map<String, String[]> map = request.getParameterMap();

        List<String> paramList = new ArrayList<String>();
        for (String set : map.keySet()) {
            paramList.add(set + "::" + map.get(set)[0]);// 用双冒号隔开，避免在后面拆分时，与时间类型的冲突
        }

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao util = new CommonUtilDaoImpl(con, log);

        boolean isSuccess = util.save(tableName, primaryKeyName, "String", type_meta_data_id, paramList);

        return Response.ok("新增" + isSuccess).build();
    }

    /**
     * 新增类型元数据属性子表
     * 
     * @param type_meta_data_id
     *            类型元数据ID
     * @param type_meta_data_filed_id
     *            类型元数据子表主键ID
     * @return
     * @author longmingfeng 2017年4月20日 下午2:11:14
     */
    @GET
    @Path("{type_meta_data_id}/{type_meta_data_filed_id}.add")
    public Response addSon(@PathParam("type_meta_data_id") String type_meta_data_id,
        @PathParam("type_meta_data_filed_id") String type_meta_data_filed_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/元数据编号/元数据属性表id.add");

        response.setCharacterEncoding("UTF-8");

        Map<String, String[]> map = request.getParameterMap();

        List<String> paramList = new ArrayList<String>();
        paramList.add("metaDataId::" + type_meta_data_id);

        for (String set : map.keySet()) {
            paramList.add(set + "::" + map.get(set)[0]);// 用双冒号隔开，避免在后面拆分时，与时间类型的冲突
        }

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao util = new CommonUtilDaoImpl(con, log);

        boolean isSuccess = util.save(sonTableName, sonPrimaryKeyName, "String", type_meta_data_filed_id, paramList);

        return Response.ok("新增" + isSuccess).build();
    }

    /**
     * 删除类型元数据主表记录
     * 
     * @param type_meta_data_id
     * @return
     * @author longmingfeng 2017年4月20日 下午2:11:14
     */
    @GET
    @Path("{type_meta_data_id}.delete")
    public Response deleteParent(@PathParam("type_meta_data_id") String type_meta_data_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/元数据编号.delete");

        response.setCharacterEncoding("UTF-8");

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao util = new CommonUtilDaoImpl(con, log);

        boolean isSuccess = util.deleteParent(tableName, primaryKeyName, type_meta_data_id.split(","), sonTableName, sonFkName);

        return Response.ok("删除" + isSuccess).build();
    }

    /**
     * 删除类型元数据属性子表
     * 
     * @param type_meta_data_id
     *            类型元数据ID
     * @param type_meta_data_filed_id
     *            类型元数据子表主键ID
     * @return
     * @author longmingfeng 2017年4月20日 下午2:11:14
     */
    @GET
    @Path("{type_meta_data_id}/{type_meta_data_filed_id}.delete")
    public Response deleteSon(@PathParam("type_meta_data_id") String type_meta_data_id,
        @PathParam("type_meta_data_filed_id") String type_meta_data_filed_id) {

        log.log(LogService.LOG_INFO, "格式：http://localhost:8080/resource/元数据编号/元数据属性表id.delete");

        response.setCharacterEncoding("UTF-8");

        Connection con = MetaDataUtil.getConnectionByDatasourcename(dataSourceName, context, log);
        CommonUtilDao util = new CommonUtilDaoImpl(con, log);

        boolean isSuccess = util.deleteSon(sonTableName, sonPrimaryKeyName, type_meta_data_filed_id.split(","), tableName, primaryKeyName, sonFkName,
            type_meta_data_id);

        return Response.ok("删除" + isSuccess).build();
    }

}
