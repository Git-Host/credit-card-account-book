package kr.ac.hansung;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

public class CardAccountBookActivity extends Activity implements CardList {

	// Nexus One, Nexus S, Gallaxy Nexus ContentProvider Uri
	private final static String referenceUri = "content://sms//inbox";
	private final static String gallexySUri = "content://com.sec.mms.provider/message";
	private final static String gallexyAUri = "content://com.btb.sec.mms.provider/message";
	private final static String lGUri = "content://com.lge.messageprovider/msg/inbox";

	private final static int SHOW_DATE_PICKER_FROM = 0;
	private final static int SHOW_DATE_PICKER_TO = 1;

	private static final String INITIAL_FLAG = "initial";
	private SharedPreferences pref;
	private Button myCardBtn, detailViewBtn, chartViewBtn, optionViewBtn;

	private SMSReceiver smsReceiver;
	private String DELIVERED = "SMS_DELIVERED";

	private TextView fromDateView;
	private TextView toDateView;
	private TextView priceTitleView;

	// getter, setter

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Button Create
		myCardBtn = (Button) findViewById(R.id.my_card_btn);
		detailViewBtn = (Button) findViewById(R.id.detail_view_btn);
		chartViewBtn = (Button) findViewById(R.id.breakdown_stats_btn);
		optionViewBtn = (Button) findViewById(R.id.option_btn);

		pref = getSharedPreferences("initial", MODE_PRIVATE);
		boolean text = pref.getBoolean(INITIAL_FLAG, false);

		// inbox msg to DB
		if (text == false) {
			initialInboxToDB(referenceUri);
//			initialInboxToDB(gallexyUri);
		}

		// ����ȭ�� ����� 1�Ϻ��� ����� �����ϱ��� �����ִ� �޼ҵ�
		fromToDateChange();
		
		// ������ݾ��� ������Ʈ�Ѵ�.
		showNowPayment();

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
				Intent GraphViewIntent = new Intent(CardAccountBookActivity.this, GraphViewActivity.class);
				startActivity(GraphViewIntent);
			}
		});
		
		// Option View Btn Click
		optionViewBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent OptionViewIntent = new Intent(CardAccountBookActivity.this, OptionViewActivity.class);
				startActivity(OptionViewIntent);
			}
		});

		// SMS BroadcastReceiver
		smsReceiver = new SMSReceiver();
		registerReceiver(smsReceiver, new IntentFilter(DELIVERED));
	}

	// ó�� �� ��ġ�� ���� SMS�� AppDB�� �����ϴ� Method
	public void initialInboxToDB(String cpUri) {
		SQLiteDatabase db;
		CardDB Cdb = new CardDB(this);
		String smsBody = "";
		String smsAddress = "";
		Resources tmpRes = this.getResources();

		Uri READ_SMS = Uri.parse(cpUri);
		Cursor cursor = getContentResolver().query(READ_SMS, null, null, null, null);
		db = Cdb.getReadableDatabase();

		while (cursor.moveToNext()) {
			String curAddress = cursor.getString(cursor.getColumnIndex("address"));
			if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_NH))) {
				smsBody = cursor.getString(cursor.getColumnIndex("body"));
				smsAddress = cursor.getString(cursor.getColumnIndex("address"));
				db.execSQL(SmsInfo.scatterMessage(smsAddress, smsBody));
			}
			
			// ��ȯ�� ��������
//			String curAddress = cursor.getString(cursor.getColumnIndex("MDN1st"));
//			if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB))
//					|| curAddress
//							.equals(tmpRes.getString(R.string.phoneNum_NH))) {
//				smsBody = cursor.getString(cursor.getColumnIndex("Title"));
//				smsAddress = cursor.getString(cursor.getColumnIndex("MDN1st"));
//
//				db.execSQL(SmsInfo.scatterMessage(smsAddress, smsBody));
//
//			}
			
