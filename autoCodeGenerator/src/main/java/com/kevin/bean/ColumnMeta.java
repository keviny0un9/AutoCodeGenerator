/**
 * Project Name:autoCodeGenerator
 * File Name:Column.java
 * Package Name:com.kevin.bean
 * Date:2015年8月13日上午9:36:07
 * Copyright (c) 2015, chenzhou1025@126.com All Rights Reserved.
 *
 */

package com.kevin.bean;

import com.kevin.utils.StringUtil;

/**
 * ClassName:ColumnMeta <br/>
 * Function: descrption for Column property as the database column. <br/>
 * Date: 2015年8月13日 上午9:36:07 <br/>
 * 
 * @author Kevin Yang
 * @version
 * @since JDK 1.6
 * @see
 */
public class ColumnMeta {

	private String javaName;
	private String name;
	private String dataType;
	private String nullable;
	private String length = "10";
	private String precision;
	private String scale;
	private String comments;
	private String unique;
	private String columnKey;

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}

	//VelocityContext会通过反射来找到setterMethod的方法。所以可以不设置属性，也能通过方法来查找到。
	public String getSetterMethod() {
		return "set"+StringUtil.firstCharUpperCase(javaName) ;
	}

	public String getGetterMethod() {
		return "get"+StringUtil.firstCharUpperCase(javaName) ;
	}
	@Override
	public String toString() {
		return "Column [javaName=" + javaName + ", name=" + name
				+ ", dataType=" + dataType + ", nullable=" + nullable
				+ ", length=" + length + ", precision=" + precision
				+ ", scale=" + scale + ", comments=" + comments + ", unique="
				+ unique + ", columnKey =" + columnKey + "]";
	}

}
