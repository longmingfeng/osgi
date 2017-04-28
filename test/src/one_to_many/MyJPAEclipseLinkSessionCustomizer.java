/**
 *   @author longmingfeng 2017年4月26日 下午4:45:36
 *   Email: yxlmf@126.com 
 */
package one_to_many;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * 
 * @author longmingfeng 2017年4月26日 下午4:45:36
 */
public class MyJPAEclipseLinkSessionCustomizer implements SessionCustomizer {
    public void customize(Session aSession) throws Exception {

        SessionLog aCustomLogger = new MySessionLog();
        aCustomLogger.setLevel(SessionLog.FINE);
        aSession.setSessionLog(aCustomLogger);
    }
}
