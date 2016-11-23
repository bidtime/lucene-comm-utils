/*
 * 存储类属性和数据库字段的关系
 */
package org.bidtime.lucene.ldbc.sql.xml;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bidtime.lucene.ldbc.sql.xml.parser.ParserSqlXML;
import org.bidtime.lucene.ldbc.sql.xml.parser.TTableProps;
import org.bidtime.utils.basic.PackageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <code>TableFieldXmlsParser</code> is a registry for sets of queries so that multiple
 * copies of the same queries aren't loaded into memory. This implementation
 * loads properties files filled with query name to SQL mappings. This class is
 * thread safe.
 */
public class TableFieldXmlsParser implements ApplicationContextAware {  
    
	private static final Logger logger = LoggerFactory
			.getLogger(TableFieldXmlsParser.class);
    
	private static ApplicationContext ctx;  
    
	/** 
     * 此方法可以把ApplicationContext对象inject到当前类中作为一个静态成员变量。 
     * @param applicationContext ApplicationContext 对象. 
     * @throws BeansException 
     */
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		ctx = ac;
	}
 
    /** 
     * 这是一个便利的方法，帮助我们快速得到一个BEAN 
     * @param beanName bean的名字 
     * @return 返回一个bean对象 
     */  
    public static Object getBean(String name) {  
        return ctx.getBean(name);  
    }

	/**
	 * Maps query set names to Maps of their queries.
	 */
	private final Map<String, TTableProps> queries = new HashMap<String, TTableProps>();

	/**
	 * TableFieldXmlsParser constructor.
	 */
	protected TableFieldXmlsParser() {
		super();
	}

	private String packageRoot;
	private String extName;
	private boolean recursive;
	
	public String getPackageRoot() {
		return packageRoot;
	}

	public void setPackageRoot(String packageRoot) {
		this.packageRoot = packageRoot;
	}

	public String getExtName() {
		return extName;
	}

	public void setExtName(String extName) {
		this.extName = extName;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
	public void init() {
		//loadInit(packageRoot, extName, recursive);
		autoLoadInit(packageRoot, extName, recursive);
	}

	protected void loadInit(String pack, String sSufix, boolean recursive) {
		List<String> list = PackageUtils.getRelationFilesJar(pack, sSufix,
				recursive);
		try {
			for (String s: list) {
				try {
					loadClass(s);
				} catch (IOException e) {
					logger.error("loadInit:", e);
				}
			}
		} catch (Exception e) {
			logger.error("loadInit:", e);
		} finally {
			list = null;
		}
	}

	protected void autoLoadInit(String pack, String sSufix, boolean recursive) {
		List<String> list = PackageUtils.autoGetRelationFilesRootOrJar(pack, sSufix,
				recursive);
		try {
			loadClass(list);
		} catch (Exception e) {
			logger.error("autoLoadInit:", e);
		} finally {
			list = null;
		}
	}

	/**
	 * Loads a Map of query names to SQL values. The Maps are cached so a
	 * subsequent request to load queries from the same path will return the
	 * cached Map.
	 * 
	 * @param path
	 *            The path that the ClassLoader will use to find the file. This
	 *            is <strong>not</strong> a file system path. If you had a
	 *            jarred Queries.properties file in the com.yourcorp.app.jdbc
	 *            package you would pass
	 *            "/com/yourcorp/app/jdbc/Queries.properties" to this method.
	 * @throws IOException
	 *             if a file access error occurs
	 * @throws IllegalArgumentException
	 *             if the ClassLoader can't find a file at the given path.
	 * @return Map of query names to SQL values
	 */
	public synchronized TTableProps load(String path) throws Exception {
		return load(path, path);
	}
	
	/*
	 * 将目录中的所有文件,逐一遍历
	 */
	public void loadClass(List<String> list) {
		try {
			for (String s: list) {
				try {
					loadClass(s);
					Thread.sleep(0L);
				} catch (IOException e) {
					logger.error("loadClass:", e);
				}
			}
		} catch (Exception e) {
			logger.error("loadClass:", e);
		}
	}

	/*
	 * 将/com/eb/business/User/props
	 * 替换成com.eb.business.User
	 * 以方便类中取此sql
	 */
	protected TTableProps loadClass(String path) throws Exception {
		String sPackCom = null;
		char c = path.charAt(0);
		if (c=='/') {
			sPackCom = path.substring(1);
		} else {
			sPackCom = path;
		}
		int nPos = sPackCom.lastIndexOf('.');
		if (nPos > 0) {
			sPackCom = sPackCom.substring(0,nPos);
		}
		sPackCom = sPackCom.replace('/', '.');
		return load(sPackCom, path);
	}

	private TTableProps load(String sKey, String path)
			throws Exception {
		TTableProps queryMap = this.loadMapOfPath(path);
		this.queries.put(sKey, queryMap);
		return queryMap;
	}

	/**
	 * Loads a set of named queries into a Map object. This implementation reads
	 * a properties file at the given path.
	 * 
	 * @param path
	 *            The path that the ClassLoader will use to find the file.
	 * @throws IOException
	 *             if a file access error occurs
	 * @throws IllegalArgumentException
	 *             if the ClassLoader can't find a file at the given path.
	 * @since DbUtils 1.1
	 * @return Map of query names to SQL values
	 */
	protected TTableProps loadMapOfPath(String path) throws Exception {
		// Findbugs flags getClass().getResource as a bad practice; maybe we
		// should change the API?
		// Copy to HashMap for better performance
		TTableProps tp = null;
		try {
			tp = ParserSqlXML.parserTable(this.getClass(), path);
		} catch (Exception e) {
			logger.error("loadMapOfPath:" + path, e);
		}
		return tp;
	}

	/**
	 * Removes the queries for the given path from the cache.
	 * 
	 * @param path
	 *            The path that the queries were loaded from.
	 */
	public synchronized void unload(String path) {
		this.queries.remove(path);
	}

	public TTableProps get(Object o) throws SQLException {
		return get(o.getClass());
	}
	
	@SuppressWarnings("rawtypes")
	public TTableProps get(Class cls) throws SQLException {
		return get(cls.getName());
	}

	public TTableProps get(String path) throws SQLException {
		TTableProps q = this.queries.get(path);
		if (q != null) {
			return q;
		} else {
			logger.error("not found xml file: ", path);
			throw new SQLException("can't load xml file:" + path);
		}
	}

}

