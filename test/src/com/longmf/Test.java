/**
 *   @author longmingfeng    2016年11月29日  上午10:44:13
 *   Email: yxlmf@126.com 
 */
package com.longmf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @author longmingfeng 2016年11月29日 上午10:44:13
 */
public class Test {

    /**
     * 解压jar文件
     */
    public static synchronized void decompress(String fileName, String outputPath) {

        if (!outputPath.endsWith(File.separator)) {
            outputPath += File.separator;
        }
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        JarFile jf = null;
        try {
            jf = new JarFile(fileName);
            for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
                JarEntry je = (JarEntry) e.nextElement();
                String outFileName = outputPath + je.getName();
                File f = new File(outFileName);
                if (je.isDirectory()) {
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                } else {
                    File pf = f.getParentFile();
                    if (!pf.exists()) {
                        pf.mkdirs();
                    }
                    InputStream in = jf.getInputStream(je);
                    OutputStream out = new FileOutputStream(f);
                    byte[] buffer = new byte[2048];
                    int nBytes = 0;
                    while ((nBytes = in.read(buffer)) > 0) {
                        out.write(buffer, 0, nBytes);
                    }
                    out.flush();
                    out.close();
                    in.close();
                }
            }
        } catch (Exception e) {
            System.out.println("解压" + fileName + "出错---" + e.getMessage());
        } finally {
            if (jf != null) {
                try {
                    jf.close();
                    File jar = new File(jf.getName());
                    if (jar.exists()) {
                        // jar.delete();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param args
     * @author longmingfeng 2016年11月29日 上午10:44:13
     */
    public static void main(String[] args) {

        String path = System.getProperty("user.dir");
        // 如果当前JAR包所在的目录有target.bat这个文件，就当已经解压过了，不再重复解压，否则解压jar包

        /*File f = new File(path);
        List<String> file = Arrays.asList(f.list());
        if (!file.contains("bin") && !file.contains("bundle")
            && !file.contains("com") && !file.contains("conf")
            && !file.contains("load") && !file.contains("META-INF")
            && !file.contains("target.bat")) {
            decompress(path + File.separator + "target.jar", path);
        }*/

        // 方式一
        try {

            Runtime.getRuntime().exec("cmd.exe /c start " + "\"\" \"" + path + File.separator + "target.bat\"");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 方式二
        //ConsoleGUI.main(null);

    }

}
