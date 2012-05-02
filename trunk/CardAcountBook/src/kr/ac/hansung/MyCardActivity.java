package kr.ac.hansung;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCardActivity extends ListActivity {
	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;
	ListView cardListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card_tmp);
		
		ArrayList<MyCardInfo> myCardList = new ArrayList<MyCardInfo>();

		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();

		String myCardQuery = "SELECT DISTINCT cardName, cardNumber FROM breakdowstats;";
		c = db.rawQuery(myCardQuery, null);

		while (c.moveToNext()) {
			String dbCardName = c.getString(c.getColumnIndex("cardName"));
			String dbCardNumber = c.getString(c.getColumnIndex("cardNumber"));
			
			Log.v("JUNU", dbCardName + dbCardNumber);
			
			MyCardInfo tmpCardInfo = new MyCardInfo();
			tmpCardInfo.setCardName(dbCardName);
			tmpCardInfo.setCardNumber(dbCardNumber);
			tmpCardInfo.setCardImage(R.drawable.questionmark_card);
	
			myCardList.add(tmpCardInfo);
		}
		db.close();

		MyCardAdapter mAdapter = new MyCardAdapter(this, R.layout.my_card_list_layout, myCardList);
		setListAdapter(mAdapter);
		
		
		cardListView = this.getListView();
	
		cardListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Toast.makeText(MyCardActivity.this, "메롱", Toast.LENGTH_LONG).show();
				return true;
			}
		});
		
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		LinearLayout clickedLiear = (LinearLayout) findViewById(R.id.card_linearlayout);
		ImageView clickedCard = (ImageView) findViewById(R.id.card_imageview);
		ImageView clickedPhone= (ImageView) findViewById(R.id.card_phone_imageview);
		
		/*여
		 * 기
		 * 부
		 * 터
		 * 하
		 * 자
		 * ~
		 *  
		 *  뭐
		 *  할
		 *  차
		 *  례
		 *  나
		 *  면
		 *   
		 *   리
		 *   스
		 *   트
		 *   뷰
		 *   에
		 *   서
		 *    
		 *    뭐
		 *    누
		 *    르
		 *    면
		 *     
		 *     각
		 *     각
		 *      
		 *      기
		 *      능
		 *      수
		 *      행
		 *      하
		 *      기
		 * */
		clickedPhone.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MyCardActivity.this, "여기", Toast.LENGTH_LONG).show();
			}
		});
	}


	// MyCardAdapter 
	public class MyCardAdapter extends ArrayAdapter<MyCardInfo> {

		private ArrayList<MyCardInfo> items;

		public MyCardAdapter(Context context, int textViewResourceId,
				ArrayList<MyCardInfo> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.my_card_list_layout, null);
			}
			
			MyCardInfo m = items.get(position);
			
			if (m != null) {
				ImageView tmpCImage = (ImageView) v.findViewById(R.id.card_imageview);
				ImageView tmpPImage = (ImageView) v.findViewById(R.id.card_phone_imageview);
				TextView tmpCName = (TextView) v.findViewById(R.id.card_name_textview);
				TextView tmpCNum = (TextView) v.findViewById(R.id.card_num_textview);
				
				tmpCImage.setImageResource(m.getCardImage());
				tmpPImage.setImageResource(m.getPhoneImage());
				tmpCName.setText(m.getCardName());
				tmpCNum.setText(m.getCardNumber());
			}
			return v;

		}
	}

}
