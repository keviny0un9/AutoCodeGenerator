/**
 * Project Name:autoCodeGenerator
 * File Name:VelocityUtils.java
 * Package Name:com.kevin.utils
 * Date:2015年8月13日上午11:36:08
 * Copyright (c) 2015, chenzhou1025@126.com All Rights Reserved.
 *
 */

package com.kevin.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;


/**
 * ClassName:VelocityUtils <br/>
 * Date: 2015年8月13日 上午11:36:08 <br/>
 * 
 * @author Kevin Yang
 * @version
 * @since JDK 1.6
 * @see
 */
public class VelocityUtil {

	public static String RESOURCE_PATH_PREFIX = "classpath:";
	private HashMap<Object, Object> context = new HashMap<Object, Object>();
	private String tmplDir;
	private String encoding = "utf-8";

	private String absolutePath;
	private String tmplFile;

	public String getTmplDir() {
		return tmplDir;
	}

	public void setTmplDir(String tmplDir) {
		this.tmplDir = tmplDir;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public String getTmplFile() {
		return tmplFile;
	}

	public void setTmplFile(String tmplFile) {
		this.tmplFile = tmplFile;
	}

	public HashMap<Object, Object> getContext() {
		return context;
	}

	public void setContext(HashMap<Object, Object> context) {
		this.context = context;
	}

	/**
	 * initVelocity:init velocity. <br/>
	 * 通过VelocityEngine.FILE_RESOURCE_LOADER_PATH来指定template路径.<br/>
	 * 
	 * @author Kevin Yang
	 * @since JDK 1.6
	 */
	public void initVelocity() {
		Properties p = new Properties();
		p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH,
				getTemplatePath());
		Velocity.init(p);
	}

	public String getTemplatePath() {
		if (tmplDir.startsWith(RESOURCE_PATH_PREFIX)) {
			URL url = this.getClass().getResource(
					"/" + tmplDir.substring(RESOURCE_PATH_PREFIX.length()));
			File file = new File(url.getPath());
			return file.getAbsolutePath();
		}
		return tmplDir;
	}

	/**
	 * transformText: 转换内容. <br/>
	 *
	 * @author Kevin Yang
	 * @param text 内容
	 * @return
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public String transformText(String text) throws Exception {
		VelocityContext tmplContext = new VelocityContext(context);
		StringWriter w = new StringWriter();
		Velocity.evaluate(tmplContext, w, super.getClass().getName(), text);
		return w.toString();
	}

	/**
	 * createFileByTmpl: 通过模板来创建文件. <br/>
	 *
	 * @author Kevin Yang
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public void createFileByTmpl() throws Exception {
		// Velocity.addProperty("file.resource.loader.path", getClassPath()
		// + tmplDir);
		Template template = Velocity.getTemplate(tmplFile, encoding);
		VelocityContext tmplContext = new VelocityContext(context);
		FileUtil.createFile(absolutePath);
		PrintWriter writer = new PrintWriter(
				new FileOutputStream(absolutePath), true);
		template.merge(tmplContext, writer);
		writer.flush();
		writer.close();
	}

	// test main
	public static void main(String[] args) throws Exception {
		VelocityUtil vu = new VelocityUtil();
		vu.initVelocity();
		vu.context.put("author", "Kevin");
		vu.setTmplFile("DAOImpl.tmpl");
		vu.setAbsolutePath("D:/abc1.txt");
		vu.createFileByTmpl();
	}
}
