package com.bidtime.lucene.base;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;

import com.bidtime.lucene.base.utils.FieldsMagnt;

public class LuceneSearchMgr extends AbstractIndexSearch {
	private SearcherManager mgr = null;// 是线程安全的

	public LuceneSearchMgr(Directory indexDir, Analyzer analyzer,
			FieldsMagnt headMagt) throws Exception {
		super(indexDir, analyzer, headMagt);
		mgr = new SearcherManager(indexDir, new SearcherFactory());
	}

	@Override
	protected IndexSearcher getSearch() throws Exception {
		mgr.maybeRefresh();
		return mgr.acquire();
	}

	@Override
	protected void closeSearch(IndexSearcher srh) throws Exception {
		if (srh != null) {
			mgr.release(srh);
		}
		srh = null;
	}

//	private Object synchronized_r = new Object();
//    private IndexSearcher getIndexSearcher() throws IOException {
//        IndexSearcher indexSearcher = null;
//        synchronized (synchronized_r) {
//            if(searcherManager == null) {
//                searcherManager = new SearcherManager(FSDirectory.open(new File(indexPath)), new SearcherFactory());
//            }
//            searcherManager.maybeRefresh();//这个方法同DirectoryReader.openIfChanged(dirReader)效果一样，其实底层还是调用的该方法实现的
//            indexSearcher = searcherManager.acquire();//借用一个IndexSearcher对象的引用，记住该对象用完之后要归还的，有借有还再借不难
//        }
//        return indexSearcher;
//    }
//
//    private void closeIndexSearcher(IndexSearcher indexSearcher) throws IOException {
//        if(indexSearcher != null) {
//            searcherManager.release(indexSearcher);//归还从SearcherManager处借来的IndexSearcher对象
//        }
//        indexSearcher = null;
//    }
}
