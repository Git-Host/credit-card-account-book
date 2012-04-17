package kr.ac.hansung;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class DetailViewActivity extends ListActivity {

	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view);
		
		ArrayList<SmsInfo> detailViewList = new ArrayList<SmsInfo>();
		
		
		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();
		Intent intent = getIntent();
		
		if(!intent.hasExtra("selMonth"))
			c =db.rawQuery("Select * From breakdowstats;", null);
		else if(intent.hasExtra("selMonth")){
			
			int selMonth = (int)intent.getDoubleExtra("selMonth", 0);
			
			String strQuery = "Select * From breakdowstats where pMonth ="+selMonth+";";
			c = db.rawQuery(strQuery, null);
		}
//		db.execSQL("INSERT INTO breakdowstats VALUES(1,'±ππŒ',2012,2,4,'øæ∞Ì¿ª',128000,'¿Ø»Ô∫Ò');");
		 
		
		while(c.moveToNext()){
			
			String cName = c.getString(1);		
			int pYear = c.getInt(2)-1900;
			int pMonth = c.getInt(3)-1;
			int pDay = c.getInt(4);
			String pPlace = c.getString(5);
			int pPrice = c.getInt(6);
			String category = c.getString(7);
			
			Date date = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			date = new Date();
			
			date.setYear(pYear);
			date.setMonth(pMonth);
			date.setDate(pDay);
			String sDate = dateFormat.format(date);
			
								
			SmsInfo tmp = new SmsInfo(cName);
			tmp.setApprovalTime(sDate);
			tmp.setPlace(pPlace);
			tmp.setPrice(pPrice);
			tmp.setCategory(category);
			detailViewList.add(tmp);
			
		}
		db.close();
		
		
		DetailViewAdapter dAdapter = new DetailViewAdapter(this,
				R.layout.detail_view_list_layout, detailViewList);

		setListAdapter(dAdapter);
		
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

				tmpATime.setText(m.getApprovalTime());
				tmpCName.setText(m.getCardName());
				tmpPlace.setText(m.getPlace());
				tmpPrice.setText(String.valueOf(m.getPrice()));
			}
			return v;

		}
	}

}
