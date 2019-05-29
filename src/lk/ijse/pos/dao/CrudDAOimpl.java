package lk.ijse.pos.dao;

import lk.ijse.pos.entity.SuperEntity;
import org.hibernate.Session;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class CrudDAOimpl<T extends SuperEntity,ID extends Serializable> implements CrudDAO<T,ID>{

    protected Session session;
    private Class<T> entity;

    public CrudDAOimpl(){
       entity =(Class<T>) ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void setSession(Session session){
        this.session=session;
    }

    public void save(T entity) throws Exception{
        session.save(entity);
    }
    public void update(T entity) throws Exception{
        session.update(entity);
    }
    public void delete(ID entityId)throws Exception{
        session.delete(session.load(entity,entityId));
    }
    public List<T> findAll()throws Exception{
       return session.createQuery("FROM " +entity.getName()).list();
    }
    public T find(ID entityId)throws Exception{
        return session.find(entity,entityId);
    }

}
