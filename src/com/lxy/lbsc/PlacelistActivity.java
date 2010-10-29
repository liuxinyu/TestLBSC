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
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Handler; 
/**
 * @author LXY
 *
 */
public class PlacelistActivity extends ListActivity {
	private HttpClient client;
	GridView toolbarGrid;
	private final int TOOLBAR_ITEM_HOT = 0;
	private final int TOOLBAR_ITEM_NEARBY = 1;
	private final int TOOLBAR_ITEM_FAVERATE = 2;
	private final int TOOLBAR_ITEM_BACK = 3;
	private final int TOOLBAR_ITEM_SEARCH = 4;
	private int mPlacelistMode = TOOLBAR_ITEM_HOT; 
	AtomicBoolean isRunning=new AtomicBoolean(false);
	ArrayList<PlaceModel> list=new ArrayList<PlaceModel>();
	
	Handler handler = new Handler(){
		@Override
	    public void handleMessage(Message msg) {
	        //update listview			        	
        	setListAdapter(new PlacelistAdapter(list));
	    }
	}; 	
	
	public class GetPlaceRunnable implements Runnable {
		public void run() {
        	String url = getString(R.string.url_hotplace);;
        	list.clear(); 
        	if (mPlacelistMode==TOOLBAR_ITEM_HOT){
        		
        	}
        	else if (mPlacelistMode==TOOLBAR_ITEM_NEARBY){
        		list.add(new PlaceModel(getString(R.string.place_list1)));
        		url = String.format(getString(R.string.url_nearbyplace), "39.980543", "116.321000");
        	}else if (mPlacelistMode==TOOLBAR_ITEM_FAVERATE){
        		url = String.format(getString(R.string.url_favorate_place), "3");
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
    			//2. parse JSON
    			JSONArray results = new JSONArray(responseBody);
    			for(int i=0; i< results.length(); i++){
    		        try{
    		            //Get My JSONObject and grab the String Value that I want.
    		        	JSONObject obj = results.getJSONObject(i);
    		        	PlaceModel place = new PlaceModel(obj.getString("place_name")); 
    		        	place.place_id = obj.getInt("place_id");
    		        	place.activities_count = obj.getInt("activities_count"); 
    		        	place.questions_count = obj.getInt("questions_count"); 
    		        	place.unanswered_count = obj.getInt("unanswered_count"); 
    		        	list.add(place);
    		        }catch(JSONException e){
    		        }
    		    }
    			if (mPlacelistMode==TOOLBAR_ITEM_NEARBY){
    				list.add(new PlaceModel(getString(R.string.place_list2)));
    				list.add(new PlaceModel(getString(R.string.place_list_test1)));
    				list.add(new PlaceModel(getString(R.string.place_list_test2)));
    				list.add(new PlaceModel(getString(R.string.place_list_test3)));
    			}
                //3. to notify refress list
            	handler.sendMessage(handler.obtainMessage());
            }
            catch (Throwable t) {
                // just end the background thread
            }
            
        }
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.placelist);		
		client=new DefaultHttpClient();
		ImageButton question = (ImageButton)findViewById(R.id.button_question);
		question.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(PlacelistActivity.this, "TO list questions", Toast.LENGTH_SHORT).show();
            	Intent intent = new Intent(PlacelistActivity.this, QuestionList.class);
                startActivity(intent);
            }  
        }); 
		
		// �����ײ��˵� Toolbar
		toolbarGrid = (GridView) findViewById(R.id.GridView_toolbar);
		//toolbarGrid.setBackgroundResource(R.drawable.channelgallery_bg);// ���ñ���
		toolbarGrid.setNumColumns(5);// ����ÿ������
		toolbarGrid.setGravity(Gravity.CENTER);// λ�þ���
		toolbarGrid.setVerticalSpacing(10);// ��ֱ���
		toolbarGrid.setHorizontalSpacing(10);// ˮƽ���
		toolbarGrid.setAdapter(new ImageAdapter(this));
		toolbarGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String str=null;
				if (mPlacelistMode==arg2) {
					Toast.makeText(PlacelistActivity.this,"����94����",Toast.LENGTH_SHORT).show();
					return; 
				}
				switch (arg2) {
				case TOOLBAR_ITEM_HOT:
					{
						mPlacelistMode = TOOLBAR_ITEM_HOT; 					
						if (isRunning.get()!=true){
							isRunning.set(true);
						}
						Thread background=new Thread(new GetPlaceRunnable() );
						background.start();	
						str = "�ȵ�";
						break;
					}
				case TOOLBAR_ITEM_NEARBY:
					{
						mPlacelistMode = TOOLBAR_ITEM_NEARBY;					
						if (isRunning.get()!=true){
							isRunning.set(true);
						}
						Thread background=new Thread(new GetPlaceRunnable() );
						background.start();
						str = "����";
						break;
					}
				case TOOLBAR_ITEM_FAVERATE:
					{
						mPlacelistMode = TOOLBAR_ITEM_FAVERATE; 
						if (isRunning.get()!=true){
							isRunning.set(true);
						}
						Thread background=new Thread(new GetPlaceRunnable() );
						background.start();
						str = "��ע";
						break;
					}
				case TOOLBAR_ITEM_BACK:
					str = "����";
					break;
				case TOOLBAR_ITEM_SEARCH:
					str = "����";
					break;
				}
				Toast.makeText(PlacelistActivity.this,str,Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		//to-do: to query web service for place list, and parse JSON file. 		
		isRunning.set(true);
		Thread background=new Thread(new GetPlaceRunnable() );
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
    	PlaceModel place = getModel(position);     	
    	if(place.questions_count>0){
    		Toast.makeText(this, place.toString()+ "-ȥ�����б���ͼ",2000).show(); 
    		Intent intent = new Intent();  
            intent.setClass(PlacelistActivity.this, QuestionListActivity.class);  
            Bundle mBundle = new Bundle();  
            mBundle.putString("place_name", place.place);//ѹ������
            mBundle.putInt("place_id", place.place_id);
            intent.putExtras(mBundle);  
            startActivity(intent);    	
    	}else{
    		Toast.makeText(this, place.toString()+ "-ȥ������",2000).show();
    		Intent intent = new Intent();  
            intent.setClass(PlacelistActivity.this, AskQuestionActivity.class);  
            Bundle mBundle = new Bundle();  
            mBundle.putString("place_name", place.place);//ѹ������
            mBundle.putInt("place_id", place.place_id);
            intent.putExtras(mBundle);  
            startActivity(intent);    		
    	}    	
    }
	
    private PlaceModel getModel(int position) {
		return(((PlacelistAdapter)getListAdapter()).getItem(position));
	}
    
	class PlacelistAdapter extends ArrayAdapter<PlaceModel> {
		PlacelistAdapter(ArrayList<PlaceModel> list) {
			super(PlacelistActivity.this, R.layout.placerow, list);
		}		
		@Override
		public boolean isEnabled(int position) {
			boolean enabled = true; 			
			PlaceModel model=getModel(position);
			if(model.toString().startsWith("-")){
				enabled = false; 
			}
			return enabled; 
		}		
		@Override
		public boolean areAllItemsEnabled(){
			return false; 
		}		
		public View getView(int position, View convertView, ViewGroup parent) {
			View row=convertView;
			PlaceModel model=getModel(position);
			boolean isHeader = false; 
			if(model.toString().startsWith("-")){
				isHeader = true; 
			}			
			if (row==null) {				
				LayoutInflater inflater=getLayoutInflater();
				row=inflater.inflate(R.layout.placerow, parent, false);
				row.setTag(R.id.icon_place, row.findViewById(R.id.icon_place));
				row.setTag(R.id.place_name, row.findViewById(R.id.place_name));
				row.setTag(R.id.place_number_of_access, row.findViewById(R.id.place_number_of_access));
				row.setTag(R.id.place_number_of_questions, row.findViewById(R.id.place_number_of_questions));
				row.setTag(R.id.place_number_of_questions2, row.findViewById(R.id.place_number_of_questions2));
			}
			ImageView img = (ImageView)row.getTag(R.id.icon_place); 
			TextView label1=(TextView)row.getTag(R.id.place_name);
			TextView label2=(TextView)row.getTag(R.id.place_number_of_access);	
			TextView label3=(TextView)row.getTag(R.id.place_number_of_questions);	
			TextView label4=(TextView)row.getTag(R.id.place_number_of_questions2);	
			label1.setText(model.toString());
			label2.setText(String.format(getString(R.string.activities_count), 
					model.activities_count));
			label3.setText(String.format(getString(R.string.questions_count), 
					model.questions_count));
			label4.setText(String.format(getString(R.string.unanswered_count), 
					model.unanswered_count));
			Log.i(TAG, "Ok in get/set name label. Position="+position+"Name="+model.toString());
			if(isHeader){
				label2.setVisibility(View.INVISIBLE); 
				label3.setVisibility(View.INVISIBLE); 
				label4.setVisibility(View.INVISIBLE); 				
				img.setVisibility(View.INVISIBLE); 
			}else{
				// todo: need to judge whether label2/3/4 has value first
				label2.setVisibility(View.VISIBLE); 
				label3.setVisibility(View.VISIBLE); 
				label4.setVisibility(View.VISIBLE); 				
				img.setVisibility(View.VISIBLE); 
			}
			return(row);			
		}
	}
	
    class PlaceModel {
		String place;
		int place_id=0;
		int activities_count=0;
		int questions_count=0;
		int unanswered_count=0; 		
		PlaceModel(String name) {
			this.place=name;
		}		
		public String toString() {
			return(place);
		}		
	}
    private static final String TAG = "LBSC";
}
