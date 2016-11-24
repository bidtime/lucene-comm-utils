package org.bidtime.lucene.base.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

public class LuceneSearch extends AbstractIndexSearch {
	
	protected IndexSearcher searcher;

	public LuceneSearch(Analyzer analyzer,
			Directory indexDir, Boolean open) throws Exception {
		super(analyzer, indexDir, open);
		if (open) {
			this.searcher = new IndexSearcher(reader);
		}
	}
	
	public LuceneSearch(Directory indexDir, Boolean open) throws Exception {
		super(indexDir, open);
		if (open) {
			this.searcher = new IndexSearcher(reader);
		}
	}
	
	public LuceneSearch(Analyzer analyzer, String idxPath, Boolean open) throws Exception {
		super(analyzer, idxPath, open);
		if (open) {
			this.searcher = new IndexSearcher(reader);
		}
	}

	public LuceneSearch(String idxPath, Boolean open) throws Exception {
		super(idxPath, open);
		if (open) {
			this.searcher = new IndexSearcher(reader);
		}
	}
	
	public IndexSearcher getSearch() throws Exception {
		return searcher;
	}
	
	protected void closeSearch(IndexSearcher srh) throws Exception {
	}

}
