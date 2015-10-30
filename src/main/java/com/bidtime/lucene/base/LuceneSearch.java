package com.bidtime.lucene.base;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

import com.bidtime.lucene.base.utils.FieldsMagnt;

public class LuceneSearch extends AbstractIndexSearch {
	
	protected IndexSearcher searcher;

	public LuceneSearch(Directory indexDir, Analyzer analyzer,
			FieldsMagnt headMagt) throws Exception {
		super(indexDir, analyzer, headMagt);
		this.searcher = new IndexSearcher(reader);
	}
	
	public IndexSearcher getSearch() throws Exception {
		return searcher;
	}
	
	protected void closeSearch(IndexSearcher srh) throws Exception {
	}

}
