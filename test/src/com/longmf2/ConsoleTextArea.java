/**
 *   @author longmingfeng    2016年12月9日  上午11:06:56
 *   Email: yxlmf@126.com 
 */
package com.longmf2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * 
 * @author longmingfeng 2016年12月9日 上午11:06:56
 */
public class ConsoleTextArea extends JTextArea {
    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 解压并删除jar文件
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
   
    
    public ConsoleTextArea(InputStream[] inStreams) {
        for (int i = 0; i < inStreams.length; ++i)
            startConsoleReaderThread(inStreams[i]);
    } // ConsoleTextArea()

    public ConsoleTextArea() throws IOException {
        //final LoopedStreams ls = new LoopedStreams();
        String path = System.getProperty("user.dir");
        System.out.println(path);
        
        //decompress( path + File.separator + "target.jar", path);
        
        Process ls = Runtime.getRuntime().exec("cmd.exe /c " + path + File.separator + "target.bat");
        // 重定向System.out和System.err
        PrintStream ps = new PrintStream(ls.getOutputStream());
        System.setOut(ps);
        System.setErr(ps);
        startConsoleReaderThread(ls.getInputStream());
    } // ConsoleTextArea()

    private void startConsoleReaderThread(InputStream inStream) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        new Thread(new Runnable() {
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    String s;
                    Document doc = getDocument();
                    while ((s = br.readLine()) != null) {
                        boolean caretAtEnd = false;
                        caretAtEnd = getCaretPosition() == doc.getLength() ? true : false;
                        sb.setLength(0);
                        append(sb.append(s).append('\n').toString());
                        if (caretAtEnd)
                            setCaretPosition(doc.getLength());
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "从BufferedReader读取错误：" + e);
                    System.exit(1);
                }
            }
        }).start();
    } // startConsoleReaderThread()

    // 该类剩余部分的功能是进行测试
    public static void main(String[] args) {
        JFrame f = new JFrame("CMD内容输出测试");
        
        //使窗口居中显示
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        
        //先乘除，后加减，再位移
        int x = ((screen.width-f.getWidth()) >> 1)-100;
        int y = (screen.height-f.getHeight() >> 1)-50;
        f.setLocation(100,100);
        
        ConsoleTextArea consoleTextArea = null;
        try {
            consoleTextArea = new ConsoleTextArea();
        } catch (IOException e) {
            System.err.println("不能创建LoopedStreams：" + e);
            System.exit(1);
        }
        consoleTextArea.setFont(java.awt.Font.decode("monospaced"));
        f.getContentPane().add(new JScrollPane(consoleTextArea), java.awt.BorderLayout.CENTER);
        f.setBounds(100, 100, screen.width/2, screen.height/2);
        f.setVisible(true);
        //f.setBackground(Color.GREEN);
        
        JPanel jp=new JPanel(); //加上这句
        jp.setBackground(Color.BLACK);//加上这句
        f.add(jp);//加上这句
        
        f.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });

        // 启动几个写操作线程向
        // System.out和System.err输出
        startWriterTestThread("写操作线程 #1", System.err, 920, 50);
        startWriterTestThread("写操作线程 #2", System.out, 500, 50);
        startWriterTestThread("写操作线程 #3", System.out, 200, 50);
        startWriterTestThread("写操作线程 #4", System.out, 1000, 50);
        startWriterTestThread("写操作线程 #5", System.err, 850, 50);
    }

    private static void startWriterTestThread(final String name, final PrintStream ps, final int delay, final int count) {
        new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i <= count; ++i) {
                    ps.println("***" + name + ", hello !, i=" + i);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

}
