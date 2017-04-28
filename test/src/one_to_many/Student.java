/**
 *   @author longmingfeng 2017年3月10日 下午3:41:19
 *   Email: yxlmf@126.com 
 */
package one_to_many;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @author longmingfeng 2017年3月10日 下午3:41:19
 */
@Entity
public class Student {

    @Id
    private int id;

    private String student_name;

    // optional=true：可选，表示此对象可以没有，可以为null；false表示必须存在
    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST }, optional = true)
    @JoinColumn(name = "school_id")
    private School school;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

}
