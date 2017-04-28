/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:13:12
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.gzydt.bundle.pingnet.socket.util.StreamUtil;

/**
 * 服务器统计在线人数的线程类
 * @author longmingfeng   2016年10月28日  上午10:13:12
 */
public class ThreadTongJi extends Thread{
    
    public ThreadTongJi(){
        File f = new File(System.getProperty("user.dir") + File.separator +"conf" + File.separator + "ip.properties");
        //File f = new File(ThreadTongJi.class.getClass().getResource("").getPath()+"ip.property"); 
        //InputStream inStream = getClass().getResourceAsStream("ip.property");
        //先将文件删除，再创建
        if(f.exists())f.delete();
        try {
            f.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        while(true){
            System.out.println("当前在线的target有：" + 
                    ThreadServer.personCount + "个；在线IP为："+StreamUtil.ipList);
            //每隔5秒钟刷新一次
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
