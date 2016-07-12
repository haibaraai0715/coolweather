package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class UiUtil {
	 public synchronized static boolean handleProvincesResponse(CoolWeatherDB	coolWeatherDB,String response){
		 if(!TextUtils.isEmpty(response)){
			 String[] allProvinces=response.split(",");
			 if(allProvinces!=null&&allProvinces.length>0){
				 for(String p:allProvinces){
					 String[] array=p.split("\\|");
					 Province province=new Province();
					 province.setProvinceCode(array[0]);
					 province.setProvinceName(array[1]);
					 coolWeatherDB.saveProvince(province);
				 }
				 return true;
			 }
		 }
		 return false;
	 }
	 public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
		 if(!TextUtils.isEmpty(response)){
			 String[] allCities=response.split(",");
			 if(allCities!=null&&response.length()>0){
				 for(String c:allCities){
					 String[] array=c.split("\\|");
					 City city=new City();
					 city.setCityCode(array[0]);
					 city.setCityName(array[1]);
					 city.setProvinceId(provinceId);
					 coolWeatherDB.saveCity(city);
				 }
				 return true;
			 }
		 }
		 return false;
	 }
	 public static boolean handleCountriesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
		 if(!TextUtils.isEmpty(response)){
			 String[] allCountries=response.split(",");
			 if(allCountries!=null&&response.length()>0){
				 for(String c:allCountries){
					 String[] array=c.split("\\|");
					 Country country=new Country();
					 country.setCountryCode(array[0]);
					 country.setCountryName(array[1]);
					 country.setCityId(cityId);
					 coolWeatherDB.saveCountry(country);
				 }
				 return true;
			 }
		 }
		 return false;
	 }
		public static void handleWeatherResponse(Context context,String response){
			
			
			try {

				JSONObject jsonObject=new JSONObject(response);
				JSONObject wetherInfo=jsonObject.getJSONObject("weatherInfo");
				String cityName=wetherInfo.getString("city");
				String weatherCode=wetherInfo.getString("cityid");
				String temp1=wetherInfo.getString("temp1");
				String temp2=wetherInfo.getString("temp2");
				String weatherDesp=wetherInfo.getString("weather");
				String publishTime=wetherInfo.getString("ptime");
				savaWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		private static void savaWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
				String temp2, String weatherDesp, String publishTime) {
			// TODO Auto-generated method stub
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyƒÍM‘¬D»’", Locale.CHINA);
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



