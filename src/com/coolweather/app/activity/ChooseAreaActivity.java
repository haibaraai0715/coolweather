package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.UiUtil;
import com.example.coolweather.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTRY=2;
	private ProgressDialog progressDialog;
	private TextView textView;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	private List<Province> provinceList;
	private List<City> cityList;
	private List<Country> countryList;
	private Province selectedProvince;
	private City selectedCity;
	private Country selectedCountry;
	
	private int currentLevel;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView) findViewById(R.id.list_view);
		textView=(TextView) findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
			
				if(currentLevel==LEVEL_PROVINCE){
					
					
					selectedProvince=provinceList.get(position);
				//queryCities();出问题
					queryCities();
				
				}else if (currentLevel==LEVEL_CITY) {
					
					selectedCity=cityList.get(position);
					queryCountries();
				}
				
			}
		});
		queryProvinces();
	}
	private void queryProvinces() {
		// TODO Auto-generated method stub 
		provinceList=coolWeatherDB.loadProvince();
		if(provinceList != null &&provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	private void queryCities(){

		cityList=coolWeatherDB.loadCity(selectedProvince.getId());

		if(cityList != null &&cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
			
		}else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	private void queryCountries(){
		countryList=coolWeatherDB.loadvCountry(selectedCity.getId());
		if(countryList != null &&countryList.size()>0){
			dataList.clear();
			for(Country country:countryList){
				dataList.add(country.getCountryName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTRY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"country");
		}
	}
	private void queryFromServer(final String code, final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address="http://www.weather.com.cn/data/list3/city.xml";
				
		}
		showProgressDialog();
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				
				// TODO Auto-generated method stub
				
				boolean result=false;
				if("province".equals(type)){
					
					result=UiUtil.handleProvincesResponse(coolWeatherDB, response);
				}else if ("city".equals(type)) {
					result=UiUtil.handleCitiesResponse(coolWeatherDB, response, selectedProvince.getId());
					
					
				}else if ("country".equals(type)) {
					result=UiUtil.handleCountriesResponse(coolWeatherDB, response, selectedCity.getId());
					
				}
				if(result){
					runOnUiThread(new Runnable() {
						public void run() {
//							Toast.makeText(ChooseAreaActivity.this, "加载chenggao",	Toast.LENGTH_LONG).show();
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if ("city".equals(type)) {
							queryCities();
								
								
							}else if ("country".equals(type)) {
							queryCountries();
								
							}
							
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",	Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		// TODO Auto-generated method stub
		
	}
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog= new ProgressDialog(this);
			progressDialog.setMessage("正在加载。。");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	private void 	closeProgressDialog(){
		if(progressDialog!=null)
			progressDialog.dismiss();
	}
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel==LEVEL_COUNTRY){
			queryCities();
		}else if (currentLevel==LEVEL_CITY) {
				queryProvinces();
			
		}else {
			finish();
		}
	}

}
