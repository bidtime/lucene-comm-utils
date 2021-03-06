package org.bidtime.lucene.ldbc.connection;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.bidtime.dbutils.gson.PropAdapt;
import org.bidtime.lucene.ldbc.rs.handler.LuceneSetHandler;
import org.bidtime.lucene.ldbc.sql.xml.LJsonFieldXmlsLoader;
import org.bidtime.lucene.ldbc.sql.xml.parser.TTableProps;
import org.bidtime.lucene.utils.LogTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlLoadUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(SqlLoadUtils.class);
	
	@SuppressWarnings("rawtypes")
	private static Document mapToRow(Map map, TTableProps tp) throws Exception {
		Document doc = null;		//row
		List<Field> listField = tp.mapToFields(map);
		if (listField != null && !listField.isEmpty()) {
			doc = new Document();
			for (Field field: listField) {
				doc.add(field);
			}
		}
		return doc;
	}
	
	@SuppressWarnings("rawtypes")
	public static void insert(Class clazz, Object object) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		insert(clazz, object, tp);
	}
	
	@SuppressWarnings("rawtypes")
	public static void insert(Class clazz, Object object, TTableProps tp) throws Exception {
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Map<String, Object> map = tp.clazzToMap(object, PropAdapt.NOTNULL);
		Document doc = mapToRow(map, tp);
		if (doc != null) {
			DbConnection.insert(doc, tp.getMapEnumWord());
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("insert:" + map + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void insert(Class clazz, List list) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		for (int i=0; i<list.size(); i++) {
			Object object = list.get(i);
			insert(clazz, object, tp);
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("insert:" + list + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void insert(Class clazz, Map map) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Document doc = mapToRow(map, tp);
		if (doc != null) {
			DbConnection.insert(doc, tp.getMapEnumWord());
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("insert:" + map + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void delete(Class clazz, Long value) throws Exception {
		delete(clazz, String.valueOf(value));
	}
	
	@SuppressWarnings("rawtypes")
	public static void delete(Class clazz, Integer value) throws Exception {
		delete(clazz, String.valueOf(value));
	}
	
	@SuppressWarnings("rawtypes")
	public static void delete(Class clazz, String value) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Term termPK = tp.getTermOfValue(value);
		if (termPK == null) {
			throw new Exception("get term error: term is null.");
		}
		DbConnection.delete(tp.getMapEnumWord(), termPK);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("delete:" + value + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void delete(Class clazz, Object object) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Map<String, Object> map = tp.clazzToMap(object, PropAdapt.NOTNULL);
		Term termPK = tp.getTermOfPK(map);
		if (termPK == null) {
			throw new Exception("get term error: term is null.");
		}
		DbConnection.delete(tp.getMapEnumWord(), termPK);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("delete:" + map + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void delete(Class clazz, Map map) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Term termPK = tp.getTermOfPK(map);
		if (termPK == null) {
			throw new Exception("get term error: term is null.");
		}
		DbConnection.delete(tp.getMapEnumWord(), termPK);
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("delete:" + map + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void update(Class clazz, Object object) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Map<String, Object> map = tp.clazzToMap(object, PropAdapt.NOTNULL);
		Term termPK = tp.getTermOfPK(map);
		if (termPK == null) {
			throw new Exception("get term error: term is null.");
		}
		Document doc = mapToRow(map, tp);
		if (doc != null) {
			DbConnection.update(doc, termPK, tp.getMapEnumWord());
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("update:" + map + ", span ", start));
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void update(Class clazz, Map map) throws Exception {
		TTableProps tp = LJsonFieldXmlsLoader.getTableProps(clazz);
		Long start = null;
		if (logger.isDebugEnabled()) {
			start = System.currentTimeMillis();
		}
		Term termPK = tp.getTermOfPK(map);
		if (termPK == null) {
			throw new Exception("get term error: term is null.");
		}
		Document doc = mapToRow(map, tp);
		if (doc != null) {
			DbConnection.update(doc, termPK, tp.getMapEnumWord());
		}
		if (logger.isDebugEnabled()) {
			logger.debug(LogTimeUtil.getFmtDiffNowMs("update:" + map + ", span ", start));
		}
	}
	
	// query
	@SuppressWarnings("rawtypes")
	public static <T> T query(Class clazz, String words, Integer nPageIdx, Integer nPageSize,
			LuceneSetHandler<T> rsh) throws Exception {
		return DbConnection.query(words, nPageIdx, nPageSize, rsh);
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T query(Class clazz, String words, LuceneSetHandler<T> rsh) throws Exception {
		return DbConnection.query(words, null, null, rsh);
	}
	
}