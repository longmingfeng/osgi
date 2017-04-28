/**
 *   @author longmingfeng 2017年4月26日 下午4:43:09
 *   Email: yxlmf@126.com 
 */
package one_to_many;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.log.LogService;

/**
 * 
 * @author longmingfeng 2017年4月26日 下午4:43:09
 */
public class MySessionLog extends AbstractSessionLog implements SessionLog {
    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
        LogService log = context.getService(context.getServiceReference(LogService.class));
        
        /*System.out.println("日志记录: " +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sessionLogEntry.getDate())
            +"---"+sessionLogEntry.getClass()
            +"---"+sessionLogEntry.getThread()
            +"---"+sessionLogEntry.getMessage());*/
        if(sessionLogEntry.hasMessage() || sessionLogEntry.hasException()){
            log.log(LogService.LOG_INFO, "JPA持久化日志： " + sessionLogEntry.getMessage());
        }
        
    }
}
