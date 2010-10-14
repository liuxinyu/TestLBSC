/**
 * 
 */
package com.lxy.lbsc;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

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
			
			/*
			if (isHeader){
				if (row==null){
					LayoutInflater inflater=getLayoutInflater();				
					row = inflater.inflate(R.layout.listheader, parent, false);
					row.setTag(R.id.place_header_name, row.findViewById(R.id.place_header_name));
				}
				TextView label1=(TextView)row.getTag(R.id.place_header_name);			
				if (label1!=null)
					label1.setText(model.toString());
			}
			else {
				*/
				if (row==null) {				
					LayoutInflater inflater=getLayoutInflater();
					row=inflater.inflate(R.layout.placerow, parent, false);
					row.setTag(R.id.place_name, row.findViewById(R.id.place_name));
					row.setTag(R.id.place_number_of_access, row.findViewById(R.id.place_number_of_access));
				}
				TextView label1=(TextView)row.getTag(R.id.place_name);			
				if (label1!=null)
					label1.setText(model.toString());
				//ImageView icon=(ImageView)row.getTag(R.id.icon);
				//icon.setImageResource(R.drawable.delete);
			//}
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
    
    String[] items={"-lorem", "ipsum", "dolor", "sit", "amet",
			"consectetuer", "adipiscing", "elit", "morbi", "vel",
			"ligula", "vitae", "arcu", "aliquet", "mollis",
			"etiam", "vel", "erat", "placerat", "ante",
			"porttitor", "sodales", "pellentesque", "augue",
			"purus"};
}
