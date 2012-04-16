package kr.ac.hansung;

import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CardAccountBookActivity extends Activity {
	private final static String inboxUri = "content://sms//inbox";
	private Button myCardBtn;
	private Button detailViewBtn;

	private Boolean initialFlag = false;
	String DELIVERED = "SMS_DELIVERED";
	
	
	//getter, setter
	public void setInitFlag(Boolean flag) { initialFlag = flag; } 


	private Button chartViewBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Button Create
		myCardBtn = (Button) findViewById(R.id.my_card_btn);
		detailViewBtn = (Button) findViewById(R.id.detail_view_btn);
		chartViewBtn = (Button)findViewById(R.id.breakdown_stats_btn);
		
		// inbox msg to DB
		if (initialFlag.equals(false)) {
			Uri READ_SMS = Uri.parse(inboxUri);
			Cursor cursor = getContentResolver().query(READ_SMS, null, null, null, null);

			String smsAddress = "";
			String smsBody = "";
			String modelNumber = Build.MODEL;
			
			
			while (cursor.moveToNext()) {
				if (cursor.getString(cursor.getColumnIndex("address")).equals(
						"15881600")) {
					smsAddress = cursor.getString(cursor.getColumnIndex("address"));
					smsBody = cursor.getString(cursor.getColumnIndex("body"));

					Log.e("Junu", smsAddress);
					Log.v("Junu", smsBody);
				
					spliteSMS(smsBody);
				}
			}
			setInitFlag(true);
		}

		// My Card Btn Click
		myCardBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myCardIntent = new Intent(CardAccountBookActivity.this,
						MyCardActivity.class);
				startActivity(myCardIntent);

			}
		});

		// Detail View Btn Click
		detailViewBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent detailViewIntent = new Intent(
						CardAccountBookActivity.this, DetailViewActivity.class);
				startActivity(detailViewIntent);
			}
		});
		//Chart View Btn Click
		chartViewBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent GraphViewIntent = new Intent(CardAccountBookActivity.this, GraphViewActivity.class);
				startActivity(GraphViewIntent);
			}
		});

		// SMS BroadcastReceiver
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

	}

	public void spliteSMS(String sms) {
		String[] tmpSMS = sms.split("\n");
		// Log.v("Junu", tmpSMS[0]);
		// Log.v("Junu", sms);
		// Log.v("Junu", String.valueOf(tmpSMS.length));
		// Log.v("Junu", String.valueOf(convertToIntPrice(tmpSMS[1])));

	}

	public int convertToIntPrice(String price) {
		String tmpPrice;

		tmpPrice = price.replace("¿ø", "");
		tmpPrice = tmpPrice.replace(",", "");

		return Integer.parseInt(tmpPrice);
	}
}