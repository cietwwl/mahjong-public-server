package com.randioo.mahjong_public_server.util;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnection2{
	public HttpConnection2(String url, String pram){
		this.url = url;
		this.pram = pram;
	}
	String url = "";
	String pram = "";
	public String result = "";
    StringBuffer sbf = new StringBuffer();
    public String connect() {

    	result = "";
        try {
            URL url2 = new URL(url.concat(pram));
            System.out.println("@@@"+url.concat(pram));
            HttpURLConnection conn = (HttpURLConnection)url2.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000);
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /*public static void main(String args[]){
    	String url = "http://manager.app.randioo.com/gateway/MaJiang/changeMoney.php?key=f4f3f65d6d804d138043fbbd1843d510&&id=";
    	String tpram = "";
    	tpram = tpram.concat("oNd11wYwAZCbMWVhW4ndzMjaQgrI");
    	tpram = tpram.concat("&&money_num=3");
    	tpram = tpram.concat("&&type=0");
    	System.out.println("URL"+url);
    	System.out.println("PRAM"+tpram);
    	HttpConnection conn = new HttpConnection(url,tpram);
    	conn.connect();
    	System.out.println(conn.result+""+conn.result.getClass().getName()+(conn.result.charAt(0) == '1'));
    }*/
	/*@Override
	public void run() {
		// TODO Auto-generated method stub
		result = connect();
	}*/
   
}

