package kr.ac.hansung;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCardActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card_tmp);
		
		ArrayList<MyCardInfo> myCardList = new ArrayList<MyCardInfo>();

		MyCardInfo tmpCard = new MyCardInfo("NH Ä«µå", "0012 2341 3482 3848");

		myCardList.add(tmpCard);

	//	MyCardAdapter mAdapter = new MyCardAdapter(this, R.layout.my_card_list_layout, myCardList);
	//	setListAdapter(mAdapter);
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
				ImageView tmpCImage = (ImageView) findViewById(R.id.card_imageview);
				ImageView tmpPImage = (ImageView) findViewById(R.id.card_phone_imageview);
				TextView tmpCName = (TextView) findViewById(R.id.card_name_textview);
				TextView tmpCNum = (TextView) findViewById(R.id.card_num_textview);
				
				tmpCImage.setImageResource(m.getCardImage());
				tmpCName.setText(m.getCardName());
				tmpCNum.setText(m.getCardNumber());
				tmpPImage.setImageResource(m.getPhoneImage());
			}
			return v;

		}
	}

}
