/**
 * 
 */
package com.lxy.lbsc;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageButton;

/**
 * @author LXY
 *
 */
public class login_mgmt extends Activity {
	private HttpClient client;
	private String format;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.login_mgmt);
		format=getString(R.string.url_login);
		
		Button btn_login = (Button)findViewById(R.id.button_login);
		btn_login.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	login();
            }  
        }); 
	}
	
	private void login(){
		EditText edit = (EditText)findViewById(R.id.edit_email1); 
		String email = edit.getText().toString(); 
		if(email.length()==0){
			Toast.makeText(login_mgmt.this,getString(R.string.err_no_email) , Toast.LENGTH_SHORT).show();
			return;
		}
		edit = (EditText)findViewById(R.id.edit_password1);
		String password = edit.getText().toString(); 
		if(password.length()==0){
			Toast.makeText(login_mgmt.this,getString(R.string.err_no_password) , Toast.LENGTH_SHORT).show();
			return;
		}
		
		String url=String.format(format, email, password);
		HttpGet getMethod=new HttpGet(url);
		try {
			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			String responseBody=client.execute(getMethod, responseHandler);
			// to handle return result from JSON
			//buildForecasts(responseBody);			
		}
		catch (Throwable t) {
			android.util.Log.e("WeatherDemo", "Exception fetching data", t);
			Toast
				.makeText(this, "Request failed: "+t.toString(), 4000)
				.show();
		}
	}
}