//			Log.e("jadf", cursor.getString(cursor.getColumnIndex("Title")));
//			Log.v("JUNU", cursor.getString(0)+" " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4)
//					+ " " + cursor.getString(5) + " " + cursor.getString(6)
//					+ " " + cursor.getString(7) + " " + cursor.getString(8)
//					+ " " + cursor.getString(9) + " " + cursor.getString(10));
		}
		cursor.close();

		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB����ī��' , 2012, 4, 30, '�̸�Ʈ', 21000, '�ֽ�', '1*2*', 20120430);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB����ī��', 2012, 5, 30, '�︶Ʈ', 40000, '��/����', '1*2*', 20120530);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB����üũ' , 2012, 5, 1, '�縶Ʈ', 5000, '�Ƿ���', '3*6*', 20120501);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB����üũ' , 2012, 5, 2, '�丶Ʈ�︶Ʈ�̸�Ʈ����Ʈ������к��̺�', 12000, '���߱���', '3*6*', 20120502);");

		String myCardQuery = "SELECT DISTINCT cardName, cardNumber FROM breakdowstats;";
		cursor = db.rawQuery(myCardQuery, null);

		while (cursor.moveToNext()) {
			String tmpCardName = cursor.getString(cursor.getColumnIndex("cardName"));
			String tmpCardNumber = cursor.getString(cursor.getColumnIndex("cardNumber"));

			String tmpQuery = "INSERT INTO myCard VALUES( null, '" + tmpCardName + "', '" + tmpCardNumber + "', null, null, null);";
			db.execSQL(tmpQuery);
		}
		db.close();

		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(INITIAL_FLAG, true);
		ed.commit();
	}

	public void fromToDateChange() {
		fromDateView = (TextView) findViewById(R.id.from_date_view);
		toDateView = (TextView) findViewById(R.id.to_date_view);
		final int todayYear;
		final int todayMonth;
		final int todayDay;

		Calendar today = Calendar.getInstance();
		todayYear = today.get(Calendar.YEAR);
		todayMonth = today.get(Calendar.MONTH) + 1;
		todayDay = today.get(Calendar.DAY_OF_MONTH);

		String updateToDate = todayYear + ". " + todayMonth + ". " + todayDay + ". ";
		String updateFromDate = todayYear + ". " + todayMonth + ". 1. ";

		toDateView.setText(updateToDate);
		fromDateView.setText(updateFromDate);

		toDateView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Bundle tmpBdl = new Bundle();
				tmpBdl.putInt("toYear", todayYear);
				tmpBdl.putInt("toMonth", todayMonth);
				tmpBdl.putInt("toDay", todayDay);

				showDialog(SHOW_DATE_PICKER_TO, tmpBdl);
			}
		});

		fromDateView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Bundle tmpBdl = new Bundle();
				tmpBdl.putInt("toYear", todayYear);
				tmpBdl.putInt("toMonth", todayMonth);
				tmpBdl.putInt("toDay", 1);

				showDialog(SHOW_DATE_PICKER_FROM, tmpBdl);
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle bdl) {
		switch (id) {
		case SHOW_DATE_PICKER_TO:
			return new DatePickerDialog(this, toDateSetListener,
					bdl.getInt("toYear"), bdl.getInt("toMonth") - 1,
					bdl.getInt("toDay"));

		case SHOW_DATE_PICKER_FROM:
			return new DatePickerDialog(this, fromDateSetListener,
					bdl.getInt("toYear"), bdl.getInt("toMonth") - 1,
					bdl.getInt("toDay"));
		}
		return super.onCreateDialog(id, bdl);
	}

	private DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			toDateView.setText(year + ". " + month + ". " + dayOfMonth + ". ");
			showNowPayment();
		}
	};

	private DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			fromDateView.setText(year + ". " + month + ". " + dayOfMonth + ". ");
			showNowPayment();
		}
	};

	public void showNowPayment() {
		String[] toYearMonthDay = toDateView.getText().toString().split(". ");
		String[] fromYearMonthDay = fromDateView.getText().toString().split(". ");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		int priceTitle = 0;

		Date tmpToDate = new Date();
		Date tmpFromDate = new Date();
		
		tmpToDate.setYear(Integer.parseInt(toYearMonthDay[0]) - 1900);
		tmpToDate.setMonth(Integer.parseInt(toYearMonthDay[1]) - 1);
		tmpToDate.setDate(Integer.parseInt(toYearMonthDay[2]));
		
		tmpFromDate.setYear(Integer.parseInt(fromYearMonthDay[0]) - 1900);
		tmpFromDate.setMonth(Integer.parseInt(fromYearMonthDay[1]) - 1);
		tmpFromDate.setDate(Integer.parseInt(fromYearMonthDay[2]));

		SQLiteDatabase db;
		Cursor cursor;
		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();

		String nowPayQuery = "SELECT price FROM breakdowstats WHERE breakdowstats.combineDate >=" 
							+ dateFormat.format(tmpFromDate)
							+ " AND breakdowstats.combineDate <= " + dateFormat.format(tmpToDate) +";";
		cursor = db.rawQuery(nowPayQuery, null);
		
		while (cursor.moveToNext()) {
			String cPrice = cursor.getString(cursor.getColumnIndex("price"));
			
			priceTitle = priceTitle + Integer.parseInt(cPrice);
		}
		db.close();

		priceTitleView = (TextView) findViewById(R.id.today_payment);
		priceTitleView.setText(SmsInfo.decimalPointToString(priceTitle));
		priceTitleView.invalidate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(smsReceiver);
		Log.e("Junu", "onDestroy() called");
	}
}