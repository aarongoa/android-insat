package com.duck.insat;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class InsatActivity extends Activity{
	
	private ImageView satImg;
	private Button show_visible, show_ir, show_vapour, show_composite;
	
    final String img_irc = "http://www.imd.gov.in/section/satmet/img/sector-irc.jpg";
    final String img_ir = "http://www.imd.gov.in/section/satmet/img/sector-ir.jpg";
    final String img_wv = "http://www.imd.gov.in/section/satmet/img/sector-wv.jpg";
    final String img_vis = "http://www.imd.gov.in/section/satmet/img/sector-vis.jpg";

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        satImg = (ImageView)findViewById(R.id.imageViewImg);
        show_visible = (Button)findViewById(R.id.buttonVisible);
        show_ir = (Button)findViewById(R.id.buttonIr);
        show_vapour = (Button)findViewById(R.id.buttonWv);
        show_composite = (Button)findViewById(R.id.buttonComposite);
        
        show_visible.setOnClickListener(new OnClick());
        show_vapour.setOnClickListener(new OnClick());
        show_ir.setOnClickListener(new OnClick());
        show_composite.setOnClickListener(new OnClick());
        
    }
    
    private void setSatImage(String urlString)
    {
    	try{

            URL url = new URL(urlString);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            satImg.setImageBitmap(bmp);
            
        }
        catch(Exception ex)
        {
            Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
        }
   
    }

    public class OnClick implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId())
			{
			case R.id.buttonVisible:
				setSatImage(img_vis);
				break;
			case R.id.buttonIr:
				setSatImage(img_ir);
				break;
			case R.id.buttonWv:
				setSatImage(img_wv);
				break;
			case R.id.buttonComposite:
				setSatImage(img_irc);
				break;
			}
			
		}
		
	}
	
}
