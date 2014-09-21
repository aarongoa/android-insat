package com.duck.insat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.text.Normalizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {

	TextView location, sampledTime, temperature, description, highLow,
	humidity, pressure;
	EditText cityName;

	String cityNameString = "";
	String url1 = "http://api.worldweatheronline.com/free/v1/weather.ashx?q=";
	String url2 = "&format=json&num_of_days=1&key=34e358412a58f64a992836610ea5a9c01025d8c4";
	String finalUrl = "";

	Button submitBtn;

	JSONObject weatherData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		location = (TextView) findViewById(R.id.location);
		sampledTime = (TextView) findViewById(R.id.sampledTime);
		temperature = (TextView) findViewById(R.id.temperature);
		description = (TextView) findViewById(R.id.description);
		highLow = (TextView) findViewById(R.id.highLow);
		humidity = (TextView) findViewById(R.id.humidity);
		pressure = (TextView) findViewById(R.id.pressure);
		cityName = (EditText) findViewById(R.id.cityName);
		submitBtn = (Button) findViewById(R.id.submitBtn);

		submitBtn.setOnClickListener(new OnClick());
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionsmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent = new Intent(this, Map.class);

		switch (item.getItemId()) {
		case R.id.view_map:
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// fetches the JSON data and displays
	private class GetContacts extends AsyncTask<String, Void, Void> {

		ProgressDialog pDialog = new ProgressDialog(WeatherActivity.this,
				AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

		String temperatureString, humidityString, pressureString,
		descriptionString, timeString;
		String maxTempString, minTempString;

		String jsonStr = "";
		InputStream inputStream;
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(String... arg0) {

			try {
				// Set up HTTP post
				// HttpClient is more then less deprecated. Need to change to
				// URLConnection
				HttpClient httpClient = new DefaultHttpClient();

				HttpPost httpPost = new HttpPost(arg0[0].toString());
				httpPost.setEntity(new UrlEncodedFormEntity(param));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

				// Read content & Log
				inputStream = httpEntity.getContent();
			} catch (Exception e) {
				Toast.makeText(WeatherActivity.this, e.getMessage(),
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

			// Convert response to string using String Builder
			try {
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sBuilder = new StringBuilder();

				String line;
				while ((line = bReader.readLine()) != null) {
					sBuilder.append(line + "\n");
				}

				inputStream.close();
				jsonStr = sBuilder.toString();

			} catch (Exception e) {
				Log.e("StringBuilding & BufferedReader",
						"Error converting result " + e.toString());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					weatherData = jsonObj.getJSONObject("data");
					JSONArray jsonArr = weatherData
							.getJSONArray("current_condition");

					jsonObj = jsonArr.getJSONObject(0);

					temperatureString = jsonObj.getString("temp_C");
					humidityString = jsonObj.getString("humidity");
					pressureString = jsonObj.getString("pressure");
					timeString = jsonObj.getString("observation_time");

					JSONArray jsonArr1 = jsonObj.getJSONArray("weatherDesc");

					JSONObject jsonObj1 = jsonArr1.getJSONObject(0);

					descriptionString = jsonObj1.getString("value");

					jsonArr1 = weatherData.getJSONArray("weather");
					jsonObj1 = jsonArr1.getJSONObject(0);

					maxTempString = jsonObj1.getString("tempMaxC");
					minTempString = jsonObj1.getString("tempMinC");

				} catch (Exception e) {
					Toast.makeText(WeatherActivity.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Couldn't get any data from the url", Toast.LENGTH_LONG)
						.show();
			}

			location.setText(cityName.getText().toString().toUpperCase());
			sampledTime.setText("Sampled time: " + timeString);
			temperature.setText(temperatureString + " °C");
			description.setText(descriptionString);
			highLow.setText("Max: " + maxTempString + " C" + "  Min: "
					+ minTempString + " C");
			humidity.setText("Humidity: " + humidityString);
			pressure.setText("Pressure: " + pressureString);

		}

	}

	private class OnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// check if connected to internet
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			
			cityNameString = cityName.getText().toString();

			// TODO Auto-generated method stub
			if(cityNameString.isEmpty()){
				Toast.makeText(getApplicationContext(), "Enter City Name", Toast.LENGTH_LONG).show();
			}
			else if (activeNetwork != null && activeNetwork.isConnected()) {

				// removes whitespaces at the end
				cityNameString = cityNameString.trim();

				// removes white spaces between 2 words and replaces with '&' to
				// pass it in the URL. Eg: San&Francisco
				cityNameString = cityNameString.replaceAll("\\s", "&");

				// to remove accents from words
				cityNameString = Normalizer.normalize(cityNameString,
						Normalizer.Form.NFD);
				cityNameString = cityNameString.replaceAll("\\p{M}", "");

				try {
					finalUrl = url1 + cityNameString + url2;
					new GetContacts().execute(finalUrl);

				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Check Internet Connection", Toast.LENGTH_LONG).show();
			}
		}

	}

}