/**
 *   @author longmingfeng 2017年3月10日 下午4:00:57
 *   Email: yxlmf@126.com 
 */
package one_to_many;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author longmingfeng 2017年3月10日 下午4:00:57
 */
public class ManyToManyServlet extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    private volatile TestDao dao;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if("/add".equals(pathInfo)){
            
            Student student = new Student();
            student.setId(1);
            student.setStudent_name("张三");
            
            Student student2 = new Student();
            student2.setId(2);
            student2.setStudent_name("李四");
            
            School school = new School();
            school.setId(1);
            school.setSchool_name("广州十三中学");
            
            school.addStudent(student);
            school.addStudent(student2);
            
            dao.save(school);
            
        }else if("/update".equals(pathInfo)){
            School sch = dao.getVOById(School.class, 1);
            sch.setSchool_name("广州师大");
            dao.update(sch);
        }else if("/delete".equals(pathInfo)){
            
            //dao.delete(School.class, new Object[]{1});
            dao.delete(Student.class, new Object[]{2});
            
        }else if("/select".equals(pathInfo)){
            School sch = dao.getVOById(School.class, 1);
            System.out.println(sch.getSchool_name()+"============");
        }
        
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
