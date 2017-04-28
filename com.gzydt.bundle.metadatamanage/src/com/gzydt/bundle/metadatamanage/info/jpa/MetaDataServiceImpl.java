package com.gzydt.bundle.metadatamanage.info.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.amdatu.jta.Transactional;

import com.gzydt.bundle.database.dao.impl.PersistenceDaoImpl;
import com.gzydt.bundle.metadatamanage.info.api.BaseMetaDataField;
import com.gzydt.bundle.metadatamanage.info.api.MetaDataService;
import com.gzydt.bundle.metadatamanage.info.api.Relation;
import com.gzydt.bundle.metadatamanage.info.api.ResourceMetaData;
import com.gzydt.bundle.metadatamanage.info.api.TypeMetaDataField;

@Transactional
/**
 * 元数据管理实现类
 * 
 * @author linliangxu 2017年03月22日 上午9:56:03
 */
public class MetaDataServiceImpl extends PersistenceDaoImpl implements MetaDataService {

    private volatile EntityManager entityManager;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> List<T> getAll(Class c, int pageNum, int pageSize) {
        Query query = entityManager.createQuery("select t from " + c.getName() + " t");
        query.setFirstResult((pageNum - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<T> list = query.getResultList();
        return list;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <T> List<T> getAll(Class c, int pageNum, int pageSize, int isStop, String keyword) {
        String sql = "select t from " + c.getName() + " t where 1=1";
        if (isStop != -1) {
            sql += " and t.state=:state ";
        }
        if (!keyword.equals("")) {
            sql += " and (t.id like :keyword or t.name like :keyword or t.content like :keyword)";
        }
        Query query = entityManager.createQuery(sql);
        query.setFirstResult((pageNum - 1) * pageSize);
        query.setMaxResults(pageSize);
        if (isStop != -1) {
            query.setParameter("state", isStop);
        }
        if (!keyword.equals("")) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        List<T> list = query.getResultList();
        return list;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int count(Class c) {
        Query query = entityManager.createQuery("select t from " + c.getName() + " t");
        return query.getResultList().size();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int count(Class c, int isStop, String keyword) {
        String sql = "select t from " + c.getName() + " t where 1=1";
        if (isStop != -1) {
            sql += " and t.state=:state ";
        }
        if (!keyword.equals("")) {
            sql += " and (t.id like :keyword or t.name like :keyword or t.content like :keyword)";
        }
        Query query = entityManager.createQuery(sql);
        if (isStop != -1) {
            query.setParameter("state", isStop);
        }
        if (!keyword.equals("")) {
            query.setParameter("keyword", "%" + keyword + "%");
        }
        return query.getResultList().size();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public <T> void remove(Class c, String id) {
        entityManager.remove(getById(c, id));
    }

    @Override
    public <T> void update(T vo) {
        entityManager.merge(vo);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T getById(Class c, String id) {
        return (T) entityManager.find(c, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BaseMetaDataField> getByMetaDataId(String id) {
        List<BaseMetaDataField> metaDataFields = new ArrayList<>();
        Query query = entityManager.createQuery("select t from BaseMetaDataField t where t.metaDataId=:metaDataId order by t.indexs");
        query.setParameter("metaDataId", id);
        metaDataFields = query.getResultList();
        return metaDataFields;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TypeMetaDataField> getByTypeMetaDataId(String id) {
        List<TypeMetaDataField> metaDataFields = new ArrayList<>();
        Query query = entityManager.createQuery("select t from TypeMetaDataField t where t.metaDataId=:metaDataId order by t.indexs");
        query.setParameter("metaDataId", id);
        metaDataFields = query.getResultList();
        return metaDataFields;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ResourceMetaData> getAllResource() {
        Query query = entityManager.createQuery("select t from ResourceMetaData t");
        List<ResourceMetaData> list = query.getResultList();
        return list;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> List<T> getAllNoPage(Class c) {
        Query query = entityManager.createQuery("select t from " + c.getName() + " t");
        List<T> list = query.getResultList();
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Relation getRelationByMetaDataId(String metadataId) {
        Query query = entityManager.createQuery("select t from Relation t where t.metadataA=:metadataA");
        query.setParameter("metadataA", metadataId);
        List<Relation> list = query.getResultList();
        for (Relation relation : list) {
            return relation;
        }
        return null;
    }

}
