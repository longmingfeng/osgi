/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：longming feng 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年10月28日  上午10:49:54
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket.client;

import java.net.Socket;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author longmingfeng   2016年10月28日  上午10:49:54
 */
public class ClientActivator implements BundleActivator {

    private Socket socket ;
    
    public void start(BundleContext context) throws Exception {
        ClientMultiple c = new ClientMultiple();
        //String addressPorts[] = c.getAddressPort();
        String addressPorts[] = new String[]{"192.168.11.169","8888" };
        socket = c.getConnection(addressPorts);

        // 启动发送消息的线程和接收消息的线程
        ThreadPrintMessage print = new ThreadPrintMessage(socket);
        ThreadReceiveMessage receive = new ThreadReceiveMessage(socket);
        print.start();
        receive.start();
    }

    public void stop(BundleContext context) throws Exception {
        socket.close();
    }
}
