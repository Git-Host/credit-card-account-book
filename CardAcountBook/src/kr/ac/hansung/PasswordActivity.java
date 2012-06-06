package kr.ac.hansung;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;

public class PasswordActivity extends Activity {

	EditText p1, p2, p3, p4;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_layout);

		SharedPreferences pref = getSharedPreferences("Pref",
				Activity.MODE_PRIVATE);

		Boolean setPass = false;
		Boolean defValue = false;
		String def = null;
		setPass = pref.getBoolean("setPass", defValue);
		password = pref.getString("password", def);
		Log.v("pass", password);

		int maxLength = 1;
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(maxLength);

		p1 = (EditText) findViewById(R.id.pass1);
		p1.setInputType(InputType.TYPE_CLASS_NUMBER);
		p1.setTransformationMethod(new PasswordTransformationMethod());
		p1.setFilters(filterArray);

		p2 = new EditText(this);
		p2.setFilters(filterArray);
		p2.setInputType(InputType.TYPE_CLASS_NUMBER);
		p2.setTransformationMethod(new PasswordTransformationMethod());

		p3 = new EditText(this);
		p3.setFilters(filterArray);
		p3.setInputType(InputType.TYPE_CLASS_NUMBER);
		p3.setTransformationMethod(new PasswordTransformationMethod());

		p4 = new EditText(this);
		p4.setFilters(filterArray);
		p4.setInputType(InputType.TYPE_CLASS_NUMBER);
		p4.setTransformationMethod(new PasswordTransformationMethod());

		Log.v("setPass", "" + setPass);

		if (!setPass) {
			Intent intent = new Intent(PasswordActivity.this,
					CardAccountBookActivity.class);
			startActivity(intent);
		} else if (setPass) {
			
		}

	}

	public class CustomEditText extends EditText {

		public CustomEditText(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

	}

}
