/**
 * 
 */
package com.lxy.lbsc;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ListActivity;
import android.os.Bundle;
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
public class QuestionDetailActivity extends ListActivity {
	private HttpClient client;
	private String mPlaceName=null;
	private int mPlaceId=0;
	private String mQuestion=null;
	private int mQuestionId=0; 
	AtomicBoolean isRunning=new AtomicBoolean(false);
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.question_detail_activity);
        client=new DefaultHttpClient();
        Bundle bundle = getIntent().getExtras();    
        mPlaceName=bundle.getString("place_name");
        mPlaceId=bundle.getInt("place_id"); 
        TextView tv = (TextView)findViewById(R.id.question_placename); 
        tv.setText(mPlaceName); 
        mQuestion=bundle.getString("question_name");
        mQuestionId=bundle.getInt("question_id"); 
        tv = (TextView)findViewById(R.id.question_description);
        tv.setText(mQuestion); 
                
        ImageButton btn_faverate = (ImageButton)findViewById(R.id.btn_faverate);
        btn_faverate.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(QuestionDetailActivity.this, "Handle faverate", Toast.LENGTH_SHORT).show();            	
            }  
        }); 
		
        ImageButton btn_submit = (ImageButton)findViewById(R.id.btn_question_submit);
        btn_submit.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(QuestionDetailActivity.this, "To submit answer", Toast.LENGTH_SHORT).show();            	
            }  
        }); 	
	}	
}
