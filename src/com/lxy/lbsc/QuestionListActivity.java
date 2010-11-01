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
import android.app.ListActivity;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
				if (msg.arg1==1){ // submit question succeed. question id in arg2; 
					Toast.makeText(QuestionListActivity.this, "ok. question id="+msg.arg2, Toast.LENGTH_SHORT).show();
					EditText et= (EditText)findViewById(R.id.edit_question); 
					et.setText(null); 
					isRunning.set(true);
					Thread background=new Thread(new GetQuestionListRunnable() );
			        background.start();
				}else if (msg.arg2 == 1){
					Toast.makeText(QuestionListActivity.this, "meet exception in submit questions", Toast.LENGTH_SHORT).show();
				}else if (msg.arg2 == 2){
					Toast.makeText(QuestionListActivity.this, "server result=0", Toast.LENGTH_SHORT).show();
				}
				else{
					setListAdapter(new QuestionlistAdapter(QuestionListActivity.this, list));
					getListView().setOnItemSelectedListener(listener);
				}		        
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
            	EditText et= (EditText)findViewById(R.id.edit_question); 
            	String str = et.getText().toString(); 
            	if (str.trim().length()==0){
            		Toast.makeText(QuestionListActivity.this, getString(R.string.err_no_question), Toast.LENGTH_SHORT).show();
            		return;
            	}
            	SharedPreferences prefs = getSharedPreferences("data", 0); 
                int uid = prefs.getInt("uid", 0);
                String token = prefs.getString("token", null);
                String webhome = prefs.getString("webhome", "http://10.0.2.2:3000");
                if (uid==0){
                	Toast.makeText(QuestionListActivity.this, "haven't logged in yet",4000).show();
                	Intent intent = new Intent(QuestionListActivity.this, login_mgmt.class);
                    startActivity(intent);
                    finish();
                    //return; 
                }
            	Toast.makeText(QuestionListActivity.this, "To submit question", Toast.LENGTH_SHORT).show(); 
            	isRunning.set(true);
        		Thread background=new Thread(new SubmitQuestionRunnable(str.trim(),uid,token, mPlaceId, 
        				handler, webhome, QuestionListActivity.this)) ;
                background.start();
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
	
	@Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	QuestionModel question = list.get(position);     	
    	Toast.makeText(this, question.question+ "-去问答视图",2000).show(); 
    	Intent intent = new Intent();  
        intent.setClass(QuestionListActivity.this, QuestionDetailActivity.class);  
        Bundle mBundle = new Bundle();  
        mBundle.putString("place_name", mPlaceName);
        mBundle.putInt("place_id", mPlaceId);
        mBundle.putString("question_name", question.question);
        mBundle.putInt("question_id", question.question_id); 
        intent.putExtras(mBundle);  
        startActivity(intent);    	    	
    }
	
    public class GetQuestionListRunnable implements Runnable {
		private void parseQuestionList(String ret){
			try{
				JSONArray obj_array = new JSONArray(ret);
				if (obj_array.length()==0){
					Log.e(TAG, "ERROR in getting question list");
					return;
				}
				int ret1 = obj_array.getJSONObject(0).getInt("result");
    			if (ret1!=1){ // return 'result=0' from server. 
    				return; 
    			}  
    			for(int i=1; i< obj_array.length(); i++){
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
        	SharedPreferences prefs = getSharedPreferences("data", 0); 
            String webhome = prefs.getString("webhome", "http://10.0.2.2:3000"); 
            url = webhome + url; 
            HttpGet getMethod=new HttpGet(url); 	
        	
            try {
            	ResponseHandler<String> responseHandler=new BasicResponseHandler();        			
            	String responseBody=client.execute(getMethod, responseHandler);
    			Log.i(TAG, responseBody);
    			parseQuestionList(responseBody);     			
            	handler.sendMessage(handler.obtainMessage());
            }
            catch (Throwable t) {
            	Log.e(TAG, t.toString());
            }            
        }
	}
   
    private static final String TAG = "LBSC";
}
