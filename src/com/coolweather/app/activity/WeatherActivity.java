package com.coolweather.app.activity;




import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.UiUtil;
import com.example.coolweather.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDesoText;
	private TextView temp1Text;
	
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDesoText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);
		switchCity=(Button) findViewById(R.id.switch_city);
		
		String countryCode = getIntent().getStringExtra("country_code");
		if(!TextUtils.isEmpty(countryCode)){
			publishText.setText("同步中");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
			
			
		}else{
		showWeather();
	}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
}

	private void queryWeatherCode(String countryCode) {
		// TODO Auto-generated method stub
		System.out.println("11");
		String adress="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		System.out.println("12");
		queryFromServerr(adress,"countryCode");
		System.out.println("13");
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDesoText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天"+prefs.getString("publish_time", "")+"发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中。。");
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
				
			}
			break;
		default	:
			break;
		}
	}
	private void queryWeatherInfo(String weatherCode) {
		String adress="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServerr(adress,"weatherCode");
		System.out.println("dsa");
		// TODO Auto-generated method stub
		
	}

	private void queryFromServerr(final String address,final String type) {
		System.out.println("fdfasfda");
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				System.out.println("1");
				if("countryCode".equals(type)){
					System.out.println("2");
					if(!TextUtils.isEmpty(response)){
						System.out.println("3");
						String[] array=response.split("\\|");
						if(array!=null&&array.length==2){
							System.out.println("4");
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					System.out.println("5");
					UiUtil.handleWeatherResponse(WeatherActivity.this, response);
					System.out.println("6");
					runOnUiThread(new Runnable() {
					
						public void run() {
							System.out.println("8");
							showWeather();
						}
					});
					
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				System.out.println("rewq");
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
		// TODO Auto-generated method stub
		
	}

	public static void handleWeatherResponse(Context context,String response){
		
		
		try {

			JSONObject jsonObject=new JSONObject(response);
			JSONObject wetherInfo=jsonObject.getJSONObject("weatherInfo");
			String cityName=wetherInfo.getString("city");
			String weatherCode=wetherInfo.getString("citycode");
			String temp1=wetherInfo.getString("l_tmp");
			String temp2=wetherInfo.getString("h_tmp");
			String weatherDesp=wetherInfo.getString("weather");
			String publishTime=wetherInfo.getString("time");
			savaWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void savaWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
			String temp2, String weatherDesp, String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月D日", Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
		
	}


}
