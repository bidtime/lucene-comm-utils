package org.bidtime.lucene.base;

import org.bidtime.lucene.base.create.LuceneCreate;
import org.bidtime.lucene.base.search.AbstractIndexSearch;


public class LuceneIndex {
	
//	private static final Logger logger = LoggerFactory
//			.getLogger(LuceneIndex.class);

	protected LuceneCreate indexCreate;
	protected AbstractIndexSearch indexSearch;
	
	public LuceneIndex() {
	}

	public LuceneCreate getIndexCreate() {
		return indexCreate;
	}

	public void setIndexCreate(LuceneCreate indexCreate) {
		this.indexCreate = indexCreate;
	}

	public AbstractIndexSearch getIndexSearch() {
		return indexSearch;
	}

	public void setIndexSearch(AbstractIndexSearch indexSearch) {
		this.indexSearch = indexSearch;
	}
	
//	public void createIndex(Object d) throws Exception {
//		this.indexCreate.createIndex(d);
//	}
//	
//	public void deleteIndex(Object pkVal) throws Exception {
//		this.indexCreate.deleteIndex(pkVal);
//	}
//	
//	public void deleteIndex(Object[] pkVal) throws Exception {
//		this.indexCreate.deleteIndex(pkVal);
//	}
//	
//	public void updateIndex(Object d) throws Exception {
//		this.indexCreate.updateIndex(d);
//	}
//	
//	public void updateNumericDocValue(Object pkVal, Object fld,
//			Object val) throws Exception {
//		this.indexCreate.updateNumericDocValue(pkVal, fld,
//			val);
//	}
//	
//	public void updateNumericDocValue(Object d) throws Exception {
//		this.indexCreate.updateNumericDocValue(d);
//	}
	
//	@SuppressWarnings("deprecation")
//	public void initial() throws Exception {
//		initialIndexDir();
//		if (analyzer == null) {
//			analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
//		}
//		indexCreate = new LuceneCreate(indexDir, analyzer, openMode);
//		//
//		indexCreate.initial(headMagt);
//		indexSearch = new LuceneSearch(indexDir, analyzer);
//		indexSearch.initial();
//	}
	
//	private boolean isHitExtName(String s) {
//		if (StringUtils.isEmpty(this.extName)) {
//			return true;
//		} else {
//			return s.endsWith(this.extName);
//		}
//	}
	
//	protected void doFileNotExists(String s) throws Exception {
//		logger.info("createDirsIndex: " 
//				+ s + " file not exists." );
//	}
	
//	public void createIndex(Object d) throws Exception {
//		indexCreate.createIndex(d);
//	}
//	
//	public void updateIndex(Object d) throws Exception {
//		indexCreate.updateIndex(d);
//	}
//	
//	public void updateNumericDocValue(Object pkVal, Object fld,
//			Object val) throws Exception {
//		indexCreate.updateNumericDocValue(pkVal, fld, val);
//	}
//	
//	public void updateNumericDocValue(Object o) throws Exception {
//		indexCreate.updateNumericDocValue(o);
//	}

//	protected void createDirsIndex(String s) throws Exception {
//		File file = new File(s);
//		if (!file.exists()) {
//			doFileNotExists(s);
//		} if (file.isDirectory()) {
//			List<String> fileList = new ArrayList<String>();
//			FileCommon.listFile(new File(s), fileList);
//			for (String filePath : fileList) {
//				logger.info("file:"+filePath);
//				if (isHitExtName(filePath)) {
//					indexCreate.createIndexPath(filePath, marginLines);
//				}
//			}
//		} else {
//			if (isHitExtName(s)) {
//				indexCreate.createIndexPath(s, marginLines);
//			}
//		}
//	}

}
