package org.bidtime.lucene.businessScope;

import java.util.ArrayList;
import java.util.List;

import org.bidtime.dbutils.gson.ResultDTO;
import org.bidtime.lucene.BasicTest;
import org.bidtime.lucene.businessScope.bean.BusinessScope;
import org.bidtime.lucene.duty.bean.Duty;
import org.bidtime.lucene.duty.dao.DutyDAO;
import org.bidtime.lucene.ldbc.rs.handler.BeanLDTOHandler;
import org.bidtime.lucene.ldbc.rs.handler.BeanListLDTOHandler;
import org.bidtime.lucene.utils.FileCommon;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bidtim on 2015/9/23.
 */
public class BusinessScopeTest extends BasicTest {

//	@Autowired
//	protected IKAnalyzer4PinYin analyzer;

	@Autowired
	protected DutyDAO dao;

	@Test
	public void test_search_bean() throws Exception {
		BeanLDTOHandler<Duty> h = new BeanLDTOHandler<>(Duty.class);
		String words = "code:jss_0";
		ResultDTO<Duty> dto = dao.query(words, h);
		System.out.println("search_bean: " + dto.getLen() + " -> " + dto.toString());
	}

	@Test
	public void test_search_list() throws Exception {
		BeanListLDTOHandler<Duty> h = new BeanListLDTOHandler<>(Duty.class);
		//String words = "bsName:方向";
		String words = "bsId:1";
		ResultDTO<List<Duty>> dto = dao.query(words, 0, 10, h);
		System.out.println("search_list: " + dto.getLen() + " -> " + dto.toString());
	}
	
	private List<BusinessScope> readIt() throws Exception {
		List<BusinessScope> list = new ArrayList<>();
		//
		List<String> listCtx = FileCommon.getFileCtxList("D:/data/lucene/index/raw/business.txt", "UTF-8");
		for (int i=1; i<listCtx.size(); i++) {
			String s = listCtx.get(i);
			String[] tmp = s.split("\t");
			//
			BusinessScope sc = new BusinessScope();
			sc.setId(i);
			sc.setpId(Integer.parseInt(tmp[0]));
			sc.setpName(tmp[1]);
			sc.setBsId(Integer.parseInt(tmp[2]));
			sc.setBsName(tmp[3]);
			print(sc);
			//
			list.add(sc);
		}
		return list;
	}
	
	@Test
	public void test_insert_file() throws Exception {
		List<BusinessScope> list = readIt();
		for (BusinessScope p : list) {
			dao.insert(p);
		}
		print("ok：" + list.size());
	}
	
//	@Test
//	public void test_insert() throws Exception {
//		for (int i=0; i<50; i++) {
//			Duty b = new Duty();
//			b.setId(i);
//			b.setName("江少山_" + i);
//			b.setCode("jss_" + i);
//			dao.insert(b);
//		}
//		print("ok");
//	}
	
}
