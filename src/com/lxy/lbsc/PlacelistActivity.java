/**
 * 
 */
package com.lxy.lbsc;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * @author LXY
 *
 */
public class PlacelistActivity extends ListActivity {

	String[] items={"lorem", "ipsum", "dolor", "sit", "amet",
			"consectetuer", "adipiscing", "elit", "morbi", "vel",
			"ligula", "vitae", "arcu", "aliquet", "mollis",
			"etiam", "vel", "erat", "placerat", "ante",
			"porttitor", "sodales", "pellentesque", "augue",
			"purus"};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.placelist);		
		setListAdapter(new ArrayAdapter<String>(this,
												android.R.layout.simple_list_item_1,
												items));
	}
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
		Intent data) {
    }
}
