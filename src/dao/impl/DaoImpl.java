/**
 * 
 */
package dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import bean.BaseBean;

import dao.Dao;
import dao.HibernateUtil;

/**
 * @author Windsor
 * 
 */
public class DaoImpl<T extends BaseBean> implements Dao<T> {

	@Override
	public T find(Class<T> clazz, int id) {
		Session session = HibernateUtil.currentSession();
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			T result = (T) session.get(clazz, id);
			return result;
		} finally {
			session.getTransaction().commit();
			HibernateUtil.closeSession();
		}
	}

	@Override
	public void create(T bean) throws Exception {
		Session session = HibernateUtil.currentSession();
		try {
			session.beginTransaction();
			session.persist(bean);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		} finally {
			HibernateUtil.closeSession();
		}
	}

	@Override
	public void save(T bean) {
		Session session = HibernateUtil.currentSession();
		try {
			session.beginTransaction();
			session.update(bean);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession();
		}
	}

	@Override
	public void delete(T bean) {
		Session session = HibernateUtil.currentSession();
		try {
			session.beginTransaction();
			session.delete(bean);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession();
		}
	}

	@Override
	public List<T> list(String hql) throws Exception {
		Session session = HibernateUtil.currentSession();
		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<T> list = (List<T>) session.createQuery(hql).list();
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			session.getTransaction().commit();
			HibernateUtil.closeSession();
		}
	}

	@Override
	public int getTotalCount(String hql, Object... params) {
		Session session = HibernateUtil.currentSession();
		try {
			session.beginTransaction();
			Query query = session.createQuery(hql);
			for (int i = 0; params != null && i < params.length; i++) {
				query.setParameter(i + 1, params[i]);
			}
			Object obj = query.uniqueResult();
			return ((Long) obj).intValue();
		} finally {
			session.getTransaction().commit();
			HibernateUtil.closeSession();
		}
	}

	@Override
	public List<T> list(String hql, int firstResult, int maxSize,
			Object... params) throws Exception {
		Session session = HibernateUtil.currentSession();
		try {
			session.beginTransaction();
			Query query = session.createQuery(hql);
			for (int i = 0; params != null && i < params.length; i++) {
				query.setParameter(i + 1, params[i]);
			}
			@SuppressWarnings("unchecked")
			List<T> list = query.setFirstResult(firstResult)
					.setMaxResults(maxSize).list();
			return list;
		} catch (Exception e) {
			throw e;
		} finally {
			session.getTransaction().commit();
			HibernateUtil.closeSession();
		}
	}

}
