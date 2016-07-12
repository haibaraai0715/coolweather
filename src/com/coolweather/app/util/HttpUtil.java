package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.coolweather.app.activity.ChooseAreaActivity;

import android.widget.Toast;

public class HttpUtil {
 public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
	 new Thread(new Runnable(){
		 public void run(){
			 HttpURLConnection connection=null;
			
			 try{	
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8*1000);
					connection.setReadTimeout(8*1000);
					System.out.println("12312321");
					InputStream in = connection.getInputStream();
					System.out.println("12312321aaaa");
				 BufferedReader reader=new BufferedReader(new InputStreamReader(in));
				 
				 StringBuilder response=new StringBuilder();
				 System.out.println("12312打撒打撒321aaaa");
			
				 String line;
				 //第二次返回weatherCode的是while 这里IO异常 
				 while((line=reader.readLine())!=null){
					 
					 response.append(line);
					 System.out.println("12312倒萨是打撒打撒321aaaa");
					 System.out.println(response);
			
				 }
					
				 if(listener!=null){
						System.out.println("123adsadsada1232aaaa5");
					 listener.onFinish(response.toString());
						System.out.println("1231232aaaa5");
				 }
			 }catch(Exception e){
					System.out.println("1231232aaaa哥哥幅度萨芬5hhhh");
				 if(listener!=null){
					 System.out.println("1231232aaaa哥哥幅度萨芬5hdsadsahhh");
					 listener.onError(e);
				 }
			 }
			 finally{
				 if(connection!=null){
					 connection.disconnect();
				 }
			 }
		 }
	 }).start();
 }
}
