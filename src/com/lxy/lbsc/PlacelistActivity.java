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
import android.widget.ListView;
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
    
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
    	Toast.makeText(this, getModel(position).toString(),4000).show();
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
			RowModel model=getModel(position);
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
			RowModel model=getModel(position);
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
				//row.setTag(R.id.place_number_of_access, row.findViewById(R.id.place_number_of_access));
			}
			ImageView img = (ImageView)row.getTag(R.id.icon_place); 
			TextView label1=(TextView)row.getTag(R.id.place_name);
			TextView label2=(TextView)row.getTag(R.id.place_number_of_access);	
			TextView label3=(TextView)row.getTag(R.id.place_number_of_questions);	
			TextView label4=(TextView)row.getTag(R.id.place_number_of_questions2);	
			label1.setText(model.toString());
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
    		"- 有问题", 
    		"2-ipsum", 
    		"3-dolor", 
    		"4-sit", 
    		"5-amet",
			"- 待发现", 
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
