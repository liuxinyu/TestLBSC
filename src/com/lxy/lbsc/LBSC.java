package com.lxy.lbsc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView; 
import android.widget.Toast;

public class LBSC extends Activity {
	GridView menuGrid; 
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        menuGrid = (GridView) findViewById(R.id.maingrid);
        menuGrid.setAdapter(new ImageAdapter(this));
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {                
                if(position==0){ // place
                	Intent intent = new Intent(LBSC.this, PlacelistActivity.class);
                    startActivity(intent);
                }else if (position==2){ // me 
                	Intent intent = new Intent(LBSC.this, login_mgmt.class);
                    startActivity(intent);
                }
                else{
                	Toast.makeText(LBSC.this, "" + position, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    
    
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;

	    public ImageAdapter(Context c) {
	        mContext = c;
	    }
	    public int getCount() {
	        return mThumbIds.length;
	    }
	    public Object getItem(int position) {
	        return null;
	    }
	    public long getItemId(int position) {
	        return 0;
	    }
	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(160, 160));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(10, 10, 10, 10);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }

	    // references to our images
	    private Integer[] mThumbIds = {
	            R.drawable.grid_place, 
	            R.drawable.grid_question,
	            R.drawable.grid_me,
	            R.drawable.grid_setting
	    };
	}
}