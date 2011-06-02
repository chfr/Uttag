package com.chfr.uttag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddATMActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_atm_activity);  
        
        Button mapButton = (Button)findViewById(R.id.choose_map_button);
        mapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(AddATMActivity.this, LocationChooserActivity.class);
				AddATMActivity.this.startActivity(i);				
			}
		});
	}
}
