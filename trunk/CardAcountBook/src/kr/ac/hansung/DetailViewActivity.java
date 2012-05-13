package kr.ac.hansung;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DetailViewActivity extends ListActivity implements CategoryList {
	final static int DETAIL_DATE_EDIT_PICKER = 0;
	final static int DETAIL_DATE_ADD_PICKER = 5;
	
	final static int SHOW_DATE_PICKER_TO = 1;
	final static int SHOW_DATE_PICKER_FROM = 2;
	
	final static int ADD_CASH_USE = 3;
	final static int ADD_CARD_USE = 4;
	
	SQLiteDatabase db;
	CardDB Cdb;
	Intent receivedIntent;
	
	Calendar today;
	
	ArrayList<SmsInfo> detailViewList = new ArrayList<SmsInfo>();
	DetailViewAdapter dAdapter;
	ListView detailListView;
	TextView editApprovalTime;
	TextView detailDlgCategory;
	
	TextView fromDateDetailView;
	TextView toDateDetailView;
	TextView detailPriceView;
	
	
	EditText addDetailCardName;
	EditText addDetailCardNumber;
	TextView addDetailApprovalTime;
	EditText addDetailPlace;
	EditText addDetailPrice;
	TextView addDetailCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view);
		
		today = Calendar.getInstance();

		fromToDateChange();
		
		receivedIntent = getIntent();
		dynamicListAdd(dynamicDatabaseCursor(receivedIntent));
	}
	
	public void dynamicListAdd(Cursor detailCursor) {
		int price = 0;
		
		detailViewList.clear();
		detailPriceView = (TextView) findViewById(R.id.detail_price_view);
		
		while (detailCursor.moveToNext()) {

			int primaryKey = detailCursor.getInt(detailCursor.getColumnIndex("breakKey"));
			String cName = detailCursor.getString(1);
			int pYear = detailCursor.getInt(2) - 1900;
			int pMonth = detailCursor.getInt(3) - 1;
			int pDay = detailCursor.getInt(4);
			String pPlace = detailCursor.getString(5);
			int pPrice = detailCursor.getInt(6);
			String category = detailCursor.getString(7);
			String tmpCardNumber = detailCursor.getString(detailCursor.getColumnIndex("cardNumber"));

			Date date = null;
			date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			date.setYear(pYear);
			date.setMonth(pMonth);
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
		
		setListAdapter(dAdapter);
		sumPrice();
	}
	
	public void sumPrice() {
		int price = 0;
		
		for (int i = 0; i < dAdapter.getCount(); i++) {
			price = price + dAdapter.getItem(i).getPrice();
		}
		detailPriceView.setText(SmsInfo.decimalPointToString(price));
	}
	
	public Cursor dynamicDatabaseCursor(TextView fromDate, TextView toDate) {
		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();
		Cursor tmpCursor;
		String tmpStr;
		
		String[] fromCombineDate = fromDate.getText().toString().replace(" ", "").split("\\.");
		String[] toCombineDate = toDate.getText().toString().replace(" ", "").split("\\.");;
		
		SimpleDateFormat tmpFormat = new SimpleDateFormat("yyyyMMdd");
		Date tmpFromDate = new Date();
		Date tmpToDate = new Date();
		
		tmpFromDate.setYear(Integer.parseInt(fromCombineDate[0]) - 1900);
		tmpFromDate.setMonth(Integer.parseInt(fromCombineDate[1]) - 1);
		tmpFromDate.setDate(Integer.parseInt(fromCombineDate[2]));
		
		tmpToDate.setYear(Integer.parseInt(toCombineDate[0]) - 1900);
		tmpToDate.setMonth(Integer.parseInt(toCombineDate[1]) - 1);
		tmpToDate.setDate(Integer.parseInt(toCombineDate[2]));
		
		if (receivedIntent.hasExtra("cardName") && receivedIntent.hasExtra("cardNumber")) {
			tmpStr = "SELECT * FROM breakdowstats WHERE cardName = '"
					+ receivedIntent.getStringExtra("cardName")
					+ "' AND cardNumber = '"
					+ receivedIntent.getStringExtra("cardNumber") + "' AND combineDate >= "
					+ tmpFormat.format(tmpFromDate) + " AND combineDate <= " 
					+ tmpFormat.format(tmpToDate) + " ORDER BY combineDate DESC;";
		} else {
			tmpStr = "SELECT * FROM breakdowstats WHERE combineDate >= " 
					+ tmpFormat.format(tmpFromDate) + " AND combineDate <= " 
					+ tmpFormat.format(tmpToDate) + " ORDER BY combineDate DESC;";
		}
		
		tmpCursor = db.rawQuery(tmpStr, null);
		
		return tmpCursor;
	}
	
	public Cursor dynamicDatabaseCursor(Intent intent) {
		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();
		Cursor tmpCursor;
		
		String[] fromCombineDate = fromDateDetailView.getText().toString().replace(" ", "").split("\\.");
		String[] toCombineDate = toDateDetailView.getText().toString().replace(" ", "").split("\\.");;
		
		SimpleDateFormat tmpFormat = new SimpleDateFormat("yyyyMMdd");
		Date tmpFromDate = new Date();
		Date tmpToDate = new Date();
		
		tmpFromDate.setYear(Integer.parseInt(fromCombineDate[0]) - 1900);
		tmpFromDate.setMonth(Integer.parseInt(fromCombineDate[1]) - 1);
		tmpFromDate.setDate(Integer.parseInt(fromCombineDate[2]));
		
		tmpToDate.setYear(Integer.parseInt(toCombineDate[0]) - 1900);
		tmpToDate.setMonth(Integer.parseInt(toCombineDate[1]) - 1);
		tmpToDate.setDate(Integer.parseInt(toCombineDate[2]));
		
		if (intent.hasExtra("selMonth")) {
			int selMonth = (int) intent.getDoubleExtra("selMonth", 0);
			String strQuery = "SELECT * FROM breakdowstats WHERE pMonth =" + selMonth + ";";
			tmpCursor = db.rawQuery(strQuery, null);
		} else if (intent.hasExtra("cardName") && intent.hasExtra("cardNumber")) {
			String strQuery = "SELECT * FROM breakdowstats WHERE cardName = '"
								+ intent.getStringExtra("cardName")
								+ "' AND cardNumber = '"
								+ intent.getStringExtra("cardNumber") + "' AND combineDate >= "
								+ tmpFormat.format(tmpFromDate) + " AND combineDate <= " 
								+ tmpFormat.format(tmpToDate) + " ORDER BY combineDate DESC;";
			tmpCursor = db.rawQuery(strQuery, null);
		} else {
			String tmpStr = "SELECT * FROM breakdowstats WHERE combineDate >= " 
							+ tmpFormat.format(tmpFromDate) + " AND combineDate <= " 
							+ tmpFormat.format(tmpToDate) + " ORDER BY combineDate DESC;";
			tmpCursor = db.rawQuery(tmpStr, null);
		}	
		
		return tmpCursor;
	}
	
	public class detailViewListItemLongClickListener implements AdapterView.OnItemLongClickListener {
		
		public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
			final int position = pos;
			LayoutInflater dlgLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View editDlgView = dlgLayoutInflater.inflate(R.layout.detail_list_edit_dialog_layout, (ViewGroup) findViewById(R.id.detail_edit_dlg_root_view));

			final EditText editCardName = (EditText) editDlgView.findViewById(R.id.detail_edit_card_name);
			final EditText editCardNumber= (EditText) editDlgView.findViewById(R.id.detail_edit_card_number);
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
			
			new AlertDialog.Builder(DetailViewActivity.this)
			.setTitle(getResources().getString(R.string.detail_dlg_edit_title))
			.setPositiveButton(R.string.edit_string, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String[] tmpDate = editApprovalTime.getText().toString().split("-");
					
					if (beforeCardName.equals(editCardName.getText().toString()) 
							&& beforeCardNumber.equals(editCardNumber.getText().toString())
							&& beforeApprovalTime.equals(editApprovalTime.getText().toString())
							&& beforePlace.equals(editPlace.getText().toString())
							&& beforePrice.equals(editPrice.getText().toString())
							&& beforeCategory.equals(editCategory.getText().toString())) {
						
					} else if (!beforeCardName.equals(editCardName.getText().toString())
							|| !beforeCardNumber.equals(editCardNumber.getText().toString())) {
						CardDB Cdb = new CardDB(DetailViewActivity.this);
						db = Cdb.getReadableDatabase();
						
						String checkQuery = "SELECT * FROM breakdowstats WHERE cardName = '" + editCardName.getText().toString()
											+ "' AND cardNumber = '" + editCardNumber.getText().toString()  +"';";
						Cursor tmpCursor = db.rawQuery(checkQuery, null);
						
						if (tmpCursor.getCount() == 0) {
							String addCardQuery = "INSERT INTO myCard VALUES (null, '" 
									+ editCardName.getText().toString() + "', '"
									+ editCardNumber.getText().toString() + "', null, null, null);";
							db.execSQL(addCardQuery);
						}
						
						String updateQuery = "UPDATE breakdowstats SET cardName = '" + editCardName.getText().toString()
											 + "', cardNumber = '" + editCardNumber.getText().toString()
											 + "' WHERE breakKey = " + dAdapter.getItem(position).getBreakKey() + ";";
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

						String updateQuery = 
								"UPDATE breakdowstats SET cardName = '"+ editCardName.getText().toString() + "', cardNumber = '" + 
								editCardNumber.getText().toString() + "', pYear = " + Integer.parseInt(tmpDate[0]) + ", pMonth = " + Integer.parseInt(tmpDate[1]) +
								", pDay = " + Integer.parseInt(tmpDate[2]) + ", combineDate = " + Integer.parseInt(editApprovalTime.getText().toString().replace("-", "")) +
								", pPlace = '" + editPlace.getText().toString() + "', price = " + Integer.parseInt(editPrice.getText().toString()) +
								", category = '" + editCategory.getText().toString() + "' WHERE breakKey = " + dAdapter.getItem(position).getBreakKey() + ";";
						
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
				}
			})
			.setCancelable(false)
			.setNegativeButton(R.string.cancel_string, null)
			.setView(editDlgView).show();
			
			return true;
		}
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		LayoutInflater dlgLayoutInflater = (LayoutInflater) this.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View dlgView = dlgLayoutInflater.inflate(R.layout.detail_list_dialog_layout, (ViewGroup) findViewById(R.id.detail_dlg_root_view));
		
		TextView detailDlgApprovalTime = (TextView) dlgView.findViewById(R.id.detail_dlg_approval_time);
		TextView detailDlgPlace = (TextView) dlgView.findViewById(R.id.detail_dlg_place);
		TextView detailDlgPrice = (TextView) dlgView.findViewById(R.id.detail_dlg_price);
		detailDlgCategory = (TextView) dlgView.findViewById(R.id.detail_dlg_category);
		
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy. MM. dd. (E)");


		SmsInfo tmpSmsInfo = dAdapter.getItem(position);
		
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
		String dlgTitle = dAdapter.getItem(position).getCardName() + " (" + dAdapter.getItem(position).getCardNumber() + ")";
		
		new AlertDialog.Builder(DetailViewActivity.this)
		.setTitle(dlgTitle)
		.setView(dlgView).show();
	}

	// MyCardAdapter
	public class DetailViewAdapter extends ArrayAdapter<SmsInfo> {

		private ArrayList<SmsInfo> items;

		public DetailViewAdapter(Context context, int textViewResourceId,
				ArrayList<SmsInfo> objects) {
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
				TextView tmpCategory = (TextView)v.findViewById(R.id.d_category_view);

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
		switch (id) {
		case DETAIL_DATE_EDIT_PICKER :
			return new DatePickerDialog(this, detailDateEditPickerListener,	bdl.getInt("aprvlYear"), bdl.getInt("aprvlMonth") - 1, bdl.getInt("aprvlDay"));
		case DETAIL_DATE_ADD_PICKER :
			return new DatePickerDialog(this, detailDateAddPickerListener,	bdl.getInt("aprvlYear"), bdl.getInt("aprvlMonth") - 1, bdl.getInt("aprvlDay"));
		case SHOW_DATE_PICKER_TO:
			return new DatePickerDialog(this, toDetailDateSetListener,
					bdl.getInt("toYear"), bdl.getInt("toMonth") - 1,
					bdl.getInt("toDay"));

		case SHOW_DATE_PICKER_FROM:
			return new DatePickerDialog(this, fromDetailDateSetListener,
					bdl.getInt("toYear"), bdl.getInt("toMonth") - 1,
					bdl.getInt("toDay"));
		}
		return super.onCreateDialog(id, bdl);
	}
	
	// ���߿� DatePickerListener �̰ɷ� ��������
//	public class onMyDateSetListner implements DatePickerDialog.OnDateSetListener {
//		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//		}
//		
//	}
	
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
	

	public class detailEditClickListener implements View.OnClickListener {

		public void onClick(View v) {
			final TextView tmpView = (TextView)v;
			Bundle aprvlBdl;
			String[] tmpAprvl;
			switch (v.getId()) {
			case R.id.detail_edit_approval_time :
				aprvlBdl = new Bundle();
				tmpAprvl = ((TextView)v).getText().toString().split("-");
				aprvlBdl.putInt("aprvlYear", Integer.parseInt(tmpAprvl[0]));
				aprvlBdl.putInt("aprvlMonth", Integer.parseInt(tmpAprvl[1]));
				aprvlBdl.putInt("aprvlDay", Integer.parseInt(tmpAprvl[2]));
				showDialog(DETAIL_DATE_EDIT_PICKER, aprvlBdl);
				break;
				
			case R.id.detail_add_approval_time :
				aprvlBdl = new Bundle();
				tmpAprvl = ((TextView)v).getText().toString().split("-");
				aprvlBdl.putInt("aprvlYear", Integer.parseInt(tmpAprvl[0]));
				aprvlBdl.putInt("aprvlMonth", Integer.parseInt(tmpAprvl[1]));
				aprvlBdl.putInt("aprvlDay", Integer.parseInt(tmpAprvl[2]));
				showDialog(DETAIL_DATE_ADD_PICKER, aprvlBdl);
				break;
				
			case R.id.detail_edit_category :
			case R.id.detail_add_category :
				final String[] detailCategory = new String[categoryList.length];
				String category = tmpView.getText().toString();
			
				for (int i=0; i<categoryList.length; i++) {
					detailCategory[i] = getResources().getString(categoryList[i]);
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
				})
				.show();
				
				break;
			}
		}
	}
	
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
	
	public int detailAutoCategory(String category) {
		String[] detailCategory = new String[categoryList.length];
		for (int i=0; i<categoryList.length; i++) {
			detailCategory[i] = getResources().getString(categoryList[i]);
		}
		
		for (int i=0; i<categoryList.length; i++) {
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
		
		switch (item.getItemId()) {
		case ADD_CASH_USE :
			break;
		case ADD_CARD_USE :
			LayoutInflater dlgLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View addDetailView = dlgLayoutInflater.inflate(R.layout.detail_list_add_dialog_layout, (ViewGroup) findViewById(R.id.detail_add_dlg_root_view));

			addDetailCardName = (EditText) addDetailView.findViewById(R.id.detail_add_card_name);
			addDetailCardNumber= (EditText) addDetailView.findViewById(R.id.detail_add_card_number);
			addDetailApprovalTime = (TextView) addDetailView.findViewById(R.id.detail_add_approval_time);
			addDetailPlace = (EditText) addDetailView.findViewById(R.id.detail_add_place);
			addDetailPrice = (EditText) addDetailView.findViewById(R.id.detail_add_price);
			addDetailCategory = (TextView) addDetailView.findViewById(R.id.detail_add_category);
		
			if (receivedIntent.hasExtra("cardName") && receivedIntent.hasExtra("cardNumber")) {
				addDetailCardName.setText(receivedIntent.getStringExtra("cardName"));
				addDetailCardNumber.setText(receivedIntent.getStringExtra("cardNumber"));
			}
			
			int tmpYear = today.get(Calendar.YEAR);
			int tmpMonth = today.get(Calendar.MONDAY);
			int tmpDay = today.get(Calendar.DAY_OF_MONTH);
			
			Date tmpDate = new Date();
			tmpDate.setYear(tmpYear - 1900);
			tmpDate.setMonth(tmpMonth);
			tmpDate.setDate(tmpDay);
			
			SimpleDateFormat tmpSdf = new SimpleDateFormat("yyyy-MM-dd");
			String showDate = tmpSdf.format(tmpDate);
			
			addDetailApprovalTime.setText(showDate);
			addDetailApprovalTime.setOnClickListener(new detailEditClickListener());
			
			addDetailCategory.setText(R.string.c_etc);
			addDetailCategory.setOnClickListener(new detailEditClickListener());
			
			new AlertDialog.Builder(DetailViewActivity.this)
			.setView(addDetailView)
			.setTitle(R.string.add_card_use)
			.setPositiveButton(R.string.add_string, new addDetailClickListener())
			.setNegativeButton(R.string.cancel_string, new addDetailClickListener())
			.show();

			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public class addDetailClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE :
				String tmpCardName = addDetailCardName.getText().toString();
				String tmpCardNumber =  addDetailCardNumber.getText().toString();
				String tmpApprovalTime = addDetailApprovalTime.getText().toString();
				String tmpPlace = addDetailPlace.getText().toString();
				String tmpPrice = addDetailPrice.getText().toString();
				String tmpCategory = addDetailCategory.getText().toString();
				
				Cdb = new CardDB(getApplicationContext());
				db = Cdb.getWritableDatabase();
				
				String[] tmpYMD = tmpApprovalTime.split("-"); 
				
				String insertQuery = "INSERT INTO breakdowstats VALUES(null, '" 
									+ tmpCardName + "', " + tmpYMD[0] + "," + tmpYMD[1] + "," + tmpYMD[2]
									+ ", '" + tmpPlace + "', " + tmpPrice + ", '" + tmpCategory + "', '" 
									+ tmpCardNumber + "', " + tmpYMD[0] + tmpYMD[1] + tmpYMD[2] + ");";
				
				db.execSQL(insertQuery);
				db.close();
				
				SmsInfo tmpObj = new SmsInfo(tmpCardName, tmpCardNumber, tmpYMD[0] + "-" + tmpYMD[1] + "-" + tmpYMD[2], tmpPlace, Integer.parseInt(tmpPrice), tmpCategory);
				dAdapter.add(tmpObj);
				dAdapter.notifyDataSetChanged();
				sumPrice();
				//���ñ� �� ���� ����� ����� �ȵȴ� 
				
				break;
			case DialogInterface.BUTTON_NEGATIVE :
				dialog.dismiss();
				break;
			}
		}
	}
}
