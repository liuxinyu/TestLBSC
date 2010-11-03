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

import com.lxy.lbsc.QuestionListActivity.GetQuestionListRunnable;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
	ArrayList<AnswersModel> list=new ArrayList<AnswersModel>();
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1==1){ // list answewrs succeed
				setListAdapter(new AnswersAdapter(QuestionDetailActivity.this, list));
				getListView().setOnItemSelectedListener(listener);
				//Toast.makeText(QuestionListActivity.this, "ok. question id="+msg.arg2, Toast.LENGTH_SHORT).show();
				//EditText et= (EditText)findViewById(R.id.edit_question); 
				//et.setText(null); 
				//isRunning.set(true);
				//Thread background=new Thread(new GetQuestionListRunnable() );
			    //background.start();
			}else if (msg.arg2 == 1){
				//Toast.makeText(QuestionListActivity.this, "meet exception in submit questions", Toast.LENGTH_SHORT).show();
				//TODO: very likely user didn't loggined in. To clear loggin info and re-direct to loggin page? 
			}else if (msg.arg2 == 2){
				//Toast.makeText(QuestionListActivity.this, "server result=0", Toast.LENGTH_SHORT).show();
			}
			else{
				//setListAdapter(new QuestionlistAdapter(QuestionListActivity.this, list));
				//getListView().setOnItemSelectedListener(listener);
			}		        
		}};
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
	
	@Override
	public void onStart() {
		super.onStart();
		isRunning.set(true);
		Thread background=new Thread(new GetAnswerListRunnable() );
        background.start();		
	}
	
	@Override
	public void onStop() {
        super.onStop();
        isRunning.set(false);
    }
	
	public class GetAnswerListRunnable implements Runnable {
		private void parseQuestionList(String ret, Message msg){
			msg.arg1=0;
            msg.arg2=0; 
            // arg1 = 0 failed / 1 okay
            // 		when arg1==0, arg2 = 0 exception/ 1 server result=0 / 
			try{
				JSONArray obj_array = new JSONArray(ret);
				if (obj_array.length()==0){
					Log.e(TAG, "ERROR in getting question list");
					msg.arg2=1; 
					return;
				}
				int ret1 = obj_array.getJSONObject(0).getInt("result");
    			if (ret1!=1){ // return 'result=0' from server. 
    				msg.arg2=1; 
    				return; 
    			}  
    			for(int i=1; i< obj_array.length(); i++){
	    		    JSONObject obj = obj_array.getJSONObject(i);
	    		    AnswersModel answer = new AnswersModel();
	    		    answer.answer = obj.getString("description"); 
	    		    answer.points = obj.getInt("up_counts") - obj.getInt("down_counts"); 
	    		    answer.answer_id = obj.getInt("answer_id"); 
    		    	if (answer.answer.trim().length()==0){
    		    		answer.answer = "Nate, ���ﲻ�ܿ� :)"; 
	    		    }    		    	
	    		    list.add(answer);
    			}   	
    			msg.arg1=1; 
			}catch (JSONException e){
				Log.e(TAG, e.toString());
			}
			
		}
		
    	public void run() {
        	String url = String.format(getString(R.string.url_answers_list), mQuestionId);
        	list.clear(); 
        	SharedPreferences prefs = getSharedPreferences("data", 0); 
            String webhome = prefs.getString("webhome", "http://10.0.2.2:3000"); 
            url = webhome + url; 
            Message msg = handler.obtainMessage(); 
            
            try {
            	HttpGet getMethod=new HttpGet(url); 
            	ResponseHandler<String> responseHandler=new BasicResponseHandler();        			
            	String responseBody=client.execute(getMethod, responseHandler);
    			Log.i(TAG, responseBody);
    			parseQuestionList(responseBody, msg);     			
            }
            catch (Throwable t) {
            	Log.e(TAG, t.toString());
            }     
            handler.sendMessage(handler.obtainMessage());
        }
	}
	
	class AnswersAdapter extends ArrayAdapter<AnswersModel> {
		AnswersAdapter(ArrayList<AnswersModel> list) {
			super(QuestionDetailActivity.this, R.layout.answers_row, list);
		}		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row=convertView;
			AnswersModel model=list.get(position); //getModel(position);
			if (row==null) {				
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.answers_row, parent, false);
				row.setTag(R.id.points, row.findViewById(R.id.points));
				row.setTag(R.id.img_people, row.findViewById(R.id.img_people));
				row.setTag(R.id.text_answer, row.findViewById(R.id.text_answer));
				row.setTag(R.id.btn_up, row.findViewById(R.id.btn_up));
				row.setTag(R.id.btn_down, row.findViewById(R.id.btn_down));
			}
			TextView label1=(TextView)row.getTag(R.id.points);
			TextView label2=(TextView)row.getTag(R.id.text_answer);	
			label1.setText(model.points);
			label2.setText(model.toString());
			Log.i(TAG, "Ok in get/set answers. Position="+position+"Answer="+model.toString());
			return(row);			
		}
	}
	
	class AnswersModel{
		String answer;
		int answer_id=0;
		int points=0;
		//TODO: add resource for image		
		public String toString() {
			return(answer);
		}		
	}
	final private String TAG = "LBSC"; 
}
