package kr.ac.hansung;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * DetailViewActivity.java 상세내역 뷰를 보여주고 상세내역 리스트의 자동추가및 수동추가
 * @author Junu Kim
 */
public class DetailViewActivity extends ListActivity implements CategoryList {
	private final static int DETAIL_DATE_EDIT_PICKER = 0;
	private final static int DETAIL_DATE_ADD_PICKER = 5;
	private final static int DETAIL_DATE_CASH_ADD_PICKER = 6;

	private final static int SHOW_DATE_PICKER_TO = 1;
	private final static int SHOW_DATE_PICKER_FROM = 2;

	private final static int ADD_CASH_USE = 3;
	private final static int ADD_CARD_USE = 4;
	private final static int INVALID_ALERT_DIALOG = 10;
	
	private final static int DELETE_OR_EDIT_DIALOG = 7;
	private final static int EDIT_DIALOG = 8;
	private final static int EDIT_CASH_DIALOG = 12;
	private final static int DELETE_ALERT_DIALOG = 9;
	private final static int DELETE_OR_EDIT_DIALOG_CASH = 11;
	private final static int CASH_USE_DATE_EDIT_PICKER = 13;
	
	private SQLiteDatabase db;
	private CardDB Cdb;
	private Intent receivedIntent;

	private String categoryQuery;
	
	private Calendar today;
	private Bundle _bdl;

	private ArrayList<SmsInfo> detailViewList = new ArrayList<SmsInfo>();
	private DetailViewAdapter dAdapter;
	private ListView detailListView;
	private TextView editApprovalTime;
	private TextView detailDlgCategory;

	// 기간별 보기 컴포넌트
	private LinearLayout fromToLinear;
	private TextView priceView;
	private TextView fromDateDetailView;
	private TextView toDateDetailView;
	private TextView detailPriceView;

	// 카드사용 수동추가 다이얼로그 컴포넌트
	private EditText addDetailCardName;
	private EditText addDetailCardNumber;
	private TextView addDetailApprovalTime;
	private EditText addDetailPlace;
	private EditText addDetailPrice;
	private TextView addDetailCategory;

	// 현금사용 추가 다이얼로그 컴포넌트
	private TextView addCashUseTime;
	private EditText addCashUsePlace;
	private EditText addCashUsePrice;
	private TextView addCashUseCategory;
	
