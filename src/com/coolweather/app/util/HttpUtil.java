package com.coolweather.app.util;

import java.io.BufferedReader;
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
				
				 String line;
				 System.out.println("1231232aaaa2");
				 while((line=reader.readLine())!=null){
					 response.append(line);
					 System.out.println(response);
					
				 }
				 if(listener!=null){
						
					 listener.onFinish(response.toString());
				 }
			 }catch(Exception e){
				 if(listener!=null){
						System.out.println("1231232aaaa5");
					 listener.onError(e);
				 }
			 }finally{
				 if(connection!=null){
					 connection.disconnect();
				 }
			 }
		 }
	 }).start();
 }
}
