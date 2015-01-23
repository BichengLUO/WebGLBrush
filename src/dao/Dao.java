/**
 * 
 */
package dao;

import java.util.List;

import bean.BaseBean;

/**
 * @author Windsor
 * 
 */
public interface Dao<T extends BaseBean> {
	public T find(Class<T> clazz, int id);

	public void create(T bean) throws Exception;

	public void save(T bean);

	public void delete(T bean);

	public List<T> list(String hql) throws Exception;

	public int getTotalCount(String hql, Object... params);

	public List<T> list(String hql, int firstResult, int maxSize,
			Object... params) throws Exception;
}
