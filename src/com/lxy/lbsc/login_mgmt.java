/**
 * 
 */
package com.lxy.lbsc;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageButton;

/**
 * @author LXY
 *
 */
public class login_mgmt extends Activity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.login_mgmt);
		Toast.makeText(login_mgmt.this, "From login_mgmt activity", Toast.LENGTH_SHORT).show();
		
		ImageButton signup = (ImageButton)findViewById(R.id.button_signupnow);
		signup.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(login_mgmt.this, "TO sign up", Toast.LENGTH_SHORT).show();
            }  
        });  
		
		ImageButton login = (ImageButton)findViewById(R.id.button_loginnow);
		login.setOnClickListener(new Button.OnClickListener(){  
            @Override  
            public void onClick(View v) {  
            	Toast.makeText(login_mgmt.this, "TO login", Toast.LENGTH_SHORT).show();
            }  
        }); 
	}
}
