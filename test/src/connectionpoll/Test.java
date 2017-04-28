/**
 *   @author longmingfeng    2016年12月14日  上午10:59:23
 *   Email: yxlmf@126.com 
 */
package connectionpoll;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.regex.Pattern;

/**
 * 
 * @author longmingfeng 2016年12月14日 上午10:59:23
 */
public class Test {

    // 正常读取没有任何操作权限的文件
    static void getContent1() {
        try {
            File f = new File("e://te.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String s = "";
            while ((s = br.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static void getContent2() {
        System.setSecurityManager(new SecurityManager());
        AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                try {
                    File f = new File("e://te.txt");
                    BufferedReader br = new BufferedReader(new FileReader(f));
                    String s = "";
                    while ((s = br.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

    }

    public static void main(String[] args) {
        // getContent2();
        // System.out.println(8>>2);
        
        //System.out.println("/room.json".matches("/1\\w+.json1"));

        String pattern = "(/)(\\w){1,}(.json)";
        boolean isMatch = Pattern.matches(pattern, "/room.json");
        System.out.println(isMatch);
        
        pattern = "(/)(\\w){1,}(/)(\\w){1,}(.json)";
        isMatch = Pattern.matches(pattern, "/room/1.json");
        System.out.println(isMatch);
        
        pattern = "(/)(\\w){1,}(-Q)(.json)";
        isMatch = Pattern.matches(pattern, "/room-Q.json");
        System.out.println(isMatch);
        
        
    }

}
