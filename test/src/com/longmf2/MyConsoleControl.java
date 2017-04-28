/**
 *   @author longmingfeng    2016年12月8日  下午2:02:27
 *   Email: yxlmf@126.com 
 */
package com.longmf2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * 处理控制台显示的信息
 * 
 * @author longmingfeng   2016年12月8日  下午2:02:58
 */
public class MyConsoleControl {

    /**
     * 对cd等切换目录的特殊命令的处理
     * 
     * @param cmdArray
     */
    public void execCdChange(String[] cmdArray) {
        // 获取当前根目录
        String nowRootDir = MyConsoleDto.getMyConsoleDto().getNowRootDir();
        // 当前要切换的目录
        File dir = null;
        // 对cd命令进行特殊处理
        if (cmdArray[2].equals("cd")) {
            // 如果命令数组的长度为4，表明cd命令输入了目录
            if (cmdArray.length == 4) {
                dir = new File(nowRootDir + cmdArray[3]);
            }
        }
        // 切换根驱动器命令
        if (cmdArray[2].length() == 2 && cmdArray[2].charAt(1) == ':') {
            dir = new File(cmdArray[2]);
        }
        // 记录当前目录和上一个目录
        if (dir != null && dir.exists() && dir.isDirectory()) {
            // 将当前目录记录为上一个目录
            MyConsoleDto.getMyConsoleDto().setPrePath(MyConsoleDto.getMyConsoleDto().getNowPath());
            // 将dir记录为当前目录
            MyConsoleDto.getMyConsoleDto().setNowPath(dir.toString());
        }
    }

    /**
     * 保护已打印文本不受编辑
     */
    public void protectHasPrintText(JTextArea consoleTextArea) {
        // 获取已打印的文本
        String hasPrintText = MyConsoleDto.getMyConsoleDto().getStrConsoleText();
        // 读取当前终端文本
        String nowConsoleText = consoleTextArea.getText();
        // 如果当前终端文本的长度小于已打印文本的长度
        if (consoleTextArea.getText().length() < hasPrintText.length()) {
            // 恢复已打印的文本
            consoleTextArea.setText(hasPrintText);
        }
        // 键盘输入的位置不在‘>’字符之后
        if (consoleTextArea.getText().charAt(hasPrintText.length() - 1) != '>') {
            // 恢复已打印的文本
            consoleTextArea.setText(hasPrintText);
        }
    }

    /**
     * 以追加的方式打印当前目录信息到consoleTextArea
     */
    public void printDirInfo(JTextArea consoleTextArea) {
        String nowPath = MyConsoleDto.getMyConsoleDto().getNowPath();
        consoleTextArea.append(nowPath + ">");
        // 记录当前consoleTextArea的文本
        MyConsoleDto.getMyConsoleDto().setStrConsoleText(consoleTextArea.getText());
        // 将光标移动到最后
        consoleTextArea.setCaretPosition(consoleTextArea.getText().length());
    }

    /**
     * 在限制时间内执行cmdArray命令
     * 
     * @param cmdArray
     *            命令数组
     * @param time
     *            时间单位为微秒
     */
    public void execCmdThread(String[] cmdArray, int time, JTextArea consoleTextArea) {

        // 如果输入的命令为exit，则退出整个程序
        if (cmdArray[2].equals("exit")) {
            System.exit(0);
        }

        // 启动新的线程执行一条命令
        Thread cmdThread = new Thread(new Runnable() {
            @Override
            public void run() {
                outputCapture(cmdArray, consoleTextArea);
            }
        });
        cmdThread.start();
        try {
            cmdThread.join(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 对cd等切换目录的特殊命令的处理
        execCdChange(cmdArray);

        // 插入一个换行符
        if (!cmdArray[2].equals("cmd")) {
            consoleTextArea.append("\n");
        }
        // 打印当前目录信息
        printDirInfo(consoleTextArea);
    }

    /**
     * 捕捉cmdArray命令的控制台输出
     * 
     * @param cmdArray
     */
    public void outputCapture(String[] cmdArray, JTextArea consoleTextArea) {
        Process process = null;
        try {
            File dir = new File(MyConsoleDto.getMyConsoleDto().getNowPath());
            // 启动命令行指定程序的新进程
            process = Runtime.getRuntime().exec(cmdArray, null, dir);
        } catch (IOException e) {
            System.err.println("创建进程时出错...\n" + e);
            System.exit(1);
        }

        // 获得新进程所写入的流
        InputStream[] inStreams = new InputStream[] { process.getInputStream(),
            process.getErrorStream() };
        // 将终端信息追加到consoleTextArea
        this.toTextArea(consoleTextArea, inStreams);

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在jTextArea上显示传入的InputStream数组的信息
     * 
     * @param jTextArea
     *            显示信息的文本区域
     * @param inStreams
     *            传入要显示的InputStream数组的信息
     */
    public void toTextArea(JTextArea jTextArea, InputStream[] inStreams) {
        for (int i = 0; i < inStreams.length; ++i) {
            startConsoleReaderThread(jTextArea, inStreams[i]);
        }
    }

    /**
     * 单独启用一个线程对控制台信息的处理
     * 
     * @param jTextArea
     * @param inputStream
     */
    private void startConsoleReaderThread(JTextArea jTextArea,
        InputStream inputStream) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        new Thread(new Runnable() {
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    String s;
                    Document doc = jTextArea.getDocument();
                    while ((s = br.readLine()) != null) {
                        boolean caretAtEnd = false;
                        caretAtEnd = jTextArea.getCaretPosition() == doc.getLength() ? true : false;
                        sb.setLength(0);
                        jTextArea.append(sb.append(s).append('\n').toString());
                        if (caretAtEnd)
                            jTextArea.setCaretPosition(doc.getLength());
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                        "从BufferedReader读取错误：" + e);
                    System.exit(1);
                }
            }
        }).start();
    }
}
