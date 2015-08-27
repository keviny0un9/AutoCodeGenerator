package com.kevin.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	/**
	 * GET方法获得HTTP 连接
	 * 
	 * @param uri
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static HttpURLConnection getGetConn(String uri)
			throws MalformedURLException, IOException {
		URL url = new URL(uri);
		return (HttpURLConnection) url.openConnection();
	}

	/**
	 * Post方法获得Http连接
	 * 
	 * @param uri
	 * @param param
	 * @return
	 * @throws java.net.MalformedURLException
	 */
	public static HttpURLConnection getPostConn(String uri,
			Map<String, Object> param) throws MalformedURLException,
			IOException {
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		// Output to the connection. Default is
		// false, set to true because post
		// method must write something to the
		// connection
		// 设置是否向connection输出，因为这个是post请求，参数要放在
		// http正文内，因此需要设为true
		connection.setDoOutput(true);
		// Read from the connection. Default is true.
		connection.setDoInput(true);
		// Set the post method. Default is GET
		connection.setRequestMethod("POST");
		// Post cannot use caches
		// Post 请求不能使用缓存
		connection.setUseCaches(false);
		// This method takes effects to
		// every instances of this class.
		// URLConnection.setFollowRedirects是static函数，作用于所有的URLConnection对象。
		// connection.setFollowRedirects(true);

		// This methods only
		// takes effacts to this
		// instance.
		// URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
		connection.setInstanceFollowRedirects(true);
		// Set the content type to urlencoded,
		// because we will write
		// some URL-encoded content to the
		// connection. Settings above must be set before connect!
		// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
		// 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode
		// 进行编码
		// connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
		// 要注意的是connection.getOutputStream会隐含的进行connect。
		connection.connect();
		// The URL-encoded contend
		// 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
		try {
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			// The URL-encoded contend
			// 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
			if (param != null && param.size() > 0) {
				// StringBuffer sb = new StringBuffer();
				for (String key : param.keySet()) {
					// sb.append("\""+key+"\"="+URLEncoder.encode(
					// param.get(key), "utf-8"));
					out.writeBytes("\"" + key + "\"=" + param.get(key));
				}
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * 获得HTTP请求返回值
	 * 
	 * @param conn
	 * @return
	 * @throws java.io.IOException
	 */
	public static String getHttpRespose(HttpURLConnection conn)
			throws IOException {
		InputStream is = conn.getInputStream();
		StringBuilder sb = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			sb = new StringBuilder();
			String content;
			while ((content = br.readLine()) != null) {
				if (sb.toString().equals("")) {
					sb.append(content);
				} else {
					sb.append("\n").append(content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void main(String args[]) {
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("step", "1");
					map.put("uid", "308d0be5da3f0ab");
					map.put("ukh", "434135551");
					// a';DROP TABLE users;
					map.put("umm", "123456");
					HttpURLConnection http = null;
					try {
						http = HttpUtil
								.getPostConn(
										"http://www.ibokn.com/Library.cgi4?uid=308d0be5da3f0ab",
										map);
						System.out.println("aa:"
								+ HttpUtil.getHttpRespose(http));
						http = HttpUtil
								.getGetConn("http://www.ibokn.com/Library.cgi4?uid=308d0be5da3f0ab&step=1&ukh=adfasfa&umm=a';DROP TABLE users;");
						System.out.println("bb:"
								+ HttpUtil.getHttpRespose(http));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						http.disconnect();
					}
				}
			}).start();
		}
	}
}
