/**
 * Project Name:autoCodeGenerator
 * File Name:JdbcUtils.java
 * Package Name:com.kevin.utils
 * Date:2015年8月13日上午9:36:58
 * Copyright (c) 2015, chenzhou1025@126.com All Rights Reserved.
 *
*/

package com.kevin.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.texen.util.PropertiesUtil;

import com.kevin.bean.ColumnMeta;

/**
 * ClassName:JdbcUtils <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2015年8月13日 上午9:36:58 <br/>
 * @author   Kevin Yang
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class JdbcUtil {

	private final static Log log = LogFactory.getLog(JdbcUtil.class);
	
	public static Connection getConnection() throws Exception{
		Properties properties = new PropertiesUtil().load("jdbc.properties");

	    Properties localProperties = new Properties();
	    localProperties.put("remarksReporting","true");//注意这里
        localProperties.put("user", properties.getProperty("jdbc.username"));
        localProperties.put("password", properties.getProperty("jdbc.password"));

        Class.forName(properties.getProperty("jdbc.driver")).newInstance();
	    Connection conn= DriverManager.getConnection(properties.getProperty("jdbc.url"),localProperties);
	    return conn;
		
	}
	
	public static void close() {
		
	}
	
	public static List<ColumnMeta> getColumnMetas(String tableName) throws Exception {
		// prepare columns meta for givin table when connect database
		Connection conn = getConnection();
		List<ColumnMeta> lsColumns = new ArrayList<ColumnMeta>(10);
		PreparedStatement stmt = conn.prepareStatement("select *  from "+tableName+" where 1=0 ");
		ResultSet resultSet = stmt.executeQuery();
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int n = rsmd.getColumnCount();
		for (int i = 1; i <= n; i++)
		{
			String colName = rsmd.getColumnName(i);
			String fieldName = StringUtil.toBeanPatternStr(colName);
			String dataType = rsmd.getColumnClassName(i);
			if("java.math.BigDecimal".equals(dataType)&&rsmd.getScale(i)==0)
				dataType= "Long";
			if("oracle.sql.CLOB".equals(dataType) )
				dataType= "String";
			ColumnMeta column = new ColumnMeta();
			column.setName(colName) ;
			column.setJavaName(fieldName) ;
			column.setDataType(dataType.endsWith("Timestamp")?"java.util.Date":dataType);
			column.setPrecision(String.valueOf(rsmd.getPrecision(i)));
			column.setScale( String.valueOf(rsmd.getScale(i)) );
			column.setLength( String.valueOf(rsmd.getColumnDisplaySize(i)));
			column.setNullable(String.valueOf(1==rsmd.isNullable(i)));

//			获取列注释
			DatabaseMetaData dbmd = conn.getMetaData();
			
			ResultSet rs = dbmd.getColumns(null, null, tableName, null);
			while (rs.next()) {
				if (colName.equals(rs.getString("COLUMN_NAME"))){
					String  comments = rs.getString("REMARKS");
					column.setComments(StringUtil.asString(comments));
				}

			}
		//				获取主键列
			ResultSet rs2 = dbmd.getPrimaryKeys(null, null, tableName);
			while (rs2.next()) {
				if (colName.equals(rs2.getString("COLUMN_NAME")))
					column.setColumnKey("TRUE");
			}
			log.debug("------------------"+column+"---------------------" );
			log.debug("<td><spring:message code=\""+StringUtil.toBeanPatternStr(tableName)+"."+column.getJavaName()+"\" />"
			+"</td> <td><input name=\""+column.getJavaName()+"\" type=\"text\" id=\""+column.getJavaName()+"\" ltype=\"text\" validate=\"{required:true,minlength:3,maxlength:10}\" />" +"</td> " );
			lsColumns.add(column);
		}
		JdbcUtil.close();
		return lsColumns;
	}
	
	
}

