/**
 *   @author longmingfeng    2017年2月20日  上午9:38:16
 *   Email: yxlmf@126.com 
 */
package com;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author longmingfeng   2017年2月20日  上午9:38:16
 */
public class Test {

    /**
     * @param args
     * @author longmingfeng  2017年2月20日  上午9:38:16
     * @throws JSONException 
     */
    public static void main(String[] args) throws JSONException {
        //System.out.println(UUID.randomUUID());
        /*AtomicInteger ato = new AtomicInteger(0);
        System.out.println(ato.getAndDecrement());*/
        
        /*Date d = new Date(1489116090010L);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));*/
        
        /*Map map = new HashMap();
        map.put("name", "张三");
        map.put("age", 28);
        JSONObject json = new JSONObject(map);
        System.out.println(json.get("name"));*/
        
        /*String [] likeParamNameValue = {"a","b"};
        List<String> likeParam = Arrays.asList(likeParamNameValue);
        System.out.println(likeParam.size());*/
        
        /*String[] dateColumn = null;
        dateColumn = new String[]{"a","b"};
        System.out.println(dateColumn.length);
        
        dateColumn = new String[3];
        System.out.println(dateColumn.length);*/
        
        /*List<String> likeParam = new ArrayList<String>();
        for (String string : likeParam) {
            System.out.println(string);
        }*/
      
        JSONArray jaa = new JSONArray();
        jaa.put("张三");
        jaa.put("李四");
        
        JSONObject joo = new JSONObject();
        joo.put("age", 15);
        joo.put("hight", "175cm");
        
        JSONObject jo = new JSONObject();
        jo.put("name", jaa);
        jo.put("person", joo);
        
        JSONArray ja = jo.getJSONArray("name");
        JSONObject jn = jo.getJSONObject("person");
        System.out.println(ja);
        System.out.println(jn);
        
        //将字符串转成JSONArray
        JSONArray j = new JSONArray("[a,b]");
        System.out.println(j);
        String j2 = j.getString(0);
        System.out.println(j2);
    }

}
