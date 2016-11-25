package org.bidtime.lucene.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileCommon {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void listFile(File f, List fileList) {
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				listFile(files[i], fileList);
			}
		} else {
			fileList.add(f.getAbsolutePath());
		}
	}

	public static String getFileContextUTF8(String filePath) throws Exception {
		return getFileContext(filePath, "UTF-8");
	}
	
	public static String getFileContext(String filePath, String encoding) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), encoding));
		try {
			StringBuilder content = new StringBuilder();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				content.append(str).append("\n");
			}
			return content.toString();
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}
	
	public static List<String> getFileCtxList(String filePath, String encoding) throws Exception {
		List<String> list = new ArrayList<>();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), encoding));
		try {
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				list.add(str);
				System.out.println(str);
			}
			return list;
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
//			list.clear();
//			list = null;
		}
	}

	/**
	 * 
	 * 读取文件内容
	 * 
	 * @param filePath
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */

	// private StringBuilder readFileBuffer(String filePath) throws Exception {
	// BufferedReader bufferedReader = new BufferedReader(
	// new InputStreamReader(new FileInputStream(filePath)));
	// StringBuilder content = new StringBuilder();
	// String str = null;
	// while ((str = bufferedReader.readLine()) != null) {
	// content.append(str).append("\n");
	// }
	// return content;
	// }

}
