/**
 * 
 */
package com.lxy.lbsc;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.widget.EditText; 
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author LIU
 *
 */
public class AskQuestionActivity extends Activity {
	private String mPlaceName = null;
	private int mPlaceId = 0; 
	private HttpClient client;
	AtomicBoolean isRunning=new AtomicBoolean(false);
	Handler handler = new Handler(){
		@Override
	    public void handleMessage(Message msg) {
	        finish();
	    }
	}; 	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.askquestion);
        
        client=new DefaultHttpClient();
        Bundle bundle = getIntent().getExtras();    
        mPlaceName=bundle.getString("place_name");
        mPlaceId=bundle.getInt("place_id"); 
        TextView tv= (TextView)findViewById(R.id.question_placename); 
        tv.setText(mPlaceName); 
                
        ImageButton btn_faverate = (ImageButton)findViewById(R.id.btn_faverate);
        btn_faverate.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(AskQuestionActivity.this, "TODO: Handle faverate", Toast.LENGTH_SHORT).show();            	
            }  
        }); 
		
        ImageButton btn_submit = (ImageButton)findViewById(R.id.btn_question_submit);
        btn_submit.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	EditText et= (EditText)findViewById(R.id.edit_question); 
            	String str = et.getText().toString(); 
            	if (str.trim().length()==0){
            		Toast.makeText(AskQuestionActivity.this, getString(R.string.err_no_question), Toast.LENGTH_SHORT).show();
            		return;
            	}
            	SharedPreferences prefs = getSharedPreferences("data", 0); 
                int uid = prefs.getInt("uid", 0);
                String token = prefs.getString("token", null); 
                if (uid==0){
                	Toast.makeText(AskQuestionActivity.this, "haven't logged in yet",4000).show();
                	Intent intent = new Intent(AskQuestionActivity.this, login_mgmt.class);
                    startActivity(intent);
                    finish();
                    //return; 
                }
            	Toast.makeText(AskQuestionActivity.this, "To submit question", Toast.LENGTH_SHORT).show(); 
            	isRunning.set(true);
        		Thread background=new Thread(new SubmitQuestionRunnable(str.trim(),uid,token, mPlaceId )) ;
                background.start();
            }  
        }); 		
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onStop() {
        super.onStop();
        isRunning.set(false);
    }
	
	public class SubmitQuestionRunnable implements Runnable {
		private String mQuestion=null;
		private int mUid=0;
		private String mToken=null; 
		private int mPlaceId=0;
		public SubmitQuestionRunnable(String question, int uid, String token, int placeid){
			mQuestion=question; 
			mUid=uid;
			mPlaceId=placeid; 
			mToken=token; 
		}
		public void run() {
        	String url = String.format(getString(R.string.url_submit_question), mPlaceId, mUid, mToken, 5, mQuestion); 
        	SharedPreferences prefs = getSharedPreferences("data", 0); 
            String webhome = prefs.getString("webhome", "http://10.0.2.2:3000"); 
            url = webhome + url; 
            url = url.replaceAll(" ", "%20");
            url = url.replaceAll("\n", "%20");
            HttpGet getMethod=new HttpGet(url); 	        	
            try {
                ResponseHandler<String> responseHandler=new BasicResponseHandler();        			
    			String responseBody=client.execute(getMethod, responseHandler);
    			Log.i(TAG, responseBody);
    			//2. parse JSON
    			// TODO: this might need to change for API change. 
    			JSONArray results = new JSONArray('['+responseBody+']');
    			JSONObject obj = results.getJSONObject(0);
    			int result = obj.getInt("status"); 
    			if (result ==1){
    				int q_id = obj.getInt("question_id"); 
    				//Toast.makeText(AskQuestionActivity.this, "ok. question id="+q_id, Toast.LENGTH_SHORT).show();
    				//TODO: where to go? 
    			}else{
    				//Toast.makeText(AskQuestionActivity.this, "faild", Toast.LENGTH_SHORT).show();
    				//TODO: where to go?     				
    			}
                //3. to notify refress list
            	handler.sendMessage(handler.obtainMessage());
            }
            catch (Throwable t) {
                // just end the background thread
            	Log.e(TAG, t.toString());
            }
            
        }
	}
    private static final String TAG = "LBSC";
}
