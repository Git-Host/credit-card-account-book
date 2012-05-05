package kr.ac.hansung;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCardActivity extends ListActivity {
	private static final int GO_EDIT_CARD_OPTION = 1;
	
	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;
	MyCardAdapter mAdapter;
	
	ListView cardListView;
	TextView clickedTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card_tmp);
		
		ArrayList<MyCardInfo> myCardList = new ArrayList<MyCardInfo>();

		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();

		String myCardQuery = "SELECT cardName, cardNumber FROM myCard;";
		c = db.rawQuery(myCardQuery, null);

		while (c.moveToNext()) {
			String dbCardName = c.getString(c.getColumnIndex("cardName"));
			String dbCardNumber = c.getString(c.getColumnIndex("cardNumber"));
			
			MyCardInfo tmpCardInfo = new MyCardInfo();
			tmpCardInfo.setCardName(dbCardName);
			tmpCardInfo.setCardNumber(dbCardNumber);
			tmpCardInfo.setCardImage(setAutoCardImage(dbCardName));
	
			myCardList.add(tmpCardInfo);
		}
		db.close();

		mAdapter = new MyCardAdapter(this, R.layout.my_card_list_layout, myCardList);
		setListAdapter(mAdapter);
		
		
		cardListView = this.getListView();
		cardListView.setOnItemLongClickListener(new myCardListItemLongClickListener());
			
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class myCardListItemLongClickListener implements AdapterView.OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			Bundle containBdl = new Bundle();
			
			containBdl.putString("cardName", mAdapter.getItem(position).getCardName());
			containBdl.putString("cardNumber", mAdapter.getItem(position).getCardNumber());
			containBdl.putInt("imageRsc", mAdapter.getItem(position).getCardImage());
			
			Intent containItent = new Intent(MyCardActivity.this, CardInfoEditActivity.class);
			containItent.putExtras(containBdl);
			startActivityForResult(containItent, GO_EDIT_CARD_OPTION);
			
			return true;
		}
		
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent clickedCardDetail = new Intent(MyCardActivity.this, DetailViewActivity.class);
		
		clickedCardDetail.putExtra("cardName", mAdapter.getItem(position).getCardName());
		clickedCardDetail.putExtra("cardNumber", mAdapter.getItem(position).getCardNumber());
		
		startActivity(clickedCardDetail);
	}

	
	public void callCardCompany(TextView v) {
		TextView tmpTv = v;
		Resources tmpRes = this.getResources();
		Intent callCompany;

		if (tmpTv.getText().equals(tmpRes.getString(R.string.NH_card))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_NH)));
			startActivity(callCompany);
		} else if (tmpTv.getText().equals(tmpRes.getString(R.string.KB_check)) || tmpTv.getText().equals(tmpRes.getString(R.string.KB_credit))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_KB)));
			startActivity(callCompany);
		} else {
			// Other Card Phone Number Add
		}
	}
	
	public int setAutoCardImage(String cardName) {
		Resources autoCardRsc = this.getResources();
		
		if (cardName.equals(autoCardRsc.getString(R.string.NH_card))) {
			return (int)(R.drawable.nh_chaum);
		} else if (cardName.equals(autoCardRsc.getString(R.string.KB_check))
				|| cardName.equals(autoCardRsc.getString(R.string.KB_credit))) {
			return (int)(R.drawable.kb_star);
		}
		
		return (int)(R.drawable.questionmark_card);
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
				final TextView tmpCName = (TextView) v.findViewById(R.id.card_name_textview);
				TextView tmpCNum = (TextView) v.findViewById(R.id.card_num_textview);

				tmpCImage.setImageResource(m.getCardImage());
				tmpPImage.setImageResource(m.getPhoneImage());
				tmpCName.setText(m.getCardName());
				tmpCNum.setText(m.getCardNumber());
				
				tmpPImage.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						callCardCompany(tmpCName);
					}
					
				});
				
			}
			return v;
		}
	}

}
