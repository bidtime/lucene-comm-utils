package com.bidtime.lucene.base.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;

import com.bidtime.lucene.base.utils.FieldsMagnt;

public class LuceneSearch extends AbstractIndexSearch {
	
	protected IndexSearcher searcher;

//	public LuceneSearch(FieldsMagnt headMagt, Analyzer analyzer,
//			Directory indexDir) throws Exception {
//		super(headMagt, analyzer, indexDir);
//		this.searcher = new IndexSearcher(reader);
//	}
//	
//	public LuceneSearch(String sourceFile,
//			Directory indexDir) throws Exception {
//		super(sourceFile, indexDir);
//		this.searcher = new IndexSearcher(reader);
//	}
	
	public LuceneSearch(FieldsMagnt headMagt, Analyzer analyzer,
			String idxPath) throws Exception {
		super(headMagt, analyzer, idxPath);
		this.searcher = new IndexSearcher(reader);
	}
	
//	public LuceneSearch(String fileSource,
//			String idxPath) throws Exception {
//		super(fileSource, idxPath);
//		this.searcher = new IndexSearcher(reader);
//	}
	
	public LuceneSearch(FieldsMagnt headMagt, String idxPath) throws Exception {
		super(headMagt, idxPath);
		this.searcher = new IndexSearcher(reader);
	}
	
	public IndexSearcher getSearch() throws Exception {
		return searcher;
	}
	
	protected void closeSearch(IndexSearcher srh) throws Exception {
	}

}
