package com.lxy.lbsc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView; 

public class LBSC extends Activity 
	implements AdapterView.OnItemSelectedListener {
	GridView menuGrid; 
	String menu_items[]={"地点", "问题", "我", "设置" }; 
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        menuGrid = (GridView) findViewById(R.id.maingrid);
        menuGrid.setAdapter(new MaingridAdapter(this, 
        		android.R.layout.simple_list_item_1, 
        		menu_items)); 
        // menuGrid.setOnItemSelectedListener(this); 
    }
    
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
    	// selection.setText(menu_items[position]);
    	// which main item is selected
    }
        
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
    private class MaingridAdapter extends ArrayAdapter<Object>{
    	Context ctxt; 
		public MaingridAdapter(Context context, int textViewResourceId,
				String[] items) {
			super(context, textViewResourceId, items);
			// TODO Auto-generated constructor stub
			this.ctxt = context;
		}
		
		public View getview(int position, View convertView, ViewGroup parent){
			TextView label = (TextView)convertView;
			if (convertView==null){
				convertView = new TextView (ctxt);
				label = (TextView)convertView;
			}
			label.setText(menu_items[position]); 
			return parent;			
		}   	
    }       
}