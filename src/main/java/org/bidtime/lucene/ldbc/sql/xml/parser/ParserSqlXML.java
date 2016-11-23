package org.bidtime.lucene.ldbc.sql.xml.parser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserSqlXML {

	private static final Logger logger = LoggerFactory
			.getLogger(ParserSqlXML.class);

	public ParserSqlXML() {
	}
	
	private static void elementToColumnPro(Element e, ColumnPro p) throws Exception {
		p.setName(ElementUtils.getValue(e, "name", String.class));
		p.setColumn(ElementUtils.getValue(e, "column", String.class));
		String dataType = ElementUtils.getValue(e, "type", String.class);
		p.setDataType(dataType);
		if (p.getDataType()==null) {
			throw new Exception("data type is null.");
		}
		p.setLength(ElementUtils.getValue(e, "length", Integer.class));
		p.setGenerator(ElementUtils.getValue(e, "generator", String.class));
		//
		Boolean notNull = ElementUtils.getValue(e, "not-null", Boolean.class, false);
		p.setNotNull(notNull);
		if (p.getNotNull()) {
			Boolean bDefault = ElementUtils.getValue(e, "default", Boolean.class, false);
			p.setDefaultValue(bDefault);
		}
		//ext
		Boolean bStore = ElementUtils.getValue(e, "store", Boolean.class, false);
		p.setStore(bStore);
		String index = ElementUtils.getValue(e, "index", String.class, "NONE");
		p.setIndex(index);
		Boolean bTokenized = ElementUtils.getValue(e, "tokenized", Boolean.class, false);
		p.setTokenized(bTokenized);
		Boolean bOmitNorms = ElementUtils.getValue(e, "omitNorms", Boolean.class, false);
		p.setOmitNorms(bOmitNorms);
		//word...
		p.setWordType(ElementUtils.getValue(e, "wordType", String.class));
		if (p.isPinyin()) {
			p.setWordName(ElementUtils.getValue(e, "wordName", String.class));
		}
		//
		p.doFinished();
	}

	@SuppressWarnings("rawtypes")
	private static void visitClassElementChild(Element pElement, TTableProps tp) throws Exception {
		List<String> listPkField = new ArrayList<>();
		for (Iterator iter1 = pElement.elementIterator(); iter1.hasNext();) {
			Element e = (Element) iter1.next();
			if (e.getName().trim().equalsIgnoreCase("id")) {
				ColumnPro p = new ColumnPro(true);
				elementToColumnPro(e, p);
				listPkField.add(p.getColumn());
				tp.addColsPkPro(p.getName(), p);
			} else if (e.getName().trim().equalsIgnoreCase("property")) {
				ColumnPro p = new ColumnPro(false);
				elementToColumnPro(e, p);
				tp.addColsCommonPro(p.getName(), p);
			}
		}
		tp.setFieldPK((String[])listPkField.toArray(new String[listPkField.size()]));
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static void visitSqlIdCol(Element pele, SqlHeadCountPro p) {
		for (Iterator it1 = pele.elementIterator(); it1.hasNext();) {
			Element pe = (Element) it1.next();
			if (pe.getName().equalsIgnoreCase("cols")) {
				for (Iterator iter1 = pe.elementIterator(); iter1.hasNext();) {
					Element e = (Element) iter1.next();
					if (e.getName().equalsIgnoreCase("col")) {
						Attribute atName = e.attribute("name");
						if (atName != null) {
							Attribute atProp = e.attribute("prop");
							String prop = null;
							if (atProp != null) {
								prop = atProp.getText();
							}
							p.setCol(atName.getValue(), e.getText(), prop);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private static void visitSqlElementChild_tp(Element pElement, TTableProps tp)
			throws DocumentException {
		for (Iterator iter1 = pElement.elementIterator(); iter1.hasNext();) {
			Element e = (Element) iter1.next();
			if (StringUtils.equalsIgnoreCase(e.getName(), "id")) {
				// attribute name
				Attribute attrClassName = e.attribute("name");
				if (attrClassName == null
						|| StringUtils.isEmpty(attrClassName.getValue())) {
					throw new DocumentException("sql id is null");
				}
				if (StringUtils.isEmpty(e.getText())) {
					throw new DocumentException("sql context is null");
				}
				String id = attrClassName.getValue();
				String sql = e.getText();
				SqlHeadCountPro p = new SqlHeadCountPro();
				p.setId(id);
				p.setSql(sql);
				// attribute type
				p.setType(ElementUtils.getValue(e, "type", String.class));
				visitSqlIdCol(e, p);
				tp.addSqlHeadPro(p);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static TTableProps parserTable(Class cls, String path) throws Exception {
		SAXReader saxReader = new SAXReader();
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(path);
		InputStream is = null;
		TTableProps tp = null;
		try {
			is = url.openStream();
			Document document = saxReader.read(is);
			Element root = document.getRootElement();
			tp = new TTableProps();
			//class
			Element elementClass = root.element("class");
			if (elementClass != null) {
				Attribute attrClassName = elementClass.attribute("name");
				Attribute attrTableName = elementClass.attribute("table");
				if (attrClassName == null || attrClassName.getValue() == null
						|| attrClassName.getValue().trim().equalsIgnoreCase("")) {
					throw new DocumentException("class name is null");
				}
				if (attrTableName == null || attrTableName.getValue() == null
						|| attrTableName.getValue().trim().equalsIgnoreCase("")) {
					throw new DocumentException("table name is null");
				}
				tp.setTableName(attrTableName.getValue());
				tp.setClassName(attrClassName.getValue());
				visitClassElementChild(elementClass, tp);
			}
			//sql
			Element elementSql = root.element("sql");
			if (elementSql != null) {
				visitSqlElementChild_tp(elementSql, tp);
			}
			tp.doFinished();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				logger.error("parserTables->" + path + ":", e);
			}				
		}
		return tp;
	}
	
}
