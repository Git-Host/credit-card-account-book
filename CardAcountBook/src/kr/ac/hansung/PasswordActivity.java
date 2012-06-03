package kr.ac.hansung;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

public class PasswordActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_layout);
		
		
	}
	public class CustomEditText extends EditText{

		public CustomEditText(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}		
		
	}
	
}
