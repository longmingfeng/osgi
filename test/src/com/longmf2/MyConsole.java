/**
 *   @author longmingfeng    2016年12月8日  下午2:01:31
 *   Email: yxlmf@126.com 
 */
package com.longmf2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 命令提示符的外壳程序
 * 
 * @author longmingfeng 2016年12月8日 下午2:02:07
 */
public class MyConsole {

    /**
     * MyConsole主窗体
     */
    private JFrame jf = new JFrame("MyConsole");

    /**
     * MyConsole的终端文本区
     */
    private JTextArea consoleTextArea = new JTextArea();

    /**
     * 将终端文本区加入到JScrollPane
     */
    private JScrollPane jScrollPane = new JScrollPane(consoleTextArea);

    /**
     * 设置默认字体为等宽字体
     */
    private final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 13);

    /**
     * 控制器对象
     */
    private MyConsoleControl myConsoleControl;

    /**
     * 程序入口
     * 
     * @param args
     */
    public static void main(String[] args) {
        new MyConsole();
    }

    public MyConsole() {
        // 初始化UI
        this.setMyConsoleUI();
        // 创建键盘监听
        this.createKeyListener();
        // 获取当前路径
        String nowPath = (new File(".")).getAbsolutePath();
        nowPath = nowPath.substring(0, nowPath.length() - 2);
        // 设置当前路径
        MyConsoleDto.getMyConsoleDto().setNowPath(nowPath);

        // 获取终端控制器
        myConsoleControl = new MyConsoleControl();

        // 读取运行cmd命令的信息
        String[] cmdArray = new String[3];
        cmdArray[0] = "cmd";
        cmdArray[1] = "/c";
        cmdArray[2] = "cmd";
        myConsoleControl.execCmdThread(cmdArray, 300, consoleTextArea);
    }

    private void setMyConsoleUI() {
        // 设置终端文本区的字体和颜色
        consoleTextArea.setBackground(Color.BLACK);
        consoleTextArea.setForeground(Color.LIGHT_GRAY);
        consoleTextArea.setFont(DEFAULT_FONT);
        consoleTextArea.setCaretColor(Color.LIGHT_GRAY);
        // 设置终端文本区自动换行
        consoleTextArea.setLineWrap(true);

        // 垂直滚动条需要时自动出现
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        jf.add(jScrollPane);
        jf.setSize(680, 450);
        FrameUtil.setFrameCenter(jf);
        // 设置窗口的默认关闭操作，退出应用程序
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }

    /**
     * 对consoleTextArea创建键盘监听
     */
    private void createKeyListener() {
        consoleTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyReleased(KeyEvent e) {

                // 保护consoleText已打印文本信息不受编辑
                myConsoleControl.protectHasPrintText(consoleTextArea);

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // 回车键释放时的操作
                    // 获取已打印的文本
                    String hasPrintText = MyConsoleDto.getMyConsoleDto().getStrConsoleText();
                    // 读取当前文本
                    String nowConsoleText = consoleTextArea.getText();
                    // 执行当前输入的命令
                    if (nowConsoleText.length() > hasPrintText.length()) {
                        // 获取当前输入的命令
                        String cmdStr = nowConsoleText.substring(hasPrintText.length()).trim();

                        // 给每条命令加入"cmd /c"的前缀
                        cmdStr = "cmd /c " + cmdStr;
                        // 以空格截取输入命令的cmdArray
                        String[] cmdArray = cmdStr.split(" ");
                        // 执行刚刚输入命令
                        myConsoleControl.execCmdThread(cmdArray, 30000, consoleTextArea);
                    }
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }
        });
    }

}
