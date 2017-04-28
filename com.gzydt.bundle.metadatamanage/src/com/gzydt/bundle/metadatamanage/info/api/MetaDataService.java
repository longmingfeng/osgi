package com.gzydt.bundle.metadatamanage.info.api;

import java.util.List;

import com.gzydt.bundle.database.dao.PersistenceDao;

@SuppressWarnings("rawtypes")
/**
 * 元数据管理接口类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public interface MetaDataService extends PersistenceDao {

    /**
     * 获取全部对象
     * 
     * @param c
     *            对象
     * @param pageNum
     *            页数
     * @param pageSize
     *            页总数
     * @return 对象集合
     */
    public <T> List<T> getAll(Class c, int pageNum, int pageSize);

    /**
     * 获取全部对象
     * 
     * @param c
     *            对象
     * @param pageNum
     *            页数
     * @param pageSize
     *            页总数
     * @param isStop
     *            是否停用
     * @param keyword
     *            关键字
     * @return 对象集合
     */
    public <T> List<T> getAll(Class c, int pageNum, int pageSize, int isStop, String keyword);

    /**
     * 获取全部对象不分页
     * 
     * @param c
     *            对象
     * @return 对象集合
     */
    public <T> List<T> getAllNoPage(Class c);

    /**
     * 移除对象
     * 
     * @param c
     *            对象
     * @param id
     *            对象的ID
     */
    public <T> void remove(Class c, String id);

    /**
     * 修改对象
     * 
     * @param t
     *            对象
     */
    public <T> void update(T t);

    /**
     * 获取总数
     * 
     * @return 总数
     */
    public int count(Class c);

    /**
     * 获取总数
     * 
     * @param c
     *            对象
     * @param isStop
     *            是否启用
     * @param keyword
     *            关键字
     * @return 总数
     */
    public int count(Class c, int isStop, String keyword);

    /**
     * 根据ID获取对象
     * 
     * @param c
     *            对象
     * @param id
     *            Id
     * @return 对象
     */
    public <T> T getById(Class c, String id);

    /**
     * 获取基础元数据的字段集合
     * 
     * @param id
     *            元数据ID
     * @return 元数据字段集合
     */
    public List<BaseMetaDataField> getByMetaDataId(String id);

    /**
     * 获取类型元数据的字段集合
     * 
     * @param id
     *            元数据ID
     * @return 元数据字段集合
     */
    public List<TypeMetaDataField> getByTypeMetaDataId(String id);

    /**
     * 获取全部资源元数据
     * 
     * @return 资源元数据
     */
    public List<ResourceMetaData> getAllResource();

    public Relation getRelationByMetaDataId(String metadataId);

}
