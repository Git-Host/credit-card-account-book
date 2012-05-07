package kr.ac.hansung;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DetailViewActivity extends ListActivity {

	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;

	DetailViewAdapter dAdapter;
	ListView detailListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view);

		ArrayList<SmsInfo> detailViewList = new ArrayList<SmsInfo>();

		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();
		Intent intent = getIntent();
		
		if (intent.hasExtra("selMonth")) {
			int selMonth = (int) intent.getDoubleExtra("selMonth", 0);
			String strQuery = "Select * From breakdowstats where pMonth =" + selMonth + ";";
			c = db.rawQuery(strQuery, null);
		} else if (intent.hasExtra("cardName") && intent.hasExtra("cardNumber")) {
			String strQuery = "SELECT * FROM breakdowstats WHERE cardName = '"
					+ intent.getStringExtra("cardName")
					+ "' AND cardNumber = '"
					+ intent.getStringExtra("cardNumber") + "' ORDER BY combineDate DESC;";
			c = db.rawQuery(strQuery, null);
		} else {
			c = db.rawQuery("SELECT * FROM breakdowstats ORDER BY combineDate DESC;", null);
		}

		while (c.moveToNext()) {

			String cName = c.getString(1);
			int pYear = c.getInt(2) - 1900;
			int pMonth = c.getInt(3) - 1;
			int pDay = c.getInt(4);
			String pPlace = c.getString(5);
			int pPrice = c.getInt(6);
			String category = c.getString(7);
			String tmpCardNumber = c.getString(c.getColumnIndex("cardNumber"));

			Date date = null;
			date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			date.setYear(pYear);
			date.setMonth(pMonth);
			date.setDate(pDay);
			String sDate = dateFormat.format(date);

			SmsInfo tmp = new SmsInfo(cName);
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
	}
	
	public class detailViewListItemLongClickListener implements AdapterView.OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			LayoutInflater dlgLayoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
			View editDlgView = dlgLayoutInflater.inflate(R.layout.detail_list_edit_dialog_layout, (ViewGroup) findViewById(R.id.detail_edit_dlg_root_view));
			
			EditText editCardName = (EditText) editDlgView.findViewById(R.id.detail_edit_card_name);
			EditText editCardNumber= (EditText) editDlgView.findViewById(R.id.detail_edit_card_number);
			TextView editApprovalTime = (TextView) editDlgView.findViewById(R.id.detail_edit_approval_time);
			EditText editPlace = (EditText) editDlgView.findViewById(R.id.detail_edit_place);
			EditText editPrice = (EditText) editDlgView.findViewById(R.id.detail_edit_price);
			TextView editCategory = (TextView) editDlgView.findViewById(R.id.detail_edit_category);
			
			editCardName.setText(dAdapter.getItem(position).getCardName());
			editCardName.setSelection(dAdapter.getItem(position).getCardName().length());
			
			editCardNumber.setText(dAdapter.getItem(position).getCardNumber());
			editCardNumber.setSelection(dAdapter.getItem(position).getCardNumber().length());
			
			editApprovalTime.setText(dAdapter.getItem(position).getApprovalTime());
			
			editPlace.setText(dAdapter.getItem(position).getPlace());
			editPlace.setSelection(dAdapter.getItem(position).getPlace().length());
			
			editPrice.setText(String.valueOf(dAdapter.getItem(position).getPrice()));
			editPrice.setSelection(String.valueOf(dAdapter.getItem(position).getPrice()).length());
			
			editCategory.setText(dAdapter.getItem(position).getCategory());
			
			new AlertDialog.Builder(DetailViewActivity.this)
			.setTitle(getResources().getString(R.string.detail_dlg_edit_title))
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
		TextView detailDlgCategory = (TextView) dlgView.findViewById(R.id.detail_dlg_category);
		
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
		
		DecimalFormat df = new DecimalFormat("#,##0");
		String decimalPoint = df.format(dAdapter.getItem(position).getPrice()) + "¿ø";
		detailDlgPrice.setText(decimalPoint);
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
				TextView tmpATime = (TextView) v
						.findViewById(R.id.d_approval_time_view);
				TextView tmpCName = (TextView) v
						.findViewById(R.id.d_card_name_view);
				TextView tmpPlace = (TextView) v
						.findViewById(R.id.d_place_view);
				TextView tmpPrice = (TextView) v
						.findViewById(R.id.d_price_view);
				TextView tmpCategory = (TextView)v
						.findViewById(R.id.d_category_view);

				DecimalFormat df = new DecimalFormat("#,##0");
				String decimalPoint = df.format(m.getPrice()) + "¿ø";
				
				tmpATime.setText(m.getApprovalTime().substring(5));
				tmpCName.setText(m.getCardName());
				tmpPlace.setText(m.getPlace());
				tmpPrice.setText(decimalPoint);
				tmpCategory.setText(m.getCategory());
			}
			return v;
		}
	}
}
