package com.gzydt.bundle.database.dao;

import java.util.List;

/**
 * 持久化接口
 * 
 * @author longmingfeng 2016年12月28日 上午10:28:43
 * @param <T>
 */
public interface PersistenceDao {

    /**
     * 新增数据
     * 
     * @param vo
     *            实体对象
     * @return 新增的实体对象
     * @author longmingfeng 2017年1月20日 上午11:00:31
     * @param <T>
     */
    public <T> T save(T vo);

    /**
     * 更新数据
     * 
     * @param vo
     *            实体对象
     * @author longmingfeng 2017年1月20日 上午11:01:15
     * @param <T>
     */
    public <T> void update(T vo);

    /**
     * 根据主键ID查询某条数据
     * 
     * @param c
     *            实体对象.class
     * @param id
     *            主键ID(此处不能写String id,或int id，因为jpa在持久华查询entityManager.find(Class c, Object id)方法时，如果类型不匹配，会出错)
     * @return class的当前实体对象
     * @author longmingfeng 2017年1月20日 上午11:01:40
     */
    @SuppressWarnings("rawtypes")
    public <T> T getVOById(Class c, Object id);

    /**
     * 分页查找数据（当pageStart==0且num==0,则为查询全部数据）
     * 
     * @param c
     *            实体对象.class
     * @param pageStart
     *            第几页
     * @param num
     *            每页记录数
     * @return
     * @author longmingfeng 2017年1月20日 上午11:05:14
     * @param <T>
     */
    @SuppressWarnings("rawtypes")
    public <T> List<T> getList(Class c, int pageStart, int num);

    /**
     * 加查询条件，分页查找数据（当pageStart==0且num==0,则为查询全部数据）
     * 
     * @param c
     *            实体对象.class
     * @param pageStart
     *            第几页
     * @param num
     *            每页显示几条
     * @param column
     *            表列名，表列名值（以"key::value"形式存储）
     * @param dateParam
     *            时间字段名（数据库为DATE,TIME类型），如果没有，传null。字段名::开始时间::结束时间（timename::stime::etime）)
     * @param likeParam
     *            模糊查询的字段名::模糊查询的字段值（以"key::value"形式存储），如果没有，传null
     * @author longmingfeng 2017年2月16日 上午11:15:33
     */
    @SuppressWarnings({ "rawtypes" })
    public List getDataList(Class c, int pageStart, int num, List<String> column, 
        List<String> dateParam,List<String> likeParam);

    /**
     * 根据主键集合，删除一系列数据
     * 
     * @param c
     *            删除的实体对象的class
     * @param ids
     *            主键集合(此处不能写String id,或int id，因为jpa在持久华查询entityManager.find(Class c, Object id)方法时，如果类型不匹配，会出错)
     * @author longmingfeng 2017年1月20日 上午11:06:37
     */
    @SuppressWarnings("rawtypes")
    public void delete(Class c, Object[] ids);

}
