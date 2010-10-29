/**
 * 
 */
package com.lxy.lbsc;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author LIU
 *
 */
public class AskQuestionActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        		WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.askquestion);
        
        Bundle bundle = getIntent().getExtras();    
        String place_name=bundle.getString("place_name");
        int place_id=bundle.getInt("place_id"); 
        TextView tv= (TextView)findViewById(R.id.question_placename); 
        tv.setText(place_name); 
                
        ImageButton btn_faverate = (ImageButton)findViewById(R.id.btn_faverate);
        btn_faverate.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(AskQuestionActivity.this, "Handle faverate", Toast.LENGTH_SHORT).show();            	
            }  
        }); 
		
        ImageButton btn_submit = (ImageButton)findViewById(R.id.btn_question_submit);
        btn_submit.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(AskQuestionActivity.this, "To submit question", Toast.LENGTH_SHORT).show();            	
            }  
        }); 		
	}
}
