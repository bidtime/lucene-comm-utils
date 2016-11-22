package org.bidtime.lucene.base.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

public class LuceneSearch extends AbstractIndexSearch {
	
	protected IndexSearcher searcher;

	public LuceneSearch(Analyzer analyzer,
			Directory indexDir) throws Exception {
		super(analyzer, indexDir);
		this.searcher = new IndexSearcher(reader);
	}
	
	public LuceneSearch(Directory indexDir) throws Exception {
		super(indexDir);
		this.searcher = new IndexSearcher(reader);
	}
	
	public LuceneSearch(Analyzer analyzer, String idxPath) throws Exception {
		super(analyzer, idxPath);
		this.searcher = new IndexSearcher(reader);
	}

	public LuceneSearch(String idxPath) throws Exception {
		super(idxPath);
		this.searcher = new IndexSearcher(reader);
	}
	
	public IndexSearcher getSearch() throws Exception {
		return searcher;
	}
	
	protected void closeSearch(IndexSearcher srh) throws Exception {
	}

}
