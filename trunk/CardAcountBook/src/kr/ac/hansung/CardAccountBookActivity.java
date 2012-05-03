package kr.ac.hansung;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CardAccountBookActivity extends Activity implements CardList {

	// Nexus One, Nexus S, Gallaxy Nexus ContentProvider Uri
	private final static String inboxUri = "content://sms//inbox";
	private final static String gallexyUri = "content://com.sec.mms.provider/message";

	private static final String INITIAL_FLAG = "initial";
	private SharedPreferences pref;
	private Button myCardBtn, detailViewBtn, chartViewBtn, optionViewBtn;
		

	private SMSReceiver smsReceiver;
	private static Boolean initialFlag = false;
	private String DELIVERED = "SMS_DELIVERED";
	private static int cardDbPKey = 0;

	// getter, setter
	public void setInitFlag(Boolean flag) {
		initialFlag = flag;
	}

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Button Create
		myCardBtn = (Button) findViewById(R.id.my_card_btn);
		detailViewBtn = (Button) findViewById(R.id.detail_view_btn);
		chartViewBtn = (Button) findViewById(R.id.breakdown_stats_btn);

		optionViewBtn = (Button)findViewById(R.id.option_btn);
		

		pref = getSharedPreferences("initial", MODE_PRIVATE);
		boolean text = pref.getBoolean(INITIAL_FLAG, false);

		// inbox msg to DB
		if (text == false) {
			initialInboxToDB(inboxUri);
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
		// Chart View Btn Click
		chartViewBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent GraphViewIntent = new Intent(
						CardAccountBookActivity.this, GraphViewActivity.class);
				startActivity(GraphViewIntent);
			}
		});
		// Option View Btn Click
		optionViewBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent OptionViewIntent = new Intent(
						CardAccountBookActivity.this,OptionViewActivity.class);
				startActivity(OptionViewIntent);
			}
		});

		// SMS BroadcastReceiver
		smsReceiver = new SMSReceiver();
		// registerReceiver(new BroadcastReceiver() {
		// @Override
		// public void onReceive(Context arg0, Intent arg1) {
		// switch (getResultCode()) {
		// case Activity.RESULT_OK:
		// Toast.makeText(getBaseContext(), "SMS delivered",
		// Toast.LENGTH_SHORT).show();
		// break;
		// case Activity.RESULT_CANCELED:
		// Toast.makeText(getBaseContext(), "SMS not delivered",
		// Toast.LENGTH_SHORT).show();
		// break;
		// }
		// }
		// }, new IntentFilter(DELIVERED));
		registerReceiver(smsReceiver, new IntentFilter(DELIVERED));

	}

	// 처음 앱 설치시 기존 SMS를 AppDB에 저장하는 Method
	public void initialInboxToDB(String cpUri) {
		SQLiteDatabase db;
		CardDB Cdb = new CardDB(this);
		String smsBody = "";
		String smsAddress = "";
		String cardQuery;
		Resources tmpRes = this.getResources();

		Uri READ_SMS = Uri.parse(cpUri);
		Cursor cursor = getContentResolver().query(READ_SMS, null, null, null,
				null);
		db = Cdb.getReadableDatabase();

		while (cursor.moveToNext()) {
			String curAddress = cursor.getString(cursor
					.getColumnIndex("address"));
			if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB))
					|| curAddress
							.equals(tmpRes.getString(R.string.phoneNum_NH))) {
				smsBody = cursor.getString(cursor.getColumnIndex("body"));
				smsAddress = cursor.getString(cursor.getColumnIndex("adress"));

				db.execSQL(SmsInfo.scatterMessage(smsAddress, smsBody));

			}
		}
		
		for (int i = 0; i < cardName.length; i++) {
			cardQuery = "INSERT INTO card VALUES('" + cardName[i] + "', " + creditPeriod[i]
					+ ", " + targetPrice[i] + ", " + paymentPlan[i] + ", '" + phoneNumber[i] + "');";
			db.execSQL(cardQuery);
		}

		db.close();
		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(INITIAL_FLAG, true);
		ed.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(smsReceiver);
		Log.e("Junu", "onDestroy() called");
	}
	
}