package org.bidtime.lucene.ldbc.connection.ds;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class DataSourceTransactionHolder {

	protected Map<Integer, TransactionManagerHolder> mapTransHolder = new HashMap<Integer, TransactionManagerHolder>();
	
	public DataSourceTransactionHolder(DataSource dataSource) {
		put(dataSource);
	}
	
	public DataSourceTransactionHolder(DataSource dataSource,
			DefaultTransactionDefinition def) {
		put(dataSource, def);
	}
	
	public DataSourceTransactionHolder(DataSource dataSource, int level) {
		put(dataSource, level);
	}

	public TransactionManagerHolder put(DataSource dataSource) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();// 事务定义类
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		return put(dataSource, def);
	}

	public TransactionManagerHolder put(DataSource dataSource, int level) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();// 事务定义类
		def.setIsolationLevel(level);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		return put(dataSource, def);
	}

	public TransactionManagerHolder put(DataSource dataSource,
			DefaultTransactionDefinition def) {
		Integer hashCode = dataSource.hashCode();
		TransactionManagerHolder h = new TransactionManagerHolder(dataSource,
				def);
		return mapTransHolder.put(hashCode, h);
	}

	public TransactionManagerHolder get(DataSource dataSource) {
		Integer hashCode = dataSource.hashCode();
		return mapTransHolder.get(hashCode);
	}

	public void getPut(DataSource dataSource) {
		Integer hashCode = dataSource.hashCode();
		TransactionManagerHolder h = mapTransHolder.get(hashCode);
		if (h == null) {
			put(dataSource);
		}
	}

	public void getPut(DataSource dataSource, DefaultTransactionDefinition def) {
		Integer hashCode = dataSource.hashCode();
		TransactionManagerHolder h = mapTransHolder.get(hashCode);
		if (h == null) {
			put(dataSource, def);
		}
	}

	public void getPut(DataSource dataSource, int level) {
		Integer hashCode = dataSource.hashCode();
		TransactionManagerHolder h = mapTransHolder.get(hashCode);
		if (h == null) {
			put(dataSource, level);
		}
	}

	public void remove(DataSource dataSource) {
		Integer hashCode = dataSource.hashCode();
		mapTransHolder.remove(hashCode);
	}

	public void commit(DataSource dataSource) {
		TransactionManagerHolder h = get(dataSource);
		if (h != null) {
			h.commit();
		}
	}
	
	public boolean isEmpty() {
		return (mapTransHolder.isEmpty());
	}

	public boolean commit(DataSource dataSource, boolean bRemove) {
		TransactionManagerHolder h = get(dataSource);
		if (h != null) {
			h.commit();
			if (bRemove) {
				remove(dataSource);
			}
		}
		return isEmpty();
	}

	public void rollback(DataSource dataSource) {
		TransactionManagerHolder h = get(dataSource);
		if (h != null) {
			h.rollback();
		}
	}

	public boolean rollback(DataSource dataSource, boolean bRemove) {
		TransactionManagerHolder h = get(dataSource);
		if (h != null) {
			h.rollback();
			if (bRemove) {
				remove(dataSource);
			}
		}
		return isEmpty();
	}
	
	protected volatile static Map<Integer, DataSourceTransactionManager> mapManager = new HashMap<Integer, DataSourceTransactionManager>();
	
	private static DataSourceTransactionManager getOfDataSource(DataSource dataSource) {
		DataSourceTransactionManager m = mapManager.get(dataSource.hashCode());
		if (m == null) {
			synchronized (DataSourceTransactionHolder.class) {
				m = mapManager.get(dataSource.hashCode());
				if (m == null) {
					m = new DataSourceTransactionManager(dataSource);
					mapManager.put(dataSource.hashCode(), m);
				}
			}
		}
		return m;
	}

	/*
	 * inner class TransactionManagerHolder
	 */
	public class TransactionManagerHolder {
		
		DataSource dataSource = null;

		TransactionStatus status = null;
		
		public TransactionManagerHolder(DataSource dataSource) {
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();// 事务定义类
			def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			initial(dataSource, def);
		}

		public TransactionManagerHolder(DataSource dataSource,
				int level) {
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();// 事务定义类
			def.setIsolationLevel(level);
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
			initial(dataSource, def);
		}

		public TransactionManagerHolder(DataSource dataSource,
				DefaultTransactionDefinition def) {
			initial(dataSource, def);
		}

		private void initial(DataSource dataSource,
				DefaultTransactionDefinition def) {
			this.dataSource = dataSource;
			DataSourceTransactionManager tran = getOfDataSource(dataSource);
			status = tran.getTransaction(def);// 返回事务对象
		}

		public void commit() {
			DataSourceTransactionManager tran = getOfDataSource(dataSource);
			tran.commit(status);
		}

		public void rollback() {
			DataSourceTransactionManager tran = getOfDataSource(dataSource);
			tran.rollback(status);
		}
	}

}
