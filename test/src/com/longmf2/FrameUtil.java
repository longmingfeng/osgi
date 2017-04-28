/**
 *   @author longmingfeng    2016年12月8日  下午2:03:45
 *   Email: yxlmf@126.com 
 */
package com.longmf2;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * 使JFrame窗口居中显示
 * 
 * @author longmingfeng   2016年12月8日  下午2:04:05
 */
public class FrameUtil {
    public static void setFrameCenter(JFrame jf){
        //使窗口居中显示
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        
        //先乘除，后加减，再位移
        int x = (screen.width-jf.getWidth()) >> 1;
        int y = (screen.height-jf.getHeight() >> 1)-16;
        jf.setLocation(x,y);
    }
}