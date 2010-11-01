/**
 * 
 */
package com.lxy.lbsc;

import java.util.concurrent.atomic.AtomicBoolean;
import android.widget.EditText; 
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	
	AtomicBoolean isRunning=new AtomicBoolean(false);
	Handler handler = new Handler(){
		@Override
	    public void handleMessage(Message msg) {
			if (msg.arg1==1){ // submit question succeed. question id in arg2; 
				Toast.makeText(AskQuestionActivity.this, "ok. question id="+msg.arg2, Toast.LENGTH_SHORT).show();
			}else { // failed
				if (msg.arg2 ==1){ //exception
					Toast.makeText(AskQuestionActivity.this, "Exception met", Toast.LENGTH_SHORT).show();
				}else{ // result = 0 ;
					Toast.makeText(AskQuestionActivity.this, "No exceptions, but request failed", Toast.LENGTH_SHORT).show();					
				}
			}			
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
                String webhome = prefs.getString("webhome", "http://10.0.2.2:3000");
                if (uid==0){
                	Toast.makeText(AskQuestionActivity.this, "haven't logged in yet",4000).show();
                	Intent intent = new Intent(AskQuestionActivity.this, login_mgmt.class);
                    startActivity(intent);
                    finish();
                    //return; 
                }
            	Toast.makeText(AskQuestionActivity.this, "To submit question", Toast.LENGTH_SHORT).show(); 
            	isRunning.set(true);
        		Thread background=new Thread(new SubmitQuestionRunnable(str.trim(),uid,token, mPlaceId, 
        				handler, webhome, AskQuestionActivity.this)) ;
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
	
	private static final String TAG = "LBSC";
}
