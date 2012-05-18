package kr.ac.hansung;

import java.util.ArrayList;

import kr.ac.hansung.MyCardActivity.MyCardAdapter;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardListActivity extends ListActivity {
	MyCardAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ArrayList<MyCardInfo> myCardList = new ArrayList<MyCardInfo>();
		
		
		
		
		mAdapter = new MyCardAdapter(this, R.layout.my_card_list_layout, myCardList);
		setListAdapter(mAdapter);
		
		super.onCreate(savedInstanceState);
	}
	
	
	public class MyCardAdapter extends ArrayAdapter<MyCardInfo> {
		private ArrayList<MyCardInfo> items;
		
		public MyCardAdapter(Context context, int textViewResourceId, ArrayList<MyCardInfo> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.my_card_add_card_list_layout, null);
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
				
				tmpPImage.setOnClickListener(null);
				
				return v;
			}
			return super.getView(position, convertView, parent);
		}

		
	}
	
}