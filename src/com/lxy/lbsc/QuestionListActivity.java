/**
 * 
 */
package com.lxy.lbsc;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lxy.lbsc.PlacelistActivity.GetPlaceRunnable;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author LIU
 *
 */
public class QuestionListActivity extends ListActivity {
	private HttpClient client;
	private String mPlaceName=null;
	private int mPlaceId=0;
	AtomicBoolean isRunning=new AtomicBoolean(false);
	ArrayList<QuestionModel> list=new ArrayList<QuestionModel>();
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
	        	setListAdapter(new QuestionlistAdapter(QuestionListActivity.this, list));
	        	getListView().setOnItemSelectedListener(listener);
		    }
		};
	AdapterView.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener() {
			View lastRow=null;
			public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
				if (lastRow!=null) {
					lastRow.setBackgroundColor(0x00000000);
				}			
				view.setBackgroundResource(R.layout.active_row);
				lastRow=view;
			}
			
			public void onNothingSelected(AdapterView<?> parent) {
				if (lastRow!=null) {
					lastRow.setBackgroundColor(0x00000000);
					lastRow=null;
				}
			}
		};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.questionlistactivity);
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
            	Toast.makeText(QuestionListActivity.this, "Handle faverate", Toast.LENGTH_SHORT).show();            	
            }  
        }); 
		
        ImageButton btn_submit = (ImageButton)findViewById(R.id.btn_question_submit);
        btn_submit.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(QuestionListActivity.this, "To submit question", Toast.LENGTH_SHORT).show();            	
            }  
        }); 	
	}
	
	@Override
	public void onStart() {
		super.onStart();
		isRunning.set(true);
		Thread background=new Thread(new GetQuestionListRunnable() );
        background.start();		
	}
	
	@Override
	public void onStop() {
        super.onStop();
        isRunning.set(false);
    }
	
    public class GetQuestionListRunnable implements Runnable {
		private void parseQuestionList(String ret){
			try{
				JSONArray obj_array = new JSONArray(ret);
				if (obj_array.length()==0){
					Log.e(TAG, "ERROR in getting question list");
					return;
				}
    			for(int i=0; i< obj_array.length(); i++){
	    		    JSONObject obj = obj_array.getJSONObject(i);
	    		    QuestionModel question = new QuestionModel();
    		    	question.place = mPlaceName;
    		    	question.place_id = mPlaceId;
    		    	question.question_id = obj.getInt("question_id");
    		    	question.question = obj.getString("description");
    		    	if (question.question.trim().length()==0){
	    		    	question.question = "Nate, 这里不能空 :)"; 
	    		    }
    		    	question.vote_count = obj.getInt("votes_sum"); 
    		    	question.answered_count = obj.getInt("answers_count"); 
	    		    list.add(question);
    			}   	    		
			}catch (JSONException e){
				Log.e(TAG, e.toString());
			}			
		}
		
    	public void run() {
        	String url = String.format(getString(R.string.url_newquestion_at_place), mPlaceId);
        	list.clear(); 
        	SharedPreferences prefs = getPreferences(0); 
            String webhome = prefs.getString("webhome", "http://10.0.2.2:3000"); 
            url = webhome + url; 
            HttpGet getMethod=new HttpGet(url); 	
        	
            try {
                //1. query webserver
            	ResponseHandler<String> responseHandler=new BasicResponseHandler();        			
            	String responseBody=client.execute(getMethod, responseHandler);
    			Log.i(TAG, responseBody);
    			parseQuestionList(responseBody);     			
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
