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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class QuestionList extends ListActivity {
	private HttpClient client;
	GridView toolbarGrid;
	private final int TOOLBAR_ITEM_HOT = 0;
	private final int TOOLBAR_ITEM_NEARBY = 1;
	private final int TOOLBAR_ITEM_FAVERATE = 2;
	private final int TOOLBAR_ITEM_BACK = 3;
	private final int TOOLBAR_ITEM_SEARCH = 4;
	private int mQuestionlistMode = TOOLBAR_ITEM_HOT; 
	AtomicBoolean isRunning=new AtomicBoolean(false);
	ArrayList<QuestionModel> list=new ArrayList<QuestionModel>();
	
	Handler handler = new Handler(){
	@Override
	public void handleMessage(Message msg) {
        	setListAdapter(new QuestionlistAdapter(QuestionList.this, list));
        	getListView().setOnItemSelectedListener(listener);
	    }
	}; 	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.questionlist);		
		client=new DefaultHttpClient();
		
		ImageButton question = (ImageButton)findViewById(R.id.button_place);
		question.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(QuestionList.this, "TO list place", Toast.LENGTH_SHORT).show();
            	Intent intent = new Intent(QuestionList.this, PlacelistActivity.class);
                startActivity(intent);
            }  
        }); 
		
		RadioGroup rGroup = (RadioGroup) findViewById(R.id.toolbar_radio);
		final RadioButton button1 = (RadioButton) findViewById(R.id.radio1);
		final RadioButton button2 = (RadioButton) findViewById(R.id.radio2);
		final RadioButton button3 = (RadioButton) findViewById(R.id.radio3);
		rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
	        public void onCheckedChanged(RadioGroup group, int checkedId){
				if(checkedId==button1.getId()){
	        		button1.setButtonDrawable(R.drawable.btn_radio1_1); 
	        		button2.setButtonDrawable(R.drawable.btn_radio2_0);
	        		button3.setButtonDrawable(R.drawable.btn_radio3_0);
	        		mQuestionlistMode = TOOLBAR_ITEM_HOT; 					
					if (isRunning.get()!=true){
						isRunning.set(true);
					}
					Thread background=new Thread(new GetQuestionRunnable() );
					background.start();	
	        	}else if (checkedId==button2.getId()){
	        		button1.setButtonDrawable(R.drawable.btn_radio1_0); 
	        		button2.setButtonDrawable(R.drawable.btn_radio2_1);
	        		button3.setButtonDrawable(R.drawable.btn_radio3_0);
	        		mQuestionlistMode = TOOLBAR_ITEM_NEARBY;					
					if (isRunning.get()!=true){
						isRunning.set(true);
					}
					Thread background=new Thread(new GetQuestionRunnable() );
					background.start();
	        	}else if (checkedId==button3.getId()){
	        		button1.setButtonDrawable(R.drawable.btn_radio1_0); 
	        		button2.setButtonDrawable(R.drawable.btn_radio2_0);
	        		button3.setButtonDrawable(R.drawable.btn_radio3_1);
	        		mQuestionlistMode = TOOLBAR_ITEM_FAVERATE; 
					if (isRunning.get()!=true){
						isRunning.set(true);
					}
					Thread background=new Thread(new GetQuestionRunnable() );
					background.start();
	        	}        			
	        }
		});
	}
	

	
	@Override
	public void onStart() {
		super.onStart();
		//to-do: to query web service for place list, and parse JSON file. 		
		isRunning.set(true);
		Thread background=new Thread(new GetQuestionRunnable() );
        background.start();		
	}
	
	@Override
	public void onStop() {
        super.onStop();
        isRunning.set(false);
    }
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent data) {
    }
    
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	Toast.makeText(this, getModel(position).toString(),2000).show();
    }
    
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
	
    private QuestionModel getModel(int position) {
		return(((QuestionlistAdapter)getListAdapter()).getItem(position));
	}
    
    public class GetQuestionRunnable implements Runnable {
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
    				//Get My JSONObject and grab the String Value that I want.
	    		    JSONObject obj = obj_array.getJSONObject(i);
	    		    QuestionModel place = new QuestionModel();
	    		    place.place = obj.getString("place_name");
	    		    place.place_id = obj.getInt("place_id");
	    		    list.add(place); // actually a place name
	    		    
	    		    JSONArray q_array = obj.getJSONArray("questions");
	    		    if (q_array.length()==0){
	    		    	Log.e(TAG, "Error in getting question in place of "+place.place); 
	    		    }
	    		    for(int j=0; j<q_array.length(); j++){
	    		    	JSONObject q_obj = q_array.getJSONObject(j);
	    		    	QuestionModel question = new QuestionModel();
	    		    	question.place = place.place;
	    		    	question.place_id = place.place_id;
	    		    	question.question_id = q_obj.getInt("question_id");
		    		    question.question = q_obj.getString("description");
		    		    if (question.question.trim().length()==0){
		    		    	question.question = "Nate, ���ﲻ�ܿ� :)"; 
		    		    }
		    		    question.answered_count = q_obj.getInt("answers_count"); 
		    		    //question.vote_count = q_obj.getInt("votes_sum"); 
		    		    list.add(question);
	    		    }	    		    
    			}   	    		
			}catch (JSONException e){
				Log.e(TAG, e.toString());
			}			
		}
		
    	public void run() {
        	String url = getString(R.string.url_question_hot);;
        	list.clear(); 
        	if (mQuestionlistMode==TOOLBAR_ITEM_HOT){
        		
        	}
        	else if (mQuestionlistMode==TOOLBAR_ITEM_NEARBY){
        		//list.add(new PlaceModel(getString(R.string.place_list1)));
        		//url = String.format(getString(R.string.url_nearbyplace), "39.980543", "116.321000");
        	}else if (mQuestionlistMode==TOOLBAR_ITEM_FAVERATE){
        		//url = String.format(getString(R.string.url_favorate_place), "3");
        	}else{
        		// something but be wrong!!!
        	}
        	SharedPreferences prefs = getSharedPreferences("data", 0); 
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
            }
            
        }
	}
    
	
	private static final String TAG = "LBSC";
}
