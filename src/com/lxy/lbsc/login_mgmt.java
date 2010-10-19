/**
 * 
 */
package com.lxy.lbsc;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
		client=new DefaultHttpClient();
		
		Button btn_login = (Button)findViewById(R.id.button_login);
		btn_login.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	login();
            }  
        }); 
		
        SharedPreferences prefs = getPreferences(0); 
        int uid = prefs.getInt("uid", 0);
        String token = prefs.getString("token", null); 
        if (uid!=0){
        	Toast.makeText(this, "User already logged in, uid="+uid,4000).show();
        }else{
        	Toast.makeText(this, "haven't logged in yet",4000).show();
        }
        
        /*if (restoredText != null) {
            mSaved.setText(restoredText, TextView.BufferType.EDITABLE);

            int selectionStart = prefs.getInt("selection-start", -1);
            int selectionEnd = prefs.getInt("selection-end", -1);
            if (selectionStart != -1 && selectionEnd != -1) {
                mSaved.setSelection(selectionStart, selectionEnd);
            }
        }*/
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
		//String url = getString(R.string.url_test);
		HttpGet getMethod=new HttpGet(url);
		try {
			ResponseHandler<String> responseHandler=new BasicResponseHandler();
			
			String responseBody=client.execute(getMethod, responseHandler);
			responseBody = String.format("[%1$s]", responseBody); 
			Log.i(TAG, responseBody);
			// to handle return result from JSON
			JSONArray results = new JSONArray(responseBody);
			JSONObject obj = results.getJSONObject(0);
			int result = obj.getInt("result");
			if (result == 1){
				int uid = obj.getInt("u_id"); 
				String token = obj.getString("m_token");
				Toast.makeText(this, "Login ok. u_id="+uid+" token="+token,4000).show();
				// todo: save userid and token to preference
				SharedPreferences.Editor editor = getPreferences(0).edit();
		        editor.putInt("uid", uid);
		        editor.putString("token", token);
		        editor.commit();				
			}else{
				Toast.makeText(this, "Login failed",4000).show();
				// to clear the token
				SharedPreferences.Editor editor = getPreferences(0).edit();
		        editor.putInt("uid", 0);
		        editor.putString("token", "");
		        editor.commit();
			}
			
		}
		catch (Throwable t) {
			android.util.Log.e("TestLBSC", "Exception fetching data", t);
			Toast
				.makeText(this, "Request failed: "+t.toString(), 4000)
				.show();
		}
	}
	private static final String TAG = "LBSC";
}
