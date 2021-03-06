/*
 * The MIT License (MIT)

Copyright (c) 2014 Aaron de Miranda Colaco

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.duck.insat;

import java.net.URL;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Map extends Activity {

	private TouchImageView satImg; // from TouchImageView.java
	private Button show_visible, show_ir, show_vapour, show_composite;
	final String img_irc = "http://www.imd.gov.in/section/satmet/img/sector-irc.jpg";
	final String img_ir = "http://www.imd.gov.in/section/satmet/img/sector-ir.jpg";
	final String img_wv = "http://www.imd.gov.in/section/satmet/img/sector-wv.jpg";
	final String img_vis = "http://www.imd.gov.in/section/satmet/img/sector-vis.jpg";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insatactivity);

		satImg = (TouchImageView) findViewById(R.id.imageViewImg);
		show_visible = (Button) findViewById(R.id.buttonVisible);
		show_ir = (Button) findViewById(R.id.buttonIr);
		show_vapour = (Button) findViewById(R.id.buttonWv);
		show_composite = (Button) findViewById(R.id.buttonComposite);
		show_visible.setOnClickListener(new OnClick());
		show_vapour.setOnClickListener(new OnClick());
		show_ir.setOnClickListener(new OnClick());
		show_composite.setOnClickListener(new OnClick());
	}

	public class OnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

			// check if connected to Internet. Otherwise app crashes
			if (activeNetwork != null && activeNetwork.isConnected()) {
				switch (v.getId()) {
				case R.id.buttonVisible:
					new Download().execute(img_vis);
					break;
				case R.id.buttonIr:
					new Download().execute(img_ir);
					break;
				case R.id.buttonWv:
					new Download().execute(img_wv);
					break;
				case R.id.buttonComposite:
					new Download().execute(img_irc);
					break;
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"Check Internet Connection", Toast.LENGTH_LONG).show();
			}
		}
	}

	private class Download extends AsyncTask<String, Void, Bitmap> {

		ProgressDialog pDialog = new ProgressDialog(Map.this,
				AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog.setCancelable(false);
			pDialog.setMessage("Fetching Image...");
			pDialog.show();
		}

		// sets the image
		@Override
		protected void onPostExecute(Bitmap bmp) {
			// TODO Auto-generated method stub
			if (pDialog.isShowing())
				pDialog.dismiss();

			satImg.setImageBitmap(bmp);
		}

		// downloads the image
		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				URL url = new URL(params[0]);
				Bitmap bmp = BitmapFactory.decodeStream(url.openConnection()
						.getInputStream());
				return bmp;
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.toString(),
						Toast.LENGTH_LONG).show();
			}
			return null;
		}

	}
}