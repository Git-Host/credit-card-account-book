package kr.ac.hansung;

import java.text.DecimalFormat;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

public class CardAccountBookActivity extends Activity implements CardList {

	// Nexus One, Nexus S, Gallaxy Nexus ContentProvider Uri
	private final static String REFERENCE_PHONE_URI = "content://sms//inbox";
	private final static String SAMSUNG_GALLEXY_S2_URI = "content://com.sec.mms.provider/message";
	private final static String SAMSUNG_GALLEXY_A_URI = "content://com.btb.sec.mms.provider/message";
	private final static String LG_URI = "content://com.lge.messageprovider/msg/inbox";

	private final static int SHOW_DATE_PICKER_FROM = 0;
	private final static int SHOW_DATE_PICKER_TO = 1;

	private static final String INITIAL_FLAG = "initial";
	private SharedPreferences pref;
	private ImageView myCardBtn, detailViewBtn, chartViewBtn, optionViewBtn;

	private SMSReceiver smsReceiver;
	private String DELIVERED = "SMS_DELIVERED";

	private TextView fromDateView;
	private TextView toDateView;
	private TextView priceTitleView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Button Create
		myCardBtn = (ImageView) findViewById(R.id.my_card_btn);
		detailViewBtn = (ImageView) findViewById(R.id.detail_view_btn);
		chartViewBtn = (ImageView) findViewById(R.id.breakdown_stats_btn);
		optionViewBtn = (ImageView) findViewById(R.id.option_btn);

		pref = getSharedPreferences("initial", MODE_PRIVATE);
		boolean text = pref.getBoolean(INITIAL_FLAG, false);
		
		String modelNumber = getDeviceModelNumber();
		String cpUri;

		// Device에 따라 적절한 ContentProvider 선택해줌
		if (modelNumber.equals(getResources().getString(R.string.mNum_gallexy_s_2_LTE_LG))
				|| modelNumber.equals(getResources().getString(R.string.mNum_gallexy_s_2_SK))) {
			cpUri = SAMSUNG_GALLEXY_S2_URI;
		} else {
			cpUri = REFERENCE_PHONE_URI;
		}
		
		// inbox msg to DBki
		if (text == false) {
			initialInboxToDB(cpUri);
		}

		// 메인화면 현재월 1일부터 현재월 현재일까지 보여주는 메소드
		fromToDateChange();
		
		// 현재사용금액을 업데이트한다.
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
						CardAccountBookActivity.this, OptionViewActivity.class);
				startActivity(OptionViewIntent);
			}
		});

		// SMS BroadcastReceiver
		smsReceiver = new SMSReceiver();
		registerReceiver(smsReceiver, new IntentFilter(DELIVERED));
	}

	/**
	 * Method getDeviceModeNumber Android Device Model Number를 얻어온다.
	 * @return String Model Number
	 */
	public String getDeviceModelNumber() {
		String modelNumber = Build.MODEL;
		return modelNumber;
	}
	
	
	// 처음 앱 설치시 기존 SMS를 AppDB에 저장하는 Method
	/**
	 * Method initialInboxToDB 각 제조사에 맞는 CP로 SMS에 점근하여 결제SMS를 추출하고 DB에 INSERT하는 메소드.
	 * 앱을 처음 설치했을때 한번만 동작함.
	 * 상세내역 및 나의카드 INSERT.
	 * @param cpUri 각 제조사에 맞는 Content Provider URI
	 */
	public void initialInboxToDB(String cpUri) {
		SQLiteDatabase db;
		CardDB Cdb = new CardDB(this);
		String smsBody = "";
		String smsAddress = "";
		Resources tmpRes = this.getResources();

		Uri READ_SMS = Uri.parse(cpUri);
		Cursor cursor = getContentResolver().query(READ_SMS, null, null, null, null);
		db = Cdb.getReadableDatabase();
		String modelNumber = getDeviceModelNumber();
		
		while (cursor.moveToNext()) {
		
			// GallexyS2LTE-LG, GallexyS2-SK
			if (modelNumber.equals(tmpRes.getString(R.string.mNum_gallexy_s_2_LTE_LG))
					|| modelNumber.equals(tmpRes.getString(R.string.mNum_gallexy_s_2_SK))) {
				String curAddress = cursor.getString(cursor.getColumnIndex("MDN1st"));
				if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB))
						|| curAddress.equals(tmpRes.getString(R.string.phoneNum_NH))) {
					smsBody = cursor.getString(cursor.getColumnIndex("Title"));
					smsAddress = cursor.getString(cursor.getColumnIndex("MDN1st"));
					
					db.execSQL(SmsInfo.scatterMessage(smsAddress, smsBody));
				}
				
			// Nexus S, GallexyS2-KT, PRADA3.0-KT
			} else {
				String curAddress = cursor.getString(cursor.getColumnIndex("address"));
				if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_NH))) {
					smsBody = cursor.getString(cursor.getColumnIndex("body"));
					smsAddress = cursor.getString(cursor.getColumnIndex("address"));
					db.execSQL(SmsInfo.scatterMessage(smsAddress, smsBody));
				}
			} 
		}
		cursor.close();

		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 4, 30, '이마트', 21000, '주식', '1*2*', 20120430);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드', 2012, 5, 30, '삼마트', 40000, '술/유흥', '1*2*', 20120530);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민체크' , 2012, 5, 1, '사마트', 5000, '의류비', '3*6*', 20120501);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민체크' , 2012, 5, 2, '토마트삼마트이마트오마트뽱뽱예압베이베', 12000, '대중교통', '3*6*', 20120502);");

		cursor = getContentResolver().query(READ_SMS, null, null, null, null);
		String myCardQuery = "SELECT DISTINCT cardName, cardNumber FROM breakdowstats;";
		cursor = db.rawQuery(myCardQuery, null);

		while (cursor.moveToNext()) {
			String tmpCardName = cursor.getString(cursor
					.getColumnIndex("cardName"));
			String tmpCardNumber = cursor.getString(cursor
					.getColumnIndex("cardNumber"));
//			db.execSQL("CREATE TABLE myCard (myCardKey INTEGER PRIMARY KEY, cardName TEXT, cardNumber TEXT, paymentDay INTEGER, tAmount INTEGER, cardType TEXT);");
			String tmpQuery = "INSERT INTO myCard VALUES( null, '" + tmpCardName + "', '" + tmpCardNumber + "', 0, 0, '');";
			db.execSQL(tmpQuery);
		}
		db.close();

		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(INITIAL_FLAG, true);
		ed.commit();
	}

	/**
	 * Method fromToDateChange Main화면의 기간별 DatePicker를 보여주기 위한 메소드
	 */
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

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			int month = monthOfYear + 1;
			toDateView.setText(year + ". " + month + ". " + dayOfMonth + ". ");
			showNowPayment();
		}
	};

	private DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			int month = monthOfYear + 1;
			fromDateView.setText(year + ". " + month + ". " + dayOfMonth + ". ");
			showNowPayment();
		}
	};

	/**
	 * Method showNowPayment 현재사용금액을 기간에 따라 갱신해주는 메소드
	 */
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

		String nowPayQuery = "SELECT price FROM breakdowstats WHERE breakdowstats.combineDate >=" + dateFormat.format(tmpFromDate) +
				" AND breakdowstats.combineDate <= " + dateFormat.format(tmpToDate) +";";
		cursor = db.rawQuery(nowPayQuery, null);
		
		while (cursor.moveToNext()) {
			String cPrice = cursor.getString(cursor.getColumnIndex("price"));
			
			priceTitle = priceTitle + Integer.parseInt(cPrice);
		}
		
		db.close();

		DecimalFormat df = new DecimalFormat("#,##0");
		String tmpPriceTitle = df.format(priceTitle) + "원";
		
		priceTitleView = (TextView) findViewById(R.id.today_payment);
		priceTitleView.setText(tmpPriceTitle);
		priceTitleView.invalidate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(smsReceiver);
		Log.e("Junu", "onDestroy() called");
	}
}