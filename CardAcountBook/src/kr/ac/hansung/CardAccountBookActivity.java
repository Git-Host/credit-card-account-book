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

public class CardAccountBookActivity extends Activity {

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

		MainButtonClickListener scatterIntentListener = new MainButtonClickListener();
		
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
		myCardBtn.setOnClickListener(scatterIntentListener);

		// Detail View Btn Click
		detailViewBtn.setOnClickListener(scatterIntentListener);
		
		// Chart View Btn Click
		chartViewBtn.setOnClickListener(scatterIntentListener);
		
		// Option View Btn Click
		optionViewBtn.setOnClickListener(scatterIntentListener);

		// SMS BroadcastReceiver
		smsReceiver = new SMSReceiver();
		registerReceiver(smsReceiver, new IntentFilter(DELIVERED));
	}

	public class MainButtonClickListener implements View.OnClickListener {
		public void onClick(View v) {
			Intent scatterIntent = null;
			switch (v.getId()) {
			case R.id.my_card_btn :
				scatterIntent = new Intent(CardAccountBookActivity.this, MyCardActivity.class);
				break;
			case R.id.detail_view_btn :
				scatterIntent = new Intent(
						CardAccountBookActivity.this, DetailViewActivity.class);
				break;
			case R.id.breakdown_stats_btn :
				scatterIntent = new Intent(CardAccountBookActivity.this, GraphViewActivity.class);
				break;
			case R.id.option_btn : 
				scatterIntent = new Intent(CardAccountBookActivity.this, OptionViewActivity.class);
				break;
			}
			startActivity(scatterIntent);
		}
		
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
			if (modelNumber.equals(tmpRes.getString(R.string.mNum_gallexy_s_2_LTE_LG)) || modelNumber.equals(tmpRes.getString(R.string.mNum_gallexy_s_2_SK))) {
				String curAddress = cursor.getString(cursor.getColumnIndex("MDN1st"));
				if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_NH))) {
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

//		public String[] sCategory = {"주식","부식","간식","외식","술/유통","관리비","공과금","이동통신","인터넷","월세","가구/가전","주방/욕실","잡화","소모","의류비","패션/잡화","헤어/뷰티",
//				"세탁/수선","운동/레져","문화생활","여행","병원비","등록금","학원/교재비","육아용품","대중교통","주유비","데이트","선물","경조사비","모임회비","카드대금"};
		
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 1, 20, '이마트', 21000, '부식', '1*2*', 20120120);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 2, 20, '이마트', 21000, '간식', '1*2*', 20120220);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 3, 20, '이마트', 21000, '외식', '1*2*', 20120320);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 4, 20, '이마트', 21000, '술/유흥', '1*2*', 20120420);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 5, 20, '이마트', 21000, '관리비', '1*2*', 20120520);");
//		
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 1, 20, '이마트', 21000, '헤어/뷰티', '1*2*', 20120120);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 2, 20, '이마트', 21000, '여행', '1*2*', 20120220);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 3, 20, '이마트', 21000, '등록금', '1*2*', 20120320);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 4, 20, '이마트', 21000, '대중교통', '1*2*', 20120420);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 3, 20, '이마트', 21000, '대중교통', '1*2*', 20120320);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 2, 20, '이마트', 21000, '대중교통', '1*2*', 20120220);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 5, 20, '이마트', 21000, '선물', '1*2*', 20120520);");
//		
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 4, 30, '이마트', 21000, '주식', '1*2*', 20120430);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 5, 30, '삼마트', 40000, '술/유흥', '1*2*', 20120530);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민체크' , 2012, 5, 1, '사마트', 5000, '의류비', '3*6*', 20120501);");
//		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민체크' , 2012, 5, 2, '토마트삼마트이마트오마트뽱뽱예압베이베', 12000, '대중교통', '3*6*', 20120502);");

		cursor = getContentResolver().query(READ_SMS, null, null, null, null);
		String myCardQuery = "SELECT DISTINCT cardName, cardNumber FROM breakdowstats;";
		cursor = db.rawQuery(myCardQuery, null);

		while (cursor.moveToNext()) {
			String tmpCardName = cursor.getString(cursor.getColumnIndex("cardName"));
			String tmpCardNumber = cursor.getString(cursor.getColumnIndex("cardNumber"));
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