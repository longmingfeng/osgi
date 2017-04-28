/**
 *   copyright@ Guangzhou Yuyang Digital Technology. All Rights Reserved. 粤ICP备12038777号-1
 *   本代码版权归作者：宇阳数码 所有，且受到相关的法律保护。
 *   没有经过版权所有者的书面同意，
 *   任何其他个人或组织均不得以任何形式将本文件或本文件的部分代码用于其他商业用途。
 *   @author longmingfeng    2016年11月7日  下午6:29:04
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.pingnet.socket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * 
 * @author longmingfeng   2016年11月7日  下午6:29:04
 */
public class MetatypeUtil {
    
    /**
     * 获取metatype.xml文件中ad标签的默认值
     * @param bundleContext   bundle下下文对象
     * @param ocd_id  ocd标签ID
     * @return map(key为ad标签ID，value为ad标签默认值)
     * @author longmingfeng  2016年11月8日  上午9:35:37
     */
    public static Map<String ,Object> getValue(BundleContext bundleContext ,String  ocd_id){
        
        System.out.println("开始处理metatype类型的xml文件...");
        
        Map<String ,Object> map = new HashMap<String ,Object>();
        
        ServiceReference metatypeRef = bundleContext.getServiceReference(MetaTypeService.class.getName());
        
        MetaTypeService service = (MetaTypeService) bundleContext.getService(metatypeRef);

        MetaTypeInformation information = service.getMetaTypeInformation(bundleContext.getBundle());
        
        ObjectClassDefinition ocd = information.getObjectClassDefinition(ocd_id, "");
        
        AttributeDefinition[] attributes = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
        
        for (AttributeDefinition attribute : attributes) {
            
            map.put(attribute.getID(), attribute.getDefaultValue()[0]);
            //System.out.println("-----------id:"+attribute.getID()+",default value: " + attribute.getDefaultValue()[0]);
        }

        
        //将metatype.xml文件中的属性，写到target运行根目录下的conf目录
        /*FileOutputStream oFile = null;
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "conf");
            if(!file.exists()) file.mkdir();
            
            File cfgFile = new File(file.getPath() + File.separator + ocd_id + ".cfg");
            
            Properties properties = new Properties();
            
            if(!cfgFile.exists()){//配置文件不存在，就创建，并写入相应配置属性
                cfgFile.createNewFile();
            
                oFile = new FileOutputStream(cfgFile.getPath(), false);// true表示追加打开
                
                properties.putAll(map);
                properties.store(oFile, ocd_id + " conf file , author : yxlmf@126.com");
                
                oFile.close();
                
            }else{//存在，就读取conf目录下，对应的cfg文件，不再从metatype.xml中去读取了
                properties.load(new FileInputStream(cfgFile));
                
                map.clear();
                
                for (Object key : properties.keySet()) {
                    map.put(key.toString(), properties.getProperty((String) key));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }*/
        
        return map;
    }

}
