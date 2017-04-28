/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:09:31
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.gzydt.bundle.pingnet.socket.util.StreamUtil;

/**
 * 多用户的客户端
 * @author longmingfeng 2016年10月28日 上午10:09:31
 */
public class ClientMultiple {
    /**
     * 获取服务器的ip地址和端口号
     * 
     * @return
     */
    public static String[] getAddressPort() {
        System.out.println("请输入服务器的信息，格式：127.0.0.1:8888");
        BufferedReader key = StreamUtil.getkey();
        String[] addressPorts = null;
        try {
            String addressPort = key.readLine();
            if (addressPort.equals("") || addressPort.length() == 0) {
                System.out.println("请不要输入空的ip地址和端口号：");
                addressPort = key.readLine();
            }

            // 将ip地址和端口号进行拆分
            addressPort= "192.168.11.169:8888";//便于测试，先写死
            addressPorts = addressPort.split(":");

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return addressPorts;
    }

    /**
     * 获取连接的方法
     * 
     * @param addressPorts
     * @return
     */
    public static Socket getConnection(String[] addressPorts) {
        // 将相关的ip地址和端口号传入到Socket对象中
        System.out.println("用户正在连接服务器....");
        Socket socket = null;
        try {
            socket = new Socket(addressPorts[0], Integer.parseInt(addressPorts[1]));
            System.out.println("恭喜您，成功连接服务器....");
        }
        catch (NumberFormatException e) {
            System.out.println("请输入正确端口号：");
            throw new NumberFormatException("请输入正确端口号！");
        }
        catch (UnknownHostException e) {
            System.out.println("请输入正确ip地址：");
            throw new NumberFormatException("请输入正确ip地址！");
        }
        catch (IOException e) {
            System.out.println("输入输出有问题：");
            throw new NumberFormatException("输入输出有问题！");
        }
        return socket;
    }

    /*public static void main(String[] args) throws IOException {
        String addressPorts[] = getAddressPort();
        Socket socket = getConnection(addressPorts);

        // 启动发送消息的线程和接收消息的线程
        ThreadPrintMessage print = new ThreadPrintMessage(socket);
        ThreadReceiveMessage receive = new ThreadReceiveMessage(socket);
        print.start();
        receive.start();

    }*/
}
