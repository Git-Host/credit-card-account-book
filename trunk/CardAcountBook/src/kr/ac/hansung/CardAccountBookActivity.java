package kr.ac.hansung;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * CardAccountBookActivity.java Main Activity  
 * @author Junu Kim
 */
/**
 * @author Admin
 *
 */
public class CardAccountBookActivity extends Activity {

	// Nexus One, Nexus S, Gallaxy Nexus ContentProvider Uri
	private final static String REFERENCE_PHONE_URI = "content://sms//inbox";
	private final static String SAMSUNG_GALLEXY_S2_URI = "content://com.sec.mms.provider/message";
	private final static String SAMSUNG_GALLEXY_A_URI = "content://com.btb.sec.mms.provider/message";
	private final static String LG_URI = "content://com.lge.messageprovider/msg/inbox";

	private final static int AUTO_SMS_PARSING_DIALOG = 10;
	
	private final static int SHOW_DATE_PICKER_FROM = 0;
	private final static int SHOW_DATE_PICKER_TO = 1;
	
	private String innerSMSquery;

	private static final String INITIAL_FLAG = "initial";
	private SharedPreferences pref;
	private ProgressDialog progressDialog;
	
	private ImageView myCardBtn, detailViewBtn, chartViewBtn, optionViewBtn;

	private SMSReceiver smsReceiver;
	private String DELIVERED = "SMS_DELIVERED";

	private LinearLayout todayPaymentView;
	private TextView fromDateView;
	private TextView toDateView;
	private TextView priceTitleView;
	private String cpUri;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Button Create
		myCardBtn = (ImageView) findViewById(R.id.my_card_btn);
		detailViewBtn = (ImageView) findViewById(R.id.detail_view_btn);
		chartViewBtn = (ImageView) findViewById(R.id.breakdown_stats_btn);
		optionViewBtn = (ImageView) findViewById(R.id.option_btn);
		todayPaymentView = (LinearLayout) findViewById(R.id.today_payment_view);

		MainButtonClickListener scatterIntentListener = new MainButtonClickListener();
		
		pref = getSharedPreferences(INITIAL_FLAG, MODE_PRIVATE);
		boolean initial_flag = pref.getBoolean(INITIAL_FLAG, false);
		
		String modelNumber = getDeviceModelNumber();

		// Device에 따라 적절한 ContentProvider 선택해줌
		if (modelNumber.equals(getResources().getString(R.string.mNum_gallexy_s_2_LTE_LG))
				|| modelNumber.equals(getResources().getString(R.string.mNum_gallexy_s_2_SK))) {
			cpUri = SAMSUNG_GALLEXY_S2_URI;
		} else {
			cpUri = REFERENCE_PHONE_URI;
		}
		
		// inbox msg to DB
		if (initial_flag == false) {
			showDialog(AUTO_SMS_PARSING_DIALOG);
		} 
		
		// 메인화면 현재월 1일부터 현재월 현재일까지 보여주는 메소드
		fromToDateChange();
		
		// 현재사용금액을 업데이트한다.
		showNowPayment();

		// Main Present payment Click
		todayPaymentView.setOnClickListener(scatterIntentListener);
		
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

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		
		switch (id) {
		case AUTO_SMS_PARSING_DIALOG :
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			
			AutoSMSListener dialogListener = new AutoSMSListener();
			
			dialogBuilder
			.setTitle(R.string.progress_bar_title)
			.setMessage(R.string.progress_bar_msg)
			.setPositiveButton(R.string.register_string, dialogListener)
			.setNegativeButton(R.string.cancel_string, dialogListener)
			.setCancelable(false);
			
			dialog = dialogBuilder.create();
			return dialog;
		}
		return super.onCreateDialog(id);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			showNowPayment();
		}
	};
	
	public class AutoSMSListener implements DialogInterface.OnClickListener {
		
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE :
				progressDialog = ProgressDialog.show(CardAccountBookActivity.this, getResources().getString(R.string.progress_bar_title), getResources().getString(R.string.progress_bar_wait), true, false);
				
				Thread progressThread = new Thread() {
					@Override
					public void run() {
					
						initialInboxToDB(cpUri);
						handler.sendEmptyMessage(0);
					}
				};
				progressThread.start();
				
				break;
			
			case DialogInterface.BUTTON_NEGATIVE :
			
				SharedPreferences.Editor ed = pref.edit();
				ed.putBoolean(INITIAL_FLAG, true);
				ed.commit();
				break;
			}
		}
	}
	
	

	public class MainButtonClickListener implements View.OnClickListener {
		public void onClick(View v) {
			Intent scatterIntent = null;
			switch (v.getId()) {
			case R.id.my_card_btn :
				scatterIntent = new Intent(CardAccountBookActivity.this, MyCardActivity.class);
				break;
			case R.id.detail_view_btn :
			case R.id.today_payment_view :
				scatterIntent = new Intent(CardAccountBookActivity.this, DetailViewActivity.class);
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
		SmsInfo smsInfoClass = new SmsInfo(this);
		Resources tmpRes = this.getResources();
		
		
		Uri READ_SMS = Uri.parse(cpUri);
		Cursor cursor = getContentResolver().query(READ_SMS, null, null, null, null);
		db = Cdb.getReadableDatabase();
		String modelNumber = getDeviceModelNumber();
		
		while (cursor.moveToNext()) {
			
			// GallexyS2LTE-LG, GallexyS2-SK
			if (modelNumber.equals(tmpRes.getString(R.string.mNum_gallexy_s_2_LTE_LG)) || modelNumber.equals(tmpRes.getString(R.string.mNum_gallexy_s_2_SK))) {
				String curAddress = cursor.getString(cursor.getColumnIndex("MDN1st"));
			
				if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_NH))
						|| curAddress.equals(tmpRes.getString(R.string.phoneNum_CITY)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_KEB))
						|| curAddress.equals(tmpRes.getString(R.string.phoneNum_saving_bank)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_SHINHAN))) {
				
					smsBody = cursor.getString(cursor.getColumnIndex("Title"));
					smsAddress = cursor.getString(cursor.getColumnIndex("MDN1st"));
					
					innerSMSquery = smsInfoClass.scatterMessage(smsAddress, smsBody);
					
					if (!innerSMSquery.equals(tmpRes.getString(R.string.sms_form_error)))
						db.execSQL(innerSMSquery);
				}
				
			// Nexus S, GallexyS2-KT, PRADA 3.0-KT
			} else {
				String curAddress = cursor.getString(cursor.getColumnIndex("address"));
			
				if (curAddress.equals(tmpRes.getString(R.string.phoneNum_KB)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_NH))
						|| curAddress.equals(tmpRes.getString(R.string.phoneNum_CITY)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_KEB))
						|| curAddress.equals(tmpRes.getString(R.string.phoneNum_saving_bank)) || curAddress.equals(tmpRes.getString(R.string.phoneNum_SHINHAN))
						 
						
