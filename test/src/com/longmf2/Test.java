/**
 *   @author longmingfeng    2016年12月14日  上午10:59:23
 *   Email: yxlmf@126.com 
 */
package com.longmf2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.felix.webconsole.internal.servlet.Base64;

/**
 * 
 * @author longmingfeng 2016年12月14日 上午10:59:23
 */
@SuppressWarnings("restriction")
public class Test {

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedPassword = md.digest("longmf".getBytes("UTF-8"));
        byte[] pa = Base64.encodeBase64(hashedPassword);
        
        System.out.println(new String(pa));
        //"{sha-256}i7kvLk/o98bQWWd4S0Yl4dk0UK+LTDPtgU8YCJt83hY="
    }

}
