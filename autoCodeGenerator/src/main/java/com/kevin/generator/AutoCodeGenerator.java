/**
 * Project Name:autoCodeGenerator
 * File Name:AutoCodeGenerator.java
 * Package Name:com.kevin.generator
 * Date:2015年8月13日上午9:37:39
 * Copyright (c) 2015, chenzhou1025@126.com All Rights Reserved.
 *
 */

package com.kevin.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.texen.util.PropertiesUtil;

import com.kevin.bean.ColumnMeta;
import com.kevin.utils.DateUtil;
import com.kevin.utils.JdbcUtil;
import com.kevin.utils.StringUtil;
import com.kevin.utils.VelocityUtil;

/**
 * ClassName:AutoCodeGenerator <br/>
 * Function: provide code generate function. <br/>
 * Date: 2015年8月13日 上午9:37:39 <br/>
 * 
 * @author Kevin Yang
 * @version
 * @since JDK 1.6
 * @see
 */
public class AutoCodeGenerator {

	private final Log log = LogFactory.getLog(getClass());

	/**
	 * context: to store the variable for velocity
	 * @since JDK 1.6
	 */
	HashMap<Object, Object> context = new HashMap<Object, Object>();
	public static String RESOURCE_PATH_PREFIX = "classpath:";

	/**
	 * init: init hashMap context to prepare data for velocity. <br/>
	 * 
	 * @author Kevin Yang
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public void init() throws Exception {
		// 1.1 load code.properties and retrieve data
		loadBasicConfig();

		// 2.1 load table meta
		loadTableMeta();

		// 3.1 load base sysinfo
		loadSysBasicInfo();
	}

	/**
	 * generate: generate code with the speicfy template <br/>
	 * 
	 * @author Kevin Yang
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public void generate() throws Exception {

		// run velocityUtils to create data
		VelocityUtil vu = new VelocityUtil();
		vu.setContext(context);
		vu.setTmplDir((String)context.get("templateDir"));

		vu.initVelocity();
		Properties properties = new PropertiesUtil().load("templates.cfg");
		// loop to read template.cfg to create file
		for (Object file : properties.values()) {
			String fileAbsolutePath = file.toString();
			String[] props = fileAbsolutePath.split(";");
			// props[1]是数组中第二个，filepath
			String filePath = vu.transformText(props[1].trim());
			String templateFile = props[2].trim();
			vu.setAbsolutePath(filePath);
			vu.setTmplFile(templateFile);
			vu.createFileByTmpl();
		}
		log.info("create success!");

	}

	private void loadBasicConfig() {
		Properties properties = new PropertiesUtil().load("basic.properties");
		context.putAll(properties);
	}

	/**
	 * loadTableMeta:加载要生成表的数据. <br/>
	 * 
	 * @author Kevin Yang
	 * @throws Exception
	 * @since JDK 1.6
	 */
	private void loadTableMeta() throws Exception {

		String tableName = (String) context.get("tableName");
		List<ColumnMeta> columnMetas = JdbcUtil.getColumnMetas(tableName);

		// load columnMeta
		loadColumnMetas(columnMetas);

		// load primary key
		loadPrimaryKey();

		// load user define data
		loadTransformationalData();
	}

	private void loadTransformationalData() {
		String groupIdDir;
		String classVar;
		String className;
		String jspDir;
		String packagePrefix;

		String tableName = (String) context.get("tableName");
		String tablePrefix = (String) context.get("tablePrefix");
		String module = (String) context.get("module");
		String subModule = (String) context.get("subModule");
		String groupId = (String) context.get("groupId");

		groupIdDir = StringUtil.replaceDouHaoWithGan(groupId);
		classVar = StringUtil.toBeanPatternStr(tableName.substring(tablePrefix
				.length()));
		className = StringUtil.firstCharUpperCase(classVar);
		packagePrefix = groupId
				+ (StringUtil.isEmpty(module) ? "" : ("." + module))
				+ (StringUtil.isEmpty(subModule) ? "" : ("." + subModule));
		jspDir = (StringUtil.isEmpty(module) ? "" : ("/" + module))
				+ (StringUtil.isEmpty(subModule) ? "" : ("/" + subModule));

		context.put("groupIdDir", groupIdDir);
		context.put("packagePrefix", packagePrefix);
		context.put("classVar", classVar);
		context.put("className", className);
		context.put("jspDir", jspDir);
	}

	private void loadSysBasicInfo() {
		context.put("needUpdate", true);
		context.put("date", DateUtil.getCurrentDay());
		context.put("author", System.getProperty("user.name"));
	}

	private void loadColumnMetas(List<ColumnMeta> columnMetas) {
		// 把主键和非主键分别放入context
		List<ColumnMeta> lsColumnTemp = new ArrayList<ColumnMeta>(
				columnMetas.size());
		List<ColumnMeta> pkColumns = new ArrayList<ColumnMeta>(2);
		for (ColumnMeta column : columnMetas) {
			if ("TRUE".equalsIgnoreCase(column.getColumnKey())) {
				pkColumns.add(column);
			} else {
				lsColumnTemp.add(column);
			}
		}
		//columnResult非主键的column list
		context.put("columnResult", lsColumnTemp);
		//pkResult 主键的column list
		context.put("pkResult", pkColumns);
		//columnMetas是所有的column list
		context.put("columnMetas", columnMetas);
	}

	@SuppressWarnings("unchecked")
	private void loadPrimaryKey() {
		String keyType;
		String keyVar;
		List<ColumnMeta> pkResult = (List<ColumnMeta>) context.get("pkResult");
		int pkCnt = pkResult.size();
		// check primary key type
		if (pkCnt > 1 || pkCnt == 0) {
			log.debug("创建复合主键类：");
			keyType = "PK";
			keyVar = "pk";
			context.put("keyType", keyType);
			context.put("keyVar", keyVar);
		} else if (pkCnt == 1) {
			//惟一主键
			ColumnMeta column = pkResult.get(0);
			keyType = column.getDataType();
			keyVar = column.getJavaName();
			
			log.info("惟一主键: " + keyVar);
			context.put("keyType", keyType);
			context.put("keyVar", keyVar);
		} else {
			// throw new Exception("该表无主键！！！！！！！！");
			log.info("no primary key in this table");
		}
	}

}
