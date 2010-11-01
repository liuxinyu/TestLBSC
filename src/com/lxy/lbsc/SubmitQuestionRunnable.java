package com.lxy.lbsc;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.content.Context; 

public class SubmitQuestionRunnable implements Runnable {
	private String mQuestion=null;
	private int mUid=0;
	private String mToken=null; 
	private int mPlaceId=0;
	private Handler mHandler=null;
	private HttpClient client;
	private String mWebHome=null;
	private Context mContext; 
	public SubmitQuestionRunnable(String question, int uid, String token, int placeid, 
			Handler handler, String webhome, Context context){
		mQuestion=question; 
		mUid=uid;
		mPlaceId=placeid; 
		mToken=token; 
		mHandler=handler;
		mWebHome=webhome;
		mContext=context; 
	}
	public void run() {
		Message msg = mHandler.obtainMessage();
		msg.arg1 = 0;
		try {
			client=new DefaultHttpClient();
        	String url = String.format(mContext.getResources().getString(R.string.url_submit_question), mPlaceId, mUid, mToken, 5, mQuestion); 
        	url = mWebHome + url; 
            url = url.replaceAll(" ", "%20");
            url = url.replaceAll("\n", "%20");
        	HttpGet getMethod=new HttpGet(url); 	
            ResponseHandler<String> responseHandler=new BasicResponseHandler();        			
			String responseBody=client.execute(getMethod, responseHandler);
			Log.i(TAG, responseBody);
			//2. parse JSON
			JSONArray results = new JSONArray(responseBody);
			JSONObject obj = results.getJSONObject(0);
			int result = obj.getInt("result"); 
			if (result ==1){
				JSONObject q_obj = results.getJSONObject(1);
				msg.arg1 = 1; 
				msg.arg2 = q_obj.getInt("question_id");    				 
			}else{
				msg.arg2 = 2; 
				//Log.e(TAG, "raise question failed");
			}
            //3. to notify refress list
			mHandler.sendMessage(msg);
        }
        catch (Throwable t) {
            msg.arg2 = 1; // exception
            mHandler.sendMessage(msg);
        	Log.e(TAG, t.toString());
        }
    }
	private static final String TAG = "LBSC";
}
