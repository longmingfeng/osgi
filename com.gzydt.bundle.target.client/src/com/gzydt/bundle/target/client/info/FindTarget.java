/**
 *   @author longmingfeng    2016-10-27 10:53:27
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.target.client.info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

/**
 * 组件列表详情信息servlet
 * 
 * @author longmingfeng 2016年10月27日 上午9:56:03
 */
public class FindTarget extends HttpServlet {

    private static final long serialVersionUID = 6278860845550969505L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String bundlename = req.getParameter("bundlename");
        String bundlestate = req.getParameter("bundlestate");
        String is_systemBundle = req.getParameter("is_systemBundle");
        String bundle_installTime_start = req.getParameter("bundle_installTime_start") == null || req.getParameter("bundle_installTime_start").equals("") ? "2000-01-01" : req.getParameter("bundle_installTime_start");
        String bundle_installTime_end = req.getParameter("bundle_installTime_end") == null || req.getParameter("bundle_installTime_end").equals("") ? new SimpleDateFormat("yyyy-MM-dd").format(new Date()) : req.getParameter("bundle_installTime_end");
        String id = req.getParameter("id");
        resp.setContentType("text/html");
        resp.setCharacterEncoding("utf-8");
        JSONObject json = new JSONObject();
        CopyOnWriteArrayList<Map<String, String>> list;
        if ((req.getParameter("bundle_installTime_start") == null || req.getParameter("bundle_installTime_start").equals("")) && (req.getParameter("bundle_installTime_end") == null || req.getParameter("bundle_installTime_end").equals(""))) {
            bundle_installTime_start = "";
            bundle_installTime_end = "";
        }
        try {
            list = getBundle(bundlename, bundlestate, is_systemBundle,
                bundle_installTime_start, bundle_installTime_end, id);
            json.put("bundleinfo", list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().println(json);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * 获取组件详细信息
     * 
     * @param bundlename
     *            组件名
     * @param bundlestate
     *            组件状态
     * @param is_systemBundle
     *            是否是系统组件
     * @param bundle_installTime_start
     *            安装开始时间
     * @param bundle_installTime_end
     *            安装结束时间
     * @param id
     *            系统Id
     * @return
     * @author longmingfeng 2016年11月29日 上午10:29:36
     */
    private CopyOnWriteArrayList<Map<String, String>> getBundle(String bundlename, String bundlestate,
        String is_systemBundle, String bundle_installTime_start, String bundle_installTime_end, String id) throws Exception {

        BundleContext m_bundleContext = FrameworkUtil.getBundle(getClass()).getBundleContext();
        Map<String, String> bundlemap;
        CopyOnWriteArrayList<Map<String, String>> list = new CopyOnWriteArrayList<Map<String, String>>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String path;// 每个bundle安装的时间，所记录的文件路径
        BufferedReader br = null;

        for (Bundle bundle : m_bundleContext.getBundles()) {
            bundlemap = new HashMap<String, String>();

            // 获取bundle的相关信息，放入map中
            bundlemap.put("bundlename", bundle.getSymbolicName());
            bundlemap.put("bundlestate", getSate(bundle.getState()));
            bundlemap.put("bundleversion", bundle.getVersion().toString());
            bundlemap.put("descrip", bundle.getHeaders().get(Constants.BUNDLE_DESCRIPTION));
            bundlemap.put("is_systemBundle", bundle.getHeaders().get("Is_SystemBundle") == null ? "true"
                : bundle.getHeaders().get("Is_SystemBundle"));

            // 获取bundle安装的时间所记录的文件路径
            if ("System Bundle".equals(bundle.getLocation())) {
                path = System.getProperty("user.dir") + File.separator + Activator.ROOT + File.separator
                    + Activator.SYS_FILE_NAME;
            } else {
                path = bundle.getDataFile("").getPath() + File.separator + Activator.FILE_NAME;
            }

            // 读取文件，获取组件的安装时间
            try {
                br = new BufferedReader(new FileReader(path));
                bundlemap.put("bundle_installTime", br.readLine());

            } catch (Exception e) {
                bundlemap.put("bundle_installTime", "");
            } finally {
                if (br != null) {
                    br.close();
                }
            }

            list.add(bundlemap);
        }

        String[] bundleNames = getSystemInfo(id);
        if (bundleNames != null && bundleNames.length > 0) {
            CopyOnWriteArrayList<Map<String, String>> list2 = new CopyOnWriteArrayList<Map<String, String>>();
            for (Map<String, String> map : list) {
                if (!"0".equals(id) && id != null && !"".equals(id)) {
                    for (String bundleName : bundleNames) {
                        if (map.get("bundlename").contains(bundleName)) {
                            list2.add(map);
                        }
                    }
                    list = list2;
                } else {
                    for (String bundleName : bundleNames) {
                        if (map.get("bundlename").contains(bundleName)) {
                            list.remove(map);
                        }
                    }
                }
            }
        }

        // 以下为条件过滤...
        if (bundlename != null && !"".equals(bundlename)) {
            for (Map<String, String> map : list) {
                if (!map.get("bundlename").contains(bundlename)) {
                    list.remove(map);
                }
            }
        }

        if (bundlestate != null && !"".equals(bundlestate)) {
            for (Map<String, String> map : list) {
                if (!map.get("bundlestate").equals(bundlestate)) {
                    list.remove(map);
                }
            }
        }

        if (is_systemBundle != null && !"".equals(is_systemBundle)) {
            for (Map<String, String> map : list) {
                if (!map.get("is_systemBundle").equals(is_systemBundle)) {
                    list.remove(map);
                }
            }
        }
        if (bundle_installTime_start != null && !"".equals(bundle_installTime_start) && bundle_installTime_end != null
            && !"".equals(bundle_installTime_end)) {
            for (Map<String, String> map : list) {
                try {
                    if (sdf.parse(map.get("bundle_installTime")).getTime() < sdf.parse(bundle_installTime_start).getTime()
                        || (sdf.parse(map.get("bundle_installTime")).getTime() > sdf.parse(bundle_installTime_end)
                            .getTime())) {
                        list.remove(map);
                    }
                } catch (Exception e) {
                    list.remove(map);
                }
            }
        }

        return list;
    }

    /**
     * 获取组件中文状态
     * 
     * @param state
     * @return
     * @author longmingfeng 2016年12月1日 下午2:38:13
     */
    private String getSate(int state) {
        String sta;
        switch (state) {
            case Bundle.INSTALLED:
                sta = "已安装";
                break;
            case Bundle.RESOLVED:
                sta = "已编译";
                break;
            case Bundle.UNINSTALLED:
                sta = "未安装";
                break;
            case Bundle.STARTING:
                sta = "启动中";
                break;
            case Bundle.ACTIVE:
                sta = "激活";
                break;
            case Bundle.STOPPING:
                sta = "停止中";
                break;
            default:
                sta = "已编译";
        }
        return sta;
    }

    /**
     * 获取系统信息
     * 
     * @param id
     *            系统ID
     * @return
     * @author longmingfeng 2016年12月19日 上午10:25:31
     */
    private String[] getSystemInfo(String id) {
        String[] bundleNames = null;
        Properties props = TargetUtil.readProp("installInfo.properties");
        for (Object key : props.keySet()) {
            if (!"0".equals(id) && id != null && !"".equals(id)) {
                if (key.toString().contains(id + "_bundleName")) {
                    bundleNames = props.getProperty(key.toString()).split(",");
                }
            } else {
                if (key.toString().contains("_bundleName")) {
                    if (props.getProperty(key.toString()) != null && !"".equals(props.getProperty(key.toString())))
                        bundleNames = props.getProperty(key.toString()).split(",");
                }
            }
        }
        return bundleNames;
    }

}