	//현금사용 수정 다이얼로그 컴포넌트
	private TextView addCashUseEditTime;
	private EditText addCashUseEditPlace;
	private EditText addCashUseEditPrice;
	private TextView addCashUseEditCategory;
	private String beforeCashUseTime;
	private String beforeCashUsePlace;
	private String beforeCashUsePrice;
	private String beforeCashUseCategory;
	private int clickedPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view);

		fromToLinear = (LinearLayout) findViewById(R.id.period_date_layout_detail); 
		priceView = (TextView) findViewById(R.id.detail_price_view_title);
		detailPriceView = (TextView) findViewById(R.id.detail_price_view);
		today = Calendar.getInstance();

		fromToDateChange();

		receivedIntent = getIntent();
		priceView.setVisibility(View.GONE);
		dynamicListAdd(dynamicDatabaseCursor(receivedIntent));
		
	}

	/**
	 * Method dynamicListAdd 동적으로 상세내역을 어뎁터에 추가하는 메소드
	 * @param detailCursor List에 Add하기 위한 DB Cursor
	 */
	public void dynamicListAdd(Cursor detailCursor) {

		detailViewList.clear();

		while (detailCursor.moveToNext()) {

			int primaryKey = detailCursor.getInt(detailCursor.getColumnIndex("breakKey"));
			String cName = detailCursor.getString(detailCursor.getColumnIndex("cardName"));
			int pYear = detailCursor.getInt(detailCursor.getColumnIndex("pYear"));
			int pMonth = detailCursor.getInt(detailCursor.getColumnIndex("pMonth"));
			int pDay = detailCursor.getInt(detailCursor.getColumnIndex("pDay"));
			String pPlace = detailCursor.getString(detailCursor.getColumnIndex("pPlace"));
			int pPrice = detailCursor.getInt(detailCursor.getColumnIndex("price"));
			String category = detailCursor.getString(detailCursor.getColumnIndex("category"));
			String tmpCardNumber = detailCursor.getString(detailCursor.getColumnIndex("cardNumber"));
			
			Date date = null;
			date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			date.setYear(pYear - 1900);
			date.setMonth(pMonth - 1);
			date.setDate(pDay);
			String sDate = dateFormat.format(date);

			SmsInfo tmp = new SmsInfo(cName);
			tmp.setBreakKey(primaryKey);
			tmp.setApprovalTime(sDate);
			tmp.setPlace(pPlace);
			tmp.setPrice(pPrice);
			tmp.setCategory(category);
			tmp.setCardNumber(tmpCardNumber);

			detailViewList.add(tmp);
		}
 		db.close();

		dAdapter = new DetailViewAdapter(this, R.layout.detail_view_list_layout, detailViewList);

		detailListView = this.getListView();
		detailListView.setOnItemLongClickListener(new detailViewListItemLongClickListener());
		detailListView.setDivider(null);
			
		setListAdapter(dAdapter);
		sumPrice();
	}

	/**
	 * Method sumPrice 상세내역 리스트의 금액을 모두 더하여 뷰에 표시해쥬는 메소드
	 */
	public void sumPrice() {
		int price = 0;

		for (int i = 0; i < dAdapter.getCount(); i++) {
			price = price + dAdapter.getItem(i).getPrice();
		}
		
		DecimalFormat df = new DecimalFormat("#,##0");
		detailPriceView.setText(df.format(price) + "원");
	}

	/**
	 * Method dynamicDatabaseCursor 두개의 TextView의 Date를 비교하여 해당기간의 상세내역의 Cursor를 리턴하는 메소드
	 * @param fromDate 'yyyy. MM. dd.' 형식이 입력되어있는 TextView
	 * @param toDate 'yyyy. MM. dd.' 형식이 입력되어있는 TextView
	 * @return Cursor 조건에 맞는 상세내역의 Cursor
	 */
	public Cursor dynamicDatabaseCursor(TextView fromDate, TextView toDate) {
		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();
		Cursor tmpCursor;
		String tmpStr = null;

		String[] fromCombineDate = fromDate.getText().toString().replace(" ", "").split("\\.");
		String[] toCombineDate = toDate.getText().toString().replace(" ", "").split("\\.");

		SimpleDateFormat tmpFormat = new SimpleDateFormat("yyyyMMdd");
		Date tmpFromDate = new Date();
		Date tmpToDate = new Date();

		tmpFromDate.setYear(Integer.parseInt(fromCombineDate[0]) - 1900);
		tmpFromDate.setMonth(Integer.parseInt(fromCombineDate[1]) - 1);
		tmpFromDate.setDate(Integer.parseInt(fromCombineDate[2]));

		tmpToDate.setYear(Integer.parseInt(toCombineDate[0]) - 1900);
		tmpToDate.setMonth(Integer.parseInt(toCombineDate[1]) - 1);
		tmpToDate.setDate(Integer.parseInt(toCombineDate[2]));

		// 나의카드에서 상세내역 기간 변경시  
		if (receivedIntent.hasExtra("cardName")	&& receivedIntent.hasExtra("cardNumber")) {
			tmpStr = "SELECT * FROM breakdowstats WHERE cardName = '" + receivedIntent.getStringExtra("cardName")
					+ "' AND cardNumber = '" + receivedIntent.getStringExtra("cardNumber")
					+ "' AND (combineDate >= " + tmpFormat.format(tmpFromDate) + " AND combineDate <= " + tmpFormat.format(tmpToDate)
					+ ") AND deleteFlag = 0 ORDER BY combineDate DESC;";
		} else if (receivedIntent.hasExtra("selCategory")) {
			tmpStr = categoryQuery + ") AND (combineDate >= " + tmpFormat.format(tmpFromDate) + " AND combineDate <= " 
					+ tmpFormat.format(tmpToDate)
					+ ") AND deleteFlag = 0 ORDER BY combineDate DESC;";
		} else {
			tmpStr = "SELECT * FROM breakdowstats WHERE combineDate >= " + tmpFormat.format(tmpFromDate) + " AND combineDate <= "
					+ tmpFormat.format(tmpToDate) + " AND deleteFlag = 0 ORDER BY combineDate DESC;";
		}

		tmpCursor = db.rawQuery(tmpStr, null);

		return tmpCursor;
	}

	/**
	 * Method dynamicDatabaseCursor intent에 포함된 정보를 확인하여 조건에 맞게 상세내역의 Cursor를
	 * 리턴하는 메소드
	 * @param intent 정보가 담긴 intent
	 * @return Cursor 조건에 맞는 상세내역의 Cursor
	 */
	public Cursor dynamicDatabaseCursor(Intent intent) {
		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();
		Cursor tmpCursor;

		String[] fromCombineDate = fromDateDetailView.getText().toString().replace(" ", "").split("\\.");
		String[] toCombineDate = toDateDetailView.getText().toString().replace(" ", "").split("\\.");

		SimpleDateFormat tmpFormat = new SimpleDateFormat("yyyyMMdd");
		Date tmpFromDate = new Date();
		Date tmpToDate = new Date();

		tmpFromDate.setYear(Integer.parseInt(fromCombineDate[0]) - 1900);
		tmpFromDate.setMonth(Integer.parseInt(fromCombineDate[1]) - 1);
		tmpFromDate.setDate(Integer.parseInt(fromCombineDate[2]));

		tmpToDate.setYear(Integer.parseInt(toCombineDate[0]) - 1900);
		tmpToDate.setMonth(Integer.parseInt(toCombineDate[1]) - 1);
		tmpToDate.setDate(Integer.parseInt(toCombineDate[2]));

		// 월별사용목록 intent
		if (intent.hasExtra("selMonth")) {
			int selMonth = (int) intent.getDoubleExtra("selMonth", 0);
			String strQuery = "SELECT * FROM breakdowstats WHERE pMonth =" + selMonth + " AND deleteFlag = 0;";
			tmpCursor = db.rawQuery(strQuery, null);
			fromToLinear.setVisibility(View.GONE);
			priceView.setText(String.valueOf(selMonth) + getResources().getString(R.string.month_use));
			priceView.setTextSize(25);
			priceView.setPadding(20, 0, 20, 5);
			priceView.setVisibility(View.VISIBLE);
			
		// 나의카드에서 카드선택 intent
		} else if (intent.hasExtra("cardName") && intent.hasExtra("cardNumber")) {
			String strQuery = "SELECT * FROM breakdowstats WHERE cardName = '" + intent.getStringExtra("cardName")
							  + "' AND cardNumber = '" + intent.getStringExtra("cardNumber") 
							  + "' AND combineDate >= " + tmpFormat.format(tmpFromDate) + " AND combineDate <= "
							  + tmpFormat.format(tmpToDate) + " AND deleteFlag = 0 ORDER BY combineDate DESC;";
			tmpCursor = db.rawQuery(strQuery, null);
		
		// 카테고리별사용목록  intent 
		} else if (intent.hasExtra("selCategory")) {
			String selCategory = (String) intent.getStringExtra("selCategory");
			String strQuery = null;
			int cIndex = 0;
			
			for (int i = 0; i < High_Category.length; i++) {
				if (High_Category[i].matches(selCategory))
					cIndex = i;
			}
			
			strQuery = "SELECT * FROM breakdowstats WHERE (category = '" + getResources().getString(i_category[cIndex][0])+"'";
			
			for (int j = 1; j < i_category[cIndex].length; j++) {
				String setCategory = getResources().getString(i_category[cIndex][j]);
				strQuery += " OR category = '" + setCategory + "'";
			}
			
			categoryQuery = strQuery;
			
			strQuery += ") AND (combineDate >= " + tmpFormat.format(tmpFromDate) + " AND combineDate <= "
							  + tmpFormat.format(tmpToDate) + ") AND deleteFlag = 0 ORDER BY combineDate DESC;";
			tmpCursor = db.rawQuery(strQuery, null);
			
		// 메인에서 현재사용금액 눌럿을때 사용내역
		} else if (intent.hasExtra("fromTime") && intent.hasExtra("toTime")) {
			String[] fromTime = intent.getStringExtra("fromTime").replace(" ", "").split("\\.");
			String[] toTime = intent.getStringExtra("toTime").replace(" ", "").split("\\.");
			
			Date fromTimeDate = new Date();
			Date toTimeDate = new Date();

			fromTimeDate.setYear(Integer.parseInt(fromTime[0]) - 1900);
			fromTimeDate.setMonth(Integer.parseInt(fromTime[1]) - 1);
			fromTimeDate.setDate(Integer.parseInt(fromTime[2]));
			
			toTimeDate.setYear(Integer.parseInt(toTime[0]) - 1900);
			toTimeDate.setMonth(Integer.parseInt(toTime[1]) - 1);
			toTimeDate.setDate(Integer.parseInt(toTime[2]));
			
			fromDateDetailView.setText(intent.getStringExtra("fromTime"));
			toDateDetailView.setText(intent.getStringExtra("toTime"));
			
			String tmpStr = "SELECT * FROM breakdowstats WHERE (combineDate >= " + tmpFormat.format(fromTimeDate)
					+ " AND combineDate <= "
					+ tmpFormat.format(toTimeDate) + ") AND deleteFlag = 0 ORDER BY combineDate DESC;";
			tmpCursor = db.rawQuery(tmpStr, null);
	
		// 상세내역보기 
		} else {
			String tmpStr = "SELECT * FROM breakdowstats WHERE (combineDate >= " + tmpFormat.format(tmpFromDate)
							+ " AND combineDate <= "
							+ tmpFormat.format(tmpToDate) + ") AND deleteFlag = 0 ORDER BY combineDate DESC;";
			tmpCursor = db.rawQuery(tmpStr, null);
		}
		return tmpCursor;
	}

	/**
	 * detailViewListItemLongClickListener 상세내역의 리스트 하나를 LongClick할때의 Event Handler
	 * @author Junu Kim
	 */
	public class detailViewListItemLongClickListener implements	AdapterView.OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
			if (dAdapter.getItem(pos).getCardName().equals(getResources().getString(R.string.cash_use_list_string))) {
				Bundle bdl = new Bundle();
				bdl.putInt("position", pos);
				showDialog(DELETE_OR_EDIT_DIALOG_CASH, bdl);
				return true;
			} else {
				Bundle bdl = new Bundle();
				bdl.putInt("position", pos);
				showDialog(DELETE_OR_EDIT_DIALOG, bdl);
				return true;
			}
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		SmsInfo tmpSmsInfo = dAdapter.getItem(position);
		LayoutInflater dlgLayoutInflater = (LayoutInflater) this.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);

		if (tmpSmsInfo.getCardName().equals(getResources().getString(R.string.cash_use_list_string))) {

			View dlgView = dlgLayoutInflater.inflate(R.layout.detail_list_cash_dialog_layout,(ViewGroup) findViewById(R.id.detail_dlg_cash_root_view));

			TextView detailDlgCashUseTime = (TextView) dlgView.findViewById(R.id.detail_dlg_cash_use_time);
			TextView detailDlgCashUsePlace = (TextView) dlgView.findViewById(R.id.detail_dlg_cash_place);
			TextView detailDlgCashUsePrice = (TextView) dlgView.findViewById(R.id.detail_dlg_cash_price);
			detailDlgCategory = (TextView) dlgView.findViewById(R.id.detail_dlg_cash_category);

			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd. (E)");

			String[] tmpMonthDay = tmpSmsInfo.getApprovalTime().split("-");
			date.setYear(Integer.parseInt(tmpMonthDay[0]) - 1900);
			date.setMonth(Integer.parseInt(tmpMonthDay[1]) - 1);
			date.setDate(Integer.parseInt(tmpMonthDay[2]));
			String dlgDate = dateFormat.format(date);

			detailDlgCashUseTime.setText(dlgDate);
			detailDlgCashUsePlace.setText(tmpSmsInfo.getPlace().toString());
			detailDlgCashUsePlace.setSelected(true);

			detailDlgCashUsePrice.setText(SmsInfo.decimalPointToString(tmpSmsInfo.getPrice()));
			detailDlgCategory.setText(tmpSmsInfo.getCategory().toString());

			new AlertDialog.Builder(DetailViewActivity.this)
			.setTitle(R.string.cash)
			.setView(dlgView)
			.show();

		} else {
			View dlgView = dlgLayoutInflater.inflate(R.layout.detail_list_dialog_layout,(ViewGroup) findViewById(R.id.detail_dlg_root_view));

			TextView detailDlgApprovalTime = (TextView) dlgView.findViewById(R.id.detail_dlg_approval_time);
			TextView detailDlgPlace = (TextView) dlgView.findViewById(R.id.detail_dlg_place);
			TextView detailDlgPrice = (TextView) dlgView.findViewById(R.id.detail_dlg_price);
			detailDlgCategory = (TextView) dlgView.findViewById(R.id.detail_dlg_category);

			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd. (E)");

			String[] tmpMonthDay = tmpSmsInfo.getApprovalTime().split("-");
			date.setYear(Integer.parseInt(tmpMonthDay[0]) - 1900);
			date.setMonth(Integer.parseInt(tmpMonthDay[1]) - 1);
			date.setDate(Integer.parseInt(tmpMonthDay[2]));
			String dlgDate = dateFormat.format(date);

			detailDlgApprovalTime.setText(dlgDate);
			detailDlgPlace.setText(dAdapter.getItem(position).getPlace());
			detailDlgPlace.setSelected(true);

			detailDlgPrice.setText(SmsInfo.decimalPointToString(dAdapter.getItem(position).getPrice()));
			detailDlgCategory.setText(dAdapter.getItem(position).getCategory());
			String dlgTitle = dAdapter.getItem(position).getCardName() + " ("+ dAdapter.getItem(position).getCardNumber() + ")";

			new AlertDialog.Builder(DetailViewActivity.this)
			.setTitle(dlgTitle)
			.setView(dlgView).show();
		}
	}

	/**
	 * DetailViewAdapter ListView의 리스트에 정보를 공급해주는 Adapter
	 * @author Junu Kim
	 */
	public class DetailViewAdapter extends ArrayAdapter<SmsInfo> {

		private ArrayList<SmsInfo> items;

		public DetailViewAdapter(Context context, int textViewResourceId, ArrayList<SmsInfo> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.detail_view_list_layout, null);
			}

			SmsInfo m = items.get(position);

			if (m != null) {
				TextView tmpATime = (TextView) v.findViewById(R.id.d_approval_time_view);
				TextView tmpCName = (TextView) v.findViewById(R.id.d_card_name_view);
				TextView tmpPlace = (TextView) v.findViewById(R.id.d_place_view);
				TextView tmpPrice = (TextView) v.findViewById(R.id.d_price_view);
				TextView tmpCategory = (TextView) v.findViewById(R.id.d_category_view);

				tmpATime.setText(m.getApprovalTime().substring(5));
				tmpCName.setText(m.getCardName());
				tmpPlace.setText(m.getPlace());
				tmpPrice.setText(SmsInfo.decimalPointToString(m.getPrice()));
				tmpCategory.setText(m.getCategory());
			}
			return v;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle bdl) {
		String[] delete_edit_array = { getResources().getString(R.string.delete_b_stats_edit), getResources().getString(R.string.delete_b_stats_delete) };
		String[] delete_edit_cash_array = { getResources().getString(R.string.edit_cash_menu), getResources().getString(R.string.delete_cash_menu) };
				
		_bdl = new Bundle();
		_bdl = bdl;
		
		Dialog createDialog;
		AlertDialog.Builder builder;

		switch (id) {
		case DETAIL_DATE_EDIT_PICKER :
			return new DatePickerDialog(this, detailDateEditPickerListener, bdl.getInt("aprvlYear"), bdl.getInt("aprvlMonth") - 1, bdl.getInt("aprvlDay"));
			
		case DETAIL_DATE_ADD_PICKER :
			return new DatePickerDialog(this, detailDateAddPickerListener, bdl.getInt("aprvlYear"), bdl.getInt("aprvlMonth") - 1, bdl.getInt("aprvlDay"));
			
		case DETAIL_DATE_CASH_ADD_PICKER :
			return new DatePickerDialog(this, detailDateCardAddPickerListener, bdl.getInt("aprvlYear"), bdl.getInt("aprvlMonth") - 1, bdl.getInt("aprvlDay"));
			
		case CASH_USE_DATE_EDIT_PICKER :
			return new DatePickerDialog(this, cashDateEditPickerListener, bdl.getInt("aprvlYear"), bdl.getInt("aprvlMonth") - 1, bdl.getInt("aprvlDay"));
			
		case SHOW_DATE_PICKER_TO :
			return new DatePickerDialog(this, toDetailDateSetListener, bdl.getInt("toYear"), bdl.getInt("toMonth") - 1, bdl.getInt("toDay"));
			
		case SHOW_DATE_PICKER_FROM :
			return new DatePickerDialog(this, fromDetailDateSetListener, bdl.getInt("toYear"), bdl.getInt("toMonth") - 1, bdl.getInt("toDay"));
			
		case DELETE_OR_EDIT_DIALOG :
			builder = new AlertDialog.Builder(this);
			
			builder
			.setTitle(R.string.delete_breakdown_stats_dlg_title)
			.setItems(delete_edit_array, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					Bundle tmpBdl = new Bundle();
					tmpBdl.putInt("position", _bdl.getInt("position"));
					switch (which) {
					case 0 :	// 사용내역 수정 클릭
						removeDialog(DELETE_OR_EDIT_DIALOG);
						showDialog(EDIT_DIALOG, tmpBdl);
						break;
						
					case 1 :	// 사용내역 삭제 클릭
						removeDialog(DELETE_OR_EDIT_DIALOG);
						showDialog(DELETE_ALERT_DIALOG, tmpBdl);
						break;
					}
					
				}
			});
			
			createDialog = builder.create();
			return createDialog;
		
		case DELETE_ALERT_DIALOG :
			builder = new AlertDialog.Builder(this);
			
			builder
			.setTitle(R.string.delete_b_stats_delete)
			.setMessage(R.string.delete_b_alert_msg)
			.setPositiveButton(R.string.delete_string, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					int pos = _bdl.getInt("position");
					
					Cdb = new CardDB(getApplicationContext());
					db = Cdb.getReadableDatabase();

					int key = dAdapter.getItem(pos).getBreakKey();
					
					String deleteFlagUpdateQuery = "UPDATE breakdowstats SET deleteFlag = 1 WHERE breakKey = " + key + ";";
					
					db.execSQL(deleteFlagUpdateQuery);
					db.close();

					dAdapter.remove(dAdapter.getItem(pos));
					dAdapter.notifyDataSetChanged();
					
					sumPrice();
					
					removeDialog(DELETE_ALERT_DIALOG);
				}
			})
			.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DELETE_ALERT_DIALOG);
				}
			});
						
			createDialog = builder.create();
			return createDialog;
			
		case EDIT_DIALOG :
			builder = new AlertDialog.Builder(this);
			
			final int position = _bdl.getInt("position");;
			LayoutInflater dlgLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View editDlgView = dlgLayoutInflater.inflate(R.layout.detail_list_edit_dialog_layout, (ViewGroup) findViewById(R.id.detail_edit_dlg_root_view));

			final EditText editCardName = (EditText) editDlgView.findViewById(R.id.detail_edit_card_name);
			final EditText editCardNumber = (EditText) editDlgView.findViewById(R.id.detail_edit_card_number);
			editApprovalTime = (TextView) editDlgView.findViewById(R.id.detail_edit_approval_time);
			final EditText editPlace = (EditText) editDlgView.findViewById(R.id.detail_edit_place);
			final EditText editPrice = (EditText) editDlgView.findViewById(R.id.detail_edit_price);
			final TextView editCategory = (TextView) editDlgView.findViewById(R.id.detail_edit_category);

			final String beforeCardName = dAdapter.getItem(position).getCardName();
			final String beforeCardNumber = dAdapter.getItem(position).getCardNumber();
			final String beforeApprovalTime = dAdapter.getItem(position).getApprovalTime();
			final String beforePlace = dAdapter.getItem(position).getPlace();
			final String beforePrice = String.valueOf(dAdapter.getItem(position).getPrice());
			final String beforeCategory = dAdapter.getItem(position).getCategory();

			editCardName.setText(beforeCardName);
			editCardName.setSelection(beforeCardName.length());

			editCardNumber.setText(beforeCardNumber);
			editCardNumber.setSelection(beforeCardNumber.length());

			editApprovalTime.setText(beforeApprovalTime);

			editPlace.setText(beforePlace);
			editPlace.setSelection(beforePlace.length());

			editPrice.setText(beforePrice);
			editPrice.setSelection(beforePrice.length());

			editCategory.setText(beforeCategory);

			editApprovalTime.setOnClickListener(new detailEditClickListener());
			editCategory.setOnClickListener(new detailEditClickListener());
			
			
			builder
			.setTitle(getResources().getString(R.string.detail_dlg_edit_title))
			.setPositiveButton(R.string.edit_string, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					String[] tmpDate = editApprovalTime.getText().toString().split("-");
					
					if (beforeCardName.equals(editCardName.getText().toString())
							&& beforeCardNumber.equals(editCardNumber.getText().toString())
							&& beforeApprovalTime.equals(editApprovalTime.getText().toString())
							&& beforePlace.equals(editPlace.getText().toString()) && beforePrice.equals(editPrice.getText().toString())
							&& beforeCategory.equals(editCategory.getText().toString())) {
						
					} else if ((!beforeCardName.equals(editCardName.getText().toString()) || !beforeCardNumber.equals(editCardNumber.getText().toString()))
							&& beforeApprovalTime.equals(editApprovalTime.getText().toString())
							&& beforePlace.equals(editPlace.getText().toString()) && beforePrice.equals(editPrice.getText().toString())
							&& beforeCategory.equals(editCategory.getText().toString())) {
						CardDB Cdb = new CardDB(DetailViewActivity.this);
						db = Cdb.getReadableDatabase();

						String checkQuery = "SELECT * FROM breakdowstats WHERE cardName = '" + editCardName.getText().toString()
											+ "' AND cardNumber = '" + editCardNumber.getText().toString() + "' AND deleteFlag = 0;";
						Cursor tmpCursor = db.rawQuery(checkQuery, null);

						if (tmpCursor.getCount() == 0) {
							String addCardQuery = "INSERT INTO myCard VALUES (null, '" + editCardName.getText().toString()
												+ "', '" + editCardNumber.getText().toString() + "', null, null, null, '', 0);";
							db.execSQL(addCardQuery);
						}

						String updateQuery = "UPDATE breakdowstats SET cardName = '" + editCardName.getText().toString() + "', cardNumber = '"
											+ editCardNumber.getText().toString() + "' WHERE breakKey = "+ dAdapter.getItem(position).getBreakKey() + ";";
						db.execSQL(updateQuery);
						db.close();

						SmsInfo tmpObj = dAdapter.getItem(position);
						tmpObj.setCardName(editCardName.getText().toString());
						tmpObj.setCardNumber(editCardNumber.getText().toString());

						dAdapter.notifyDataSetChanged();
						sumPrice();
					} else {
						CardDB Cdb = new CardDB(DetailViewActivity.this);
						db = Cdb.getReadableDatabase();

						String updateQuery = "UPDATE breakdowstats SET cardName = '" + editCardName.getText().toString() + "', cardNumber = '" + editCardNumber.getText().toString()
											 + "', pYear = "+ Integer.parseInt(tmpDate[0]) + ", pMonth = "+ Integer.parseInt(tmpDate[1])+ ", pDay = " + Integer.parseInt(tmpDate[2])
											 + ", combineDate = "+ Integer.parseInt(editApprovalTime.getText().toString().replace("-",""))+ ", pPlace = '"+ editPlace.getText().toString()
											 + "', price = "+ Integer.parseInt(editPrice.getText().toString())+ ", category = '"+ editCategory.getText().toString()+ "' WHERE breakKey = "
											 + dAdapter.getItem(position).getBreakKey() + ";";

						db.execSQL(updateQuery);
						db.close();

						SmsInfo tmpObj = dAdapter.getItem(position);
						tmpObj.setCardName(editCardName.getText().toString());
						tmpObj.setCardNumber(editCardNumber.getText().toString());
						tmpObj.setApprovalTime(editApprovalTime.getText().toString());
						tmpObj.setPlace(editPlace.getText().toString());
						tmpObj.setPrice(Integer.parseInt(editPrice.getText().toString()));
						tmpObj.setCategory(editCategory.getText().toString());

						dAdapter.notifyDataSetChanged();
						sumPrice();
					}
					removeDialog(EDIT_DIALOG);
				}
			})
			.setCancelable(false)
			.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(EDIT_DIALOG);
				}
			})
			.setView(editDlgView);
			
			createDialog = builder.create();
			return createDialog;
			
		case INVALID_ALERT_DIALOG :
			builder = new AlertDialog.Builder(this);
			
			builder
			.setTitle(R.string.invalid_input_alert_title)
			.setMessage(R.string.invalid_input_alert_description_add_cash)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.identify_string, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(INVALID_ALERT_DIALOG);
				}
			});
			
			createDialog = builder.create();
			return createDialog;
		
		case DELETE_OR_EDIT_DIALOG_CASH :
			builder = new AlertDialog.Builder(this);
			
			builder
			.setTitle(R.string.delete_edit_cash_title)
			.setItems(delete_edit_cash_array, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					Bundle tmpBdl = new Bundle();
					tmpBdl.putInt("position", _bdl.getInt("position"));
					switch (which) {
					case 0 :	// 사용내역 수정 클릭
						removeDialog(DELETE_OR_EDIT_DIALOG_CASH);
						showDialog(EDIT_CASH_DIALOG, tmpBdl);
						break;
						
					case 1 :	// 사용내역 삭제 클릭
						removeDialog(DELETE_OR_EDIT_DIALOG_CASH);
						showDialog(DELETE_ALERT_DIALOG, tmpBdl);
						break;
					}
					
				}
			});
			
			createDialog = builder.create();
			return createDialog;

		case EDIT_CASH_DIALOG :
			builder = new AlertDialog.Builder(this);
			clickedPos = _bdl.getInt("position");
			
			LayoutInflater editCashLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View editCashDlgView = editCashLayoutInflater.inflate(R.layout.detail_list_cash_edit_dialog_layout, (ViewGroup) findViewById(R.id.detail_cash_use_edit_root_view));

			addCashUseEditTime = (TextView) editCashDlgView.findViewById(R.id.detail_cash_use_time_edit);
			addCashUseEditPlace = (EditText) editCashDlgView.findViewById(R.id.detail_cash_use_place_edit);
			addCashUseEditPrice = (EditText) editCashDlgView.findViewById(R.id.detail_cash_use_price_edit);
			addCashUseEditCategory = (TextView) editCashDlgView.findViewById(R.id.detail_cash_use_category_edit);

			beforeCashUseTime = dAdapter.getItem(clickedPos).getApprovalTime();
			beforeCashUsePlace = dAdapter.getItem(clickedPos).getPlace();
			beforeCashUsePrice = String.valueOf(dAdapter.getItem(clickedPos).getPrice());
			beforeCashUseCategory = dAdapter.getItem(clickedPos).getCategory();
			
			addCashUseEditTime.setText(beforeCashUseTime);
			addCashUseEditPlace.setText(beforeCashUsePlace);
			addCashUseEditPrice.setText(beforeCashUsePrice);
			addCashUseEditCategory.setText(beforeCashUseCategory);
			
			addCashUseEditTime.setOnClickListener(new detailEditClickListener());
			addCashUseEditCategory.setOnClickListener(new detailEditClickListener());
			
			EditCashDialogListener editCashDialogListener = new EditCashDialogListener();
			
			builder
			.setTitle(R.string.edit_cash_menu)
			.setPositiveButton(R.string.edit_string, editCashDialogListener)
			.setNegativeButton(R.string.cancel_string, editCashDialogListener)
			.setCancelable(false)
			.setView(editCashDlgView);
			
			createDialog = builder.create();
			return createDialog;
		}
		return super.onCreateDialog(id, bdl);
	}


	private DatePickerDialog.OnDateSetListener toDetailDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			toDateDetailView.setText(year + ". " + month + ". " + dayOfMonth + ". ");
			dynamicListAdd(dynamicDatabaseCursor(fromDateDetailView, toDateDetailView));
		}
	};

	private DatePickerDialog.OnDateSetListener fromDetailDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			fromDateDetailView.setText(year + ". " + month + ". " + dayOfMonth + ". ");
			dynamicListAdd(dynamicDatabaseCursor(fromDateDetailView, toDateDetailView));
		}
	};

	private DatePickerDialog.OnDateSetListener detailDateEditPickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date detailDlgDate = new Date();
			detailDlgDate.setYear(year - 1900);
			detailDlgDate.setMonth(month - 1);
			detailDlgDate.setDate(dayOfMonth);

			editApprovalTime.setText(dateFormat.format(detailDlgDate));
		}
	};
	
	private DatePickerDialog.OnDateSetListener cashDateEditPickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date detailDlgDate = new Date();
			detailDlgDate.setYear(year - 1900);
			detailDlgDate.setMonth(month - 1);
			detailDlgDate.setDate(dayOfMonth);

			addCashUseEditTime.setText(dateFormat.format(detailDlgDate));
		}
	};

	private DatePickerDialog.OnDateSetListener detailDateAddPickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date detailDlgDate = new Date();
			detailDlgDate.setYear(year - 1900);
			detailDlgDate.setMonth(month - 1);
			detailDlgDate.setDate(dayOfMonth);

			addDetailApprovalTime.setText(dateFormat.format(detailDlgDate));
		}
	};

	private DatePickerDialog.OnDateSetListener detailDateCardAddPickerListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			int month = monthOfYear + 1;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date detailDlgDate = new Date();
			detailDlgDate.setYear(year - 1900);
			detailDlgDate.setMonth(month - 1);
			detailDlgDate.setDate(dayOfMonth);

			addCashUseTime.setText(dateFormat.format(detailDlgDate));
		}
	};

	/**
	 * detailEditClickListener 상세내역 정보수정을 위한 Dialog의 Event Handler
	 * @author Junu Kim
	 */
	public class detailEditClickListener implements View.OnClickListener {

		public void onClick(View v) {
			final TextView tmpView = (TextView) v;
			Bundle aprvlBdl;
			String[] tmpAprvl;
		
			switch (v.getId()) {
			
			case R.id.detail_cash_use_time_edit :
				aprvlBdl = new Bundle();
				tmpAprvl = ((TextView) v).getText().toString().split("-");
				aprvlBdl.putInt("aprvlYear", Integer.parseInt(tmpAprvl[0]));
				aprvlBdl.putInt("aprvlMonth", Integer.parseInt(tmpAprvl[1]));
				aprvlBdl.putInt("aprvlDay", Integer.parseInt(tmpAprvl[2]));
				showDialog(CASH_USE_DATE_EDIT_PICKER, aprvlBdl);
				break;
				
			case R.id.detail_edit_approval_time:
				aprvlBdl = new Bundle();
				tmpAprvl = ((TextView) v).getText().toString().split("-");
				aprvlBdl.putInt("aprvlYear", Integer.parseInt(tmpAprvl[0]));
				aprvlBdl.putInt("aprvlMonth", Integer.parseInt(tmpAprvl[1]));
				aprvlBdl.putInt("aprvlDay", Integer.parseInt(tmpAprvl[2]));
				showDialog(DETAIL_DATE_EDIT_PICKER, aprvlBdl);
				break;

			case R.id.detail_add_approval_time:
				aprvlBdl = new Bundle();
				tmpAprvl = ((TextView) v).getText().toString().split("-");
				aprvlBdl.putInt("aprvlYear", Integer.parseInt(tmpAprvl[0]));
				aprvlBdl.putInt("aprvlMonth", Integer.parseInt(tmpAprvl[1]));
				aprvlBdl.putInt("aprvlDay", Integer.parseInt(tmpAprvl[2]));
				showDialog(DETAIL_DATE_ADD_PICKER, aprvlBdl);
				break;

			case R.id.detail_cash_use_time:
				aprvlBdl = new Bundle();
				tmpAprvl = ((TextView) v).getText().toString().split("-");
				aprvlBdl.putInt("aprvlYear", Integer.parseInt(tmpAprvl[0]));
				aprvlBdl.putInt("aprvlMonth", Integer.parseInt(tmpAprvl[1]));
				aprvlBdl.putInt("aprvlDay", Integer.parseInt(tmpAprvl[2]));
				showDialog(DETAIL_DATE_CASH_ADD_PICKER, aprvlBdl);
				break;

			case R.id.detail_cash_use_category_edit :
			case R.id.detail_edit_category:
			case R.id.detail_add_category:
			case R.id.detail_cash_use_category:
				final String[] detailCategory = new String[categoryList.length];
				String category = tmpView.getText().toString();

				for (int i = 0; i < categoryList.length; i++) {
					detailCategory[i] = getResources().getString(
							categoryList[i]);
				}

				new AlertDialog.Builder(DetailViewActivity.this)
				.setTitle(getResources().getString(R.string.edit_category))
				.setNegativeButton(R.string.cancel_string, null)
				.setSingleChoiceItems(detailCategory, detailAutoCategory(category), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String tmp = detailCategory[which];
						tmpView.setText(tmp);
						dialog.dismiss();
					}
				}).show();
				break;
			}
		}
	}

	/**
	 * Method fromToDateChange 기간별 DatePicker를 보여주기 위한 메소드
	 */
	public void fromToDateChange() {
		fromDateDetailView = (TextView) findViewById(R.id.from_date_view_detail);
		toDateDetailView = (TextView) findViewById(R.id.to_date_view_detail);

		final int todayYear;
		final int todayMonth;
		final int todayDay;

		todayYear = today.get(Calendar.YEAR);
		todayMonth = today.get(Calendar.MONTH) + 1;
		todayDay = today.get(Calendar.DAY_OF_MONTH);

		String updateToDate = todayYear + ". " + todayMonth + ". " + todayDay + ". ";
		String updateFromDate = todayYear + ". " + todayMonth + ". 1. ";

		toDateDetailView.setText(updateToDate);
		fromDateDetailView.setText(updateFromDate);

		toDateDetailView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Bundle tmpBdl = new Bundle();
				tmpBdl.putInt("toYear", todayYear);
				tmpBdl.putInt("toMonth", todayMonth);
				tmpBdl.putInt("toDay", todayDay);

				showDialog(SHOW_DATE_PICKER_TO, tmpBdl);
			}
		});

		fromDateDetailView.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Bundle tmpBdl = new Bundle();
				tmpBdl.putInt("toYear", todayYear);
				tmpBdl.putInt("toMonth", todayMonth);
				tmpBdl.putInt("toDay", 1);

				showDialog(SHOW_DATE_PICKER_FROM, tmpBdl);
			}
		});

	}

	/**
	 * Method detailAutoCategory 상세내역 수정을 위한 Dialog에서 카테고리의 자동선택을 위한 메소드
	 * @param category Category
	 * @return int Category List에서의 index
	 */
	public int detailAutoCategory(String category) {
		String[] detailCategory = new String[categoryList.length];
		for (int i = 0; i < categoryList.length; i++) {
			detailCategory[i] = getResources().getString(categoryList[i]);
		}

		for (int i = 0; i < categoryList.length; i++) {
			if (category.equals(detailCategory[i])) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADD_CASH_USE, 0, R.string.add_cash_use);
		menu.add(0, ADD_CARD_USE, 0, R.string.add_card_use);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		LayoutInflater dlgLayoutInflater;
		int tmpYear, tmpMonth, tmpDay;
		Date tmpDate;
		SimpleDateFormat tmpSdf = new SimpleDateFormat("yyyy-MM-dd");
		String showDate;

		switch (item.getItemId()) {
		case ADD_CASH_USE:
			dlgLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View addCashDetailView = dlgLayoutInflater.inflate(R.layout.detail_cash_use_add_layout, (ViewGroup) findViewById(R.id.detail_cash_use_add_root_view));

			addCashUseTime = (TextView) addCashDetailView.findViewById(R.id.detail_cash_use_time);
			addCashUsePlace = (EditText) addCashDetailView.findViewById(R.id.detail_cash_use_place);
			addCashUsePrice = (EditText) addCashDetailView.findViewById(R.id.detail_cash_use_price);
			addCashUseCategory = (TextView) addCashDetailView.findViewById(R.id.detail_cash_use_category);

			tmpYear = today.get(Calendar.YEAR);
			tmpMonth = today.get(Calendar.MONDAY);
			tmpDay = today.get(Calendar.DAY_OF_MONTH);

			tmpDate = new Date();
			tmpDate.setYear(tmpYear - 1900);
			tmpDate.setMonth(tmpMonth);
			tmpDate.setDate(tmpDay);
			showDate = tmpSdf.format(tmpDate);

			addCashUseTime.setText(showDate);
			addCashUseTime.setOnClickListener(new detailEditClickListener());

			addCashUseCategory.setText(R.string.c_etc);
			addCashUseCategory.setOnClickListener(new detailEditClickListener());

			AddCashDetailClickListener addcashListener = new AddCashDetailClickListener();
			
			new AlertDialog.Builder(DetailViewActivity.this)
			.setView(addCashDetailView)
			.setTitle(R.string.add_cash_use)
			.setPositiveButton(R.string.add_string, addcashListener)
			.setNegativeButton(R.string.cancel_string, addcashListener)
			.show();

			break;
			
		case ADD_CARD_USE:
			dlgLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View addDetailView = dlgLayoutInflater.inflate(R.layout.detail_list_add_dialog_layout, (ViewGroup) findViewById(R.id.detail_add_dlg_root_view));

			addDetailCardName = (EditText) addDetailView.findViewById(R.id.detail_add_card_name);
			addDetailCardNumber = (EditText) addDetailView.findViewById(R.id.detail_add_card_number);
			addDetailApprovalTime = (TextView) addDetailView.findViewById(R.id.detail_add_approval_time);
			addDetailPlace = (EditText) addDetailView.findViewById(R.id.detail_add_place);
			addDetailPrice = (EditText) addDetailView.findViewById(R.id.detail_add_price);
			addDetailCategory = (TextView) addDetailView.findViewById(R.id.detail_add_category);

			if (receivedIntent.hasExtra("cardName") && receivedIntent.hasExtra("cardNumber")) {
				addDetailCardName.setText(receivedIntent.getStringExtra("cardName"));
				addDetailCardNumber.setText(receivedIntent.getStringExtra("cardNumber"));
			}

			tmpYear = today.get(Calendar.YEAR);
			tmpMonth = today.get(Calendar.MONDAY);
			tmpDay = today.get(Calendar.DAY_OF_MONTH);

			tmpDate = new Date();
			tmpDate.setYear(tmpYear - 1900);
			tmpDate.setMonth(tmpMonth);
			tmpDate.setDate(tmpDay);

			showDate = tmpSdf.format(tmpDate);

			addDetailApprovalTime.setText(showDate);
			addDetailApprovalTime.setOnClickListener(new detailEditClickListener());

			addDetailCategory.setText(R.string.c_etc);
			addDetailCategory.setOnClickListener(new detailEditClickListener());

			AddDetailClickListener addDetailClickListener = new AddDetailClickListener();
			
			new AlertDialog.Builder(DetailViewActivity.this)
			.setView(addDetailView)
			.setTitle(R.string.add_card_use)
			.setPositiveButton(R.string.add_string, addDetailClickListener)
			.setNegativeButton(R.string.cancel_string, addDetailClickListener)
			.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * addDetailClickListener 상세내역 수정 Dialog에서 '수정', '취소'버튼의 Event Handler
	 * @author Junu Kim
	 */
	public class AddDetailClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			int primaryKey = 1000;
			
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				
				if (addDetailCardName.getText().toString().replace(" " , "").equals("")
					|| addDetailCardNumber.getText().toString().replace(" ", "").equals("")
					|| addDetailPlace.getText().toString().replace(" ", "").equals("")
					|| addDetailPrice.getText().toString().equals("")) {
					showDialog(INVALID_ALERT_DIALOG);
					break;
				}
					
				String tmpCardName = addDetailCardName.getText().toString();
				String tmpCardNumber = addDetailCardNumber.getText().toString();
				String tmpApprovalTime = addDetailApprovalTime.getText().toString();
				String tmpPlace = addDetailPlace.getText().toString();
				String tmpPrice = addDetailPrice.getText().toString();
				String tmpCategory = addDetailCategory.getText().toString();

				Cdb = new CardDB(getApplicationContext());
				db = Cdb.getWritableDatabase();

				String[] tmpYMD = tmpApprovalTime.split("-");

				String insertQuery = "INSERT INTO breakdowstats VALUES(null, '"
									+ tmpCardName + "', " + tmpYMD[0] + "," + tmpYMD[1]
									+ "," + tmpYMD[2] + ", '" + tmpPlace + "', " + tmpPrice
									+ ", '" + tmpCategory + "', '" + tmpCardNumber + "', "
									+ tmpYMD[0] + tmpYMD[1] + tmpYMD[2] + ", 0);";

				db.execSQL(insertQuery);
				
				String getKeyQuery = "SELECT breakKey FROM breakdowstats WHERE cardName = '" + tmpCardName + "' AND cardNumber = '"
						 + tmpCardNumber + "' AND combineDate = " + tmpYMD[0] + tmpYMD[1] + tmpYMD[2] + " AND deleteFlag = 0;";
				
				Cursor c = db.rawQuery(getKeyQuery, null);
				
				while (c.moveToNext()) {
					primaryKey = c.getInt(c.getColumnIndex("breakKey"));
				}
				db.close();

				SmsInfo tmpObj = new SmsInfo(primaryKey, tmpCardName, tmpCardNumber, tmpYMD[0] + "-" + tmpYMD[1] + "-" + tmpYMD[2], tmpPlace, Integer.parseInt(tmpPrice), tmpCategory);
				dAdapter.add(tmpObj);
				dAdapter.notifyDataSetChanged();
				sumPrice();

				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			}
		}
	}

	/**
	 * addCashDetailClickListener 현금사용 추가 Dialog의 Event Handler
	 * @author Junu Kim
	 */
	public class AddCashDetailClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				
				//When Place, Price were blank
				if (addCashUsePlace.getText().toString().replace(" ", "").equals("") 
						|| addCashUsePrice.getText().toString().equals(""))	{
					showDialog(INVALID_ALERT_DIALOG);
					break;
				}
				
				String tmpTime = addCashUseTime.getText().toString();
				String tmpPlace = addCashUsePlace.getText().toString();
				String tmpPrice = addCashUsePrice.getText().toString();
				String tmpCategory = addCashUseCategory.getText().toString();
				int primaryKey = 2000;
				
				Cdb = new CardDB(getApplicationContext());
				db = Cdb.getWritableDatabase();

				String[] tmpYMD = tmpTime.split("-");
				String cashString = getResources().getString(R.string.cash_use_list_string);

				String insertQuery = "INSERT INTO breakdowstats VALUES(null, '"
									 + cashString + "', " + tmpYMD[0] + "," + tmpYMD[1]
									 + "," + tmpYMD[2] + ", '" + tmpPlace + "', " + tmpPrice
									 + ", '" + tmpCategory + "', '', " + tmpYMD[0]
									 + tmpYMD[1] + tmpYMD[2] + ", 0);";

				db.execSQL(insertQuery);
				
				String getKeyQuery = "SELECT breakKey FROM breakdowstats WHERE cardName = '" + cashString + "' AND cardNumber = '' AND combineDate = "
									 + tmpYMD[0] + tmpYMD[1] + tmpYMD[2] + " AND deleteFlag = 0 AND pPlace = '" + tmpPlace +"';";
				
				Cursor c = db.rawQuery(getKeyQuery, null);
				
				while (c.moveToNext()) {
					primaryKey = c.getInt(c.getColumnIndex("breakKey"));
				}
				
				db.close();

				SmsInfo tmpObj = new SmsInfo(primaryKey, cashString, "", tmpYMD[0] + "-" + tmpYMD[1] + "-" + tmpYMD[2], tmpPlace, Integer.parseInt(tmpPrice), tmpCategory);
				dAdapter.add(tmpObj);
				dAdapter.notifyDataSetChanged();
				sumPrice();

				break;
			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			}
		}
	}
	
	public class EditCashDialogListener implements DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE :
				
				if (addCashUseEditPlace.getText().toString().replace(" ", "").equals("") 
						|| addCashUseEditPrice.getText().toString().equals("")) {
					removeDialog(EDIT_CASH_DIALOG);
					showDialog(INVALID_ALERT_DIALOG);
					break;
				} else if (beforeCashUseTime.equals(addCashUseEditTime.getText().toString())
						&& beforeCashUsePlace.equals(addCashUseEditPlace.getText().toString())
						&& beforeCashUsePrice.equals(addCashUseEditPrice.getText().toString())
						&& beforeCashUseCategory.equals(addCashUseEditCategory.getText().toString())) {
					removeDialog(EDIT_CASH_DIALOG);
					break;
				}  else {
					CardDB Cdb = new CardDB(DetailViewActivity.this);
					String[] newCashUseTimeArray = addCashUseEditTime.getText().toString().split("-");
					String newCashUseTime = addCashUseEditTime.getText().toString().replace("-", "");
					
					db = Cdb.getReadableDatabase();

					String updateQuery = "UPDATE breakdowstats SET pYear = "+ newCashUseTimeArray[0] + ", pMonth = "+ newCashUseTimeArray[1] + ", pDay = " + newCashUseTimeArray[2]
										 + ", combineDate = "+ newCashUseTime + ", pPlace = '"+ addCashUseEditPlace.getText().toString()
										 + "', price = "+ addCashUseEditPrice.getText().toString() + ", category = '"+ addCashUseEditCategory.getText().toString() + "' WHERE breakKey = "
										 + dAdapter.getItem(clickedPos).getBreakKey() + ";";
					db.execSQL(updateQuery);
					db.close();
					
					dAdapter.getItem(clickedPos).setApprovalTime(addCashUseEditTime.getText().toString());
					dAdapter.getItem(clickedPos).setPlace(addCashUseEditPlace.getText().toString());
					dAdapter.getItem(clickedPos).setPrice(Integer.parseInt(addCashUseEditPrice.getText().toString()));
					dAdapter.getItem(clickedPos).setCategory(addCashUseEditCategory.getText().toString());
					
					dAdapter.notifyDataSetChanged();
					removeDialog(EDIT_CASH_DIALOG);
				}
				break;
				
			case DialogInterface.BUTTON_NEGATIVE :
				removeDialog(EDIT_CASH_DIALOG);
				break;
			}
			
		}
		
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		setResult(0,intent);
		super.finish();
	}
}
