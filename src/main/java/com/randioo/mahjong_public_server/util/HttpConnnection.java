package com.randioo.mahjong_public_server.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnnection {

	public static void main(String args[]) throws Exception {

		String url = "http://10.0.51.17/APPadmin/gateway/MaJiang/getMoney.php";
		String param = "key=f4f3f65d6d804d138043fbbd1843d510&id=770";
		// sendMessage(url + param);
	}

	// get
	public static String sendMessageGet(String urlStr) throws Exception {
		// urlStr
		// 示例http://10.0.51.17/APPadmin/gateway/MaJiang/getMoney.php？key=f4f3f65d6d804d138043fbbd1843d510&id=770
		// TODO Auto-generated method stub
		URL url = new URL(urlStr);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setDoOutput(true); // 需要输出
		httpConn.setDoInput(true); // 需要输入
		httpConn.setUseCaches(false); // 不允许缓存
		// httpConn.setRequestMethod("POST"); // 设置POST方式连接
		// 设置请求属性
		httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpConn.setRequestProperty("Charset", "UTF-8");
		try {
			httpConn.connect();
			// 获得响应状态
			int resultCode = httpConn.getResponseCode();
			if (HttpURLConnection.HTTP_OK == resultCode) {
				StringBuffer sb = new StringBuffer();
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(),
						"UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					sb.append(readLine).append("\n");
				}
				responseReader.close();
				System.out.println(sb.toString());
				return sb.toString();
			} else {
				return null;
			}
		} finally {
			httpConn.disconnect();
		}
	}

	// post
	public static String sendMessagePost(String urlStr, String param) throws Exception {
		// urlStr示例:
		// http://10.0.51.17/APPadmin/gateway/MaJiang/getMoney.php
		// param示例:
		// key=f4f3f65d6d804d138043fbbd1843d510&id=770

		URL url = new URL(urlStr);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setDoOutput(true); // 需要输出
		httpConn.setDoInput(true); // 需要输入
		httpConn.setUseCaches(false); // 不允许缓存
		httpConn.setRequestMethod("POST"); // 设置POST方式连接
		// 设置请求属性
		httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
		httpConn.setRequestProperty("Charset", "UTF-8");
		// 连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
		httpConn.connect();
		// 建立输入流，向指向的URL传入参数
		DataOutputStream dos = new DataOutputStream(httpConn.getOutputStream());
		dos.writeBytes(param);
		dos.flush();
		dos.close();
		// 获得响应状态
		int resultCode = httpConn.getResponseCode();
		StringBuffer sb = new StringBuffer();
		if (HttpURLConnection.HTTP_OK == resultCode) {
			String readLine = new String();
			BufferedReader responseReader = new BufferedReader(
					new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			while ((readLine = responseReader.readLine()) != null) {
				sb.append(readLine).append("\n");
			}
			responseReader.close();
			System.out.println(sb.toString());
		}
		return sb.toString();
	}
}