//					|| curAddress.equals("01039487705")) { // 명희번호임 지워야함
//						|| curAddress.equals("01042434994")) { // 내번호임 지워야함
){
					
					smsBody = cursor.getString(cursor.getColumnIndex("body"));
					smsAddress = cursor.getString(cursor.getColumnIndex("address"));
					
					innerSMSquery = smsInfoClass.scatterMessage(smsAddress, smsBody);
					
					if (!innerSMSquery.equals(tmpRes.getString(R.string.sms_form_error)))
						db.execSQL(innerSMSquery);
				}
			} 
		}
		cursor.close();
		

		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 6, 5, '이마트', 21000, '대중교통', '1*2*', 20120605, 0);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 6, 5, '이마트', 14000, '술/유흥', '1*2*', 20120605, 0);");
		db.execSQL("INSERT INTO breakdowstats VALUES(null, 'KB국민카드' , 2012, 6, 5, '이마트', 300000, '등록금', '1*2*', 20120605, 0);");

		
		cursor = getContentResolver().query(READ_SMS, null, null, null, null);
		String myCardQuery = "SELECT DISTINCT cardName, cardNumber FROM breakdowstats WHERE deleteFlag = 0;";
		cursor = db.rawQuery(myCardQuery, null);

		while (cursor.moveToNext()) {
			String tmpCardName = cursor.getString(cursor.getColumnIndex("cardName"));
			String tmpCardNumber = cursor.getString(cursor.getColumnIndex("cardNumber"));
			
			String tmpQuery = "INSERT INTO myCard VALUES( null, '" + tmpCardName + "', '" + tmpCardNumber
							   + "', 0, 0, '', '" + setAutoCardImage(tmpCardName) + "', 0);";
			db.execSQL(tmpQuery);
		}
		db.close();

		SharedPreferences.Editor ed = pref.edit();
		ed.putBoolean(INITIAL_FLAG, true);
		ed.commit();
		
		progressDialog.dismiss();
	}

	/**
	 * Method setAutoCardImage 나의카드에 추가될 카드의 이미지를 자동으로 찾아서 이미지리소스URI를 리턴하는 메소드
	 * @param cardName
	 * @return String Image Resource URI
	 */
	public String setAutoCardImage(String cardName) {
		Resources autoCardRsc = this.getResources();

		String imageUri = "kr.ac.hansung:drawable/questionmark_card";
	
		if (cardName.equals(autoCardRsc.getString(R.string.NH_card))) {
			imageUri = "kr.ac.hansung:drawable/nh_chaum";
			return imageUri;
		} else if (cardName.equals(autoCardRsc.getString(R.string.KB_check)) || cardName.equals(autoCardRsc.getString(R.string.KB_credit))) {
			imageUri = "kr.ac.hansung:drawable/kb_kookmin_star";
			return imageUri;
		}
		
		return imageUri;
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

		String nowPayQuery = "SELECT price FROM breakdowstats WHERE (breakdowstats.combineDate >=" + dateFormat.format(tmpFromDate) +
				" AND breakdowstats.combineDate <= " + dateFormat.format(tmpToDate) +") AND breakdowstats.deleteFlag = 0;";
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