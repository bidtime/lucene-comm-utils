package org.bidtime.lucene.busScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.BasicTest;
import org.bidtime.lucene.busScope.bean.BusScope;
import org.bidtime.lucene.busScope.dao.BusScopeDAO;
import org.bidtime.lucene.ldbc.rs.handler.BeanLDTOHandler;
import org.bidtime.lucene.ldbc.rs.handler.BeanListLDTOHandler;
import org.bidtime.lucene.ldbc.rs.handler.ColumnLSetHandler;
import org.bidtime.lucene.utils.FileCommon;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bidtim on 2015/9/23.
 */
public class BusScopeTest extends BasicTest {

	@Autowired
	protected BusScopeDAO dao;

	@Test
	public void test_search_bean() throws Exception {
		BeanLDTOHandler<BusScope> h = new BeanLDTOHandler<>(BusScope.class);
//		String words = "code:jss_0";
		String words = "name:刹车";
		ResultDTO<BusScope> dto = dao.query(words, h);
		System.out.println("search_bean: " + dto.getLen() + " -> " + dto.toString());
	}

	@Test
	public void test_search_set() throws Exception {
		ColumnLSetHandler<Integer> h = new ColumnLSetHandler<Integer>(Integer.class, "pid");
//		String words = "code:jss_0";
		String words = "name:车";
		Set<Integer> dto = dao.query(words, h);
		System.out.println("search_set: " + dto.size() + " -> " + dto.toString());
	}

	@Test
	public void test_search_list() throws Exception {
		BeanListLDTOHandler<BusScope> h = new BeanListLDTOHandler<>(BusScope.class);
		String words = "name:江";
		ResultDTO<List<BusScope>> dto = dao.query(words, 0, 10, h);
		System.out.println("search_list: " + dto.getLen() + " -> " + dto.toString());
	}
	
	private List<BusScope> readIt() throws Exception {
		List<BusScope> list = new ArrayList<>();
		//
		List<String> listCtx = FileCommon.getFileCtxList("D:/data/lucene/index/raw/business.txt", "UTF-8");
		for (int i=1; i<listCtx.size(); i++) {
			String s = listCtx.get(i);
			String[] tmp = s.split("\t");
			//
			BusScope sc = new BusScope();
			sc.setId(i);
			//sc.setpId(Integer.parseInt(tmp[0]));
			//sc.setpName(tmp[1]);
			//sc.setBsId(Integer.parseInt(tmp[2]));
			//sc.setBsName(tmp[3]);
			sc.setpId(Integer.parseInt(tmp[0]));
			sc.setCode(tmp[1]);
			sc.setName(tmp[3]);
			print(sc);
			//
			list.add(sc);
		}
		return list;
	}
	
	@Test
	public void test_insert_file() throws Exception {
		List<BusScope> list = readIt();
		for (BusScope p : list) {
			dao.insert(p);
		}
		print("ok：" + list.size());
	}
	
	@Test
	public void test_insert() throws Exception {
		for (int i=0; i<50; i++) {
			BusScope b = new BusScope();
			b.setId(i);
			b.setName("江少山_" + i);
			b.setCode("jss_" + i);
			dao.insert(b);
		}
		print("ok");
	}
	
}
