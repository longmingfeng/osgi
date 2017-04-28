/**
 *   Email: yxlmf@126.com 
 */
package com.gzydt.bundle.database.vo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "test2")
public class JpaBridgeManager2 {

    @Id
    @TableGenerator(name = "test2", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "test2")
    private Integer id;

    @Column(length = 128, name = "rule_name")
    private String ruleName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    private Date createTime;

    public JpaBridgeManager2() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
