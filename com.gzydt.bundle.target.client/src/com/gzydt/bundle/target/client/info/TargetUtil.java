package com.gzydt.bundle.target.client.info;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

public class TargetUtil {

    /**
     * 读取配置文件
     * 
     * @param file
     *            文件
     * @return 配置文件信息
     */
    public static Properties readProp(String file) {
        Properties props = new Properties();
        File directory = new File(System.getProperty("user.dir") + File.separator + "conf");
        if (!directory.exists())
            directory.mkdir();

        File dir = new File(directory.getPath() + File.separator + file);

        if (!dir.exists())
            return props;

        FileInputStream input = null;
        try {
            input = new FileInputStream(dir);
            props.load(input);
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        } finally {
            closeSilently(input);
        }
        return props;
    }

    /**
     * 写入配置文件
     * 
     * @param file
     *            文件
     * @param data
     *            数据
     */
    public static void writeProp(String file, Map<String, String> data) {
        File directory = new File(System.getProperty("user.dir") + File.separator + "conf");
        if (!directory.exists())
            directory.mkdir();

        File dir = new File(directory.getPath() + File.separator + file);

        FileOutputStream output = null;
        Properties props = null;
        try {
            output = new FileOutputStream(dir);
            props = new Properties();
            for (String key : data.keySet()) {
                props.put(key, data.get(key));
            }
            props.store(output, "");
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            // ignore
        } finally {
            closeSilently(output);
        }
    }

    /**
     * 获取连接状态
     * 
     * @param parent_url
     *            地址
     * @param son_url
     *            子节地址
     * @return 连接状态
     */
    public static String getConnectionState(String parent_url, String son_url) {
        String state;
        try {
            URL m_host = new URL(parent_url);
            URL urls = new URL(m_host, son_url);
            HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setAllowUserInteraction(false);
            conn.setDefaultUseCaches(false);
            conn.setUseCaches(false);
            conn.setConnectTimeout(1000);
            conn.setReadTimeout(1000);

            int responseCode = conn.getResponseCode();

            state = responseCode == HttpServletResponse.SC_OK ? "连接成功" : "连接失败";

        } catch (Exception e) {
            e.printStackTrace();
            state = "连接失败";
        }
        return state;
    }

    /**
     * 读取日志信息
     * 
     * @return 日志信息
     */
    public static List<String> readLog() {
        List<String> logs = new ArrayList<String>();
        File directory = new File(System.getProperty("user.dir") + File.separator + "log");
        if (!directory.exists())
            directory.mkdir();

        File dir = new File(directory.getPath() + File.separator + "installError.log");

        if (!dir.exists())
            return logs;

        BufferedReader read = null;
        try {
            read = new BufferedReader(new FileReader(dir));
            String log = null;
            while ((log = read.readLine()) != null) {
                Date logDate = new Date(Long.parseLong(log.substring(0, log.indexOf("-"))));
                log = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(logDate) + "-" + log.substring(log.indexOf("-") + 1);
                logs.add(log);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(read);
        }
        return logs;
    }

    /**
     * 清空错误日志
     */
    public static void clearLog() {
        File directory = new File(System.getProperty("user.dir") + File.separator + "log");
        if (!directory.exists())
            directory.mkdir();

        File dir = new File(directory.getPath() + File.separator + "installError.log");
        if (!dir.exists())
            return;

        FileWriter input = null;
        try {
            input = new FileWriter(dir);
            input.write("");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(input);
        }
    }

    /**
     * 将数据写入log文件
     * 
     * @param fileName
     *            文件名
     * @param content
     *            写入内容
     */
    public static void writeToLog(String fileName, String content) {
        BufferedWriter fw = null;
        try {
            File directory = new File(System.getProperty("user.dir") + File.separator + "log");
            if (!directory.exists())
                directory.mkdir();
            File file = new File(directory.getPath() + File.separator + fileName);
            if (!file.exists())
                file.createNewFile();
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
            fw.write(content);
            fw.newLine();
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeSilently(fw);
        }
    }

    /**
     * 解决跨域问题
     * 
     * @param server_url
     *            源地址
     * @return 访问的结果
     */
    public static String changeHttp(String server_url) {
        String lines = "";
        StringBuffer buffer = new StringBuffer();

        BufferedReader ins = null;
        HttpURLConnection conn = null;
        try {
            URL host = new URL(server_url);
            conn = (HttpURLConnection) host.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            InputStream in = conn.getInputStream();

            ins = new BufferedReader(new InputStreamReader(in, "utf-8"));

            while ((lines = ins.readLine()) != null) {
                buffer.append(lines);
            }
        } catch (Exception e) {
            return "";
        } finally {
            closeSilently(ins);
            if (conn != null)
                conn.disconnect();
        }
        return buffer.toString();
    }

    /**
     * 解决跨域问题
     * 
     * @param server_url
     *            源地址
     * @param parm
     *            参数
     * @return 访问的结果
     */
    public static String changePostHttp(String server_url, String parm) {
        String lines = "";
        StringBuffer buffer = new StringBuffer();

        BufferedReader ins = null;
        HttpURLConnection conn = null;
        try {
            URL host = new URL(server_url);
            conn = (HttpURLConnection) host.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            DataOutputStream dataout = new DataOutputStream(conn.getOutputStream());
            dataout.writeUTF(parm);
            dataout.flush();
            dataout.close();
            InputStream in = conn.getInputStream();

            ins = new BufferedReader(new InputStreamReader(in, "utf-8"));

            while ((lines = ins.readLine()) != null) {
                buffer.append(lines);
            }
        } catch (Exception e) {
            return "";
        } finally {
            closeSilently(ins);
            if (conn != null)
                conn.disconnect();
        }
        return buffer.toString();
    }

    public static Closeable closeSilently(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException exception) {
            // Ignore...
        }
        return null;
    }
}
