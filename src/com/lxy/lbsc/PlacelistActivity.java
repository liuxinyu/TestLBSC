/**
 * 
 */
package com.lxy.lbsc;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author LXY
 *
 */
public class PlacelistActivity extends ListActivity {
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//String[] placelist =getResources().getStringArray(R.array.placelist_array); 
		setContentView(R.layout.placelist);		
		ArrayList<RowModel> list=new ArrayList<RowModel>();
		for (String s : items) {
			list.add(new RowModel(s));
		}		
		setListAdapter(new PlacelistAdapter(list));
		
		ImageButton question = (ImageButton)findViewById(R.id.button_question);
		question.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(PlacelistActivity.this, "TO list questions", Toast.LENGTH_SHORT).show();
            }  
        });  
	}
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent data) {
    }
	
    private RowModel getModel(int position) {
		return(((PlacelistAdapter)getListAdapter()).getItem(position));
	}
    
	class PlacelistAdapter extends ArrayAdapter<RowModel> {
		PlacelistAdapter(ArrayList<RowModel> list) {
			super(PlacelistActivity.this, R.layout.placerow, list);
		}
		
		@Override
		public boolean isEnabled(int position) {
			boolean enabled = true; 			
			/*RowModel model=getModel(position);
			if(model.toString().startsWith("-")){
				enabled = false; 
			}*/
			return enabled; 
		}
		
		@Override
		public boolean areAllItemsEnabled(){
			return true; 
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			/*TextView tv;
            if (convertView == null) {
                tv = (TextView) getLayoutInflater().inflate(
                        android.R.layout.simple_expandable_list_item_1, parent, false);
            } else {
                tv = (TextView) convertView;
            }
            tv.setText(items[position]);
            return tv;*/
			
			
			View row=convertView;
			RowModel model=getModel(position);
			boolean isHeader = false; 
			if(model.toString().startsWith("-")){
				isHeader = true; 
			}			

			if (isHeader){
				if (row==null){
					LayoutInflater inflater=getLayoutInflater();				
					row = inflater.inflate(R.layout.listheader, parent, false);
					row.setTag(R.id.place_header_name, row.findViewById(R.id.place_header_name));
				}
				TextView label1=(TextView)row.getTag(R.id.place_header_name);			
				if (label1!=null){
					label1.setText(model.toString()); 
				}else{
					Log.e(TAG, "Error in get header name label in position="+position + "  Name=" + model.toString());
				}
				Log.w(TAG, "A header in position="+position + "  Name=" + model.toString());
			}
			else {
				
				if (row==null) {				
					LayoutInflater inflater=getLayoutInflater();
					row=inflater.inflate(R.layout.placerow, parent, false);
					row.setTag(R.id.place_name, row.findViewById(R.id.place_name));
					//row.setTag(R.id.place_number_of_access, row.findViewById(R.id.place_number_of_access));
				}
				TextView label1=(TextView)row.getTag(R.id.place_name);			
				if (label1!=null){
					label1.setText(model.toString());
					Log.w(TAG, "Ok in get/set name label. Position="+position+"Name="+model.toString());
				}
				else{
					Log.e(TAG, "Error in get name label. Position="+position+"Name="+model.toString());
				}
				
				//ImageView icon=(ImageView)row.getTag(R.id.icon);
				//icon.setImageResource(R.drawable.delete);
			}
			return(row);
			
		}
	}
	
    class RowModel {
		String place;		
		RowModel(String name) {
			this.place=name;
		}		
		public String toString() {
			return(this.place);
		}
	}
    
    String[] items={
    		"-1-lorem", 
    		"2-ipsum", 
    		"3-dolor", 
    		"4-sit", 
    		"5-amet",
			"6-consectetuer", 
			"7-adipiscing", 
			"8-elit", 
			"9-morbi", 
			"-10-vel",
			"11-ligula", 
			"12-vitae", 
			"13-arcu", 
			"14-aliquet", 
			"15-mollis",
			"16-etiam", 
			"17-vel", 
			"18-erat", 
			"19-placerat", 
			"20-ante",
			"21-porttitor", 
			"22-sodales", 
			"23-pellentesque", 
			"24-augue",
			"25-purus"};
    private static final String TAG = "LBSC";
}
