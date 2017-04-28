/**
 *   @author longmingfeng 2017年3月10日 下午3:36:10
 *   Email: yxlmf@126.com 
 */
package one_to_many;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 
 * @author longmingfeng 2017年3月10日 下午3:36:10
 */
@Entity
public class School {
    @Id
    private int id;

    @Column(name="school_name",columnDefinition="varchar(200) COMMENT '学校名称'",length=100)
    private String school_name;

    // @OneToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE})
    // mappedBy="school": 指明school类为双向关系维护端，负责外键的更新
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "school")
    private Set<Student> studentSet = new HashSet<Student>();

    public void addStudent(Student student) {

        if (!studentSet.contains(student)){
            student.setSchool(this);
            studentSet.add(student);
        }
    }

    public void deleteStudent(Student student) {

        if (studentSet.contains(student)){
            student.setSchool(null);
            studentSet.remove(student);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public Set<Student> getStudent() {
        return studentSet;
    }

    public void setStudent(Set<Student> studentSet) {
        this.studentSet = studentSet;
    }

}
