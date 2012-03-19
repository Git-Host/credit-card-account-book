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

public class DetailViewActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_view);

		ArrayList<SmsInfo> detailViewList = new ArrayList<SmsInfo>();

		// temp infomation
		SmsInfo tmpInfo_1 = new SmsInfo("0102", "NH카드", "똥싼바지", "10000");
		SmsInfo tmpInfo_2 = new SmsInfo("0203", "NH카드", "돈주왕", "20000");
		SmsInfo tmpInfo_3 = new SmsInfo("0303", "NH카드", "영웅분식", "35000");
		SmsInfo tmpInfo_4 = new SmsInfo("0305", "NH카드", "한성서점", "50000");
		SmsInfo tmpInfo_5 = new SmsInfo("0306", "NH카드", "공대매점", "5000");
		SmsInfo tmpInfo_6 = new SmsInfo("0310", "NH카드", "미래관카페", "28000");

		detailViewList.add(tmpInfo_1);
		detailViewList.add(tmpInfo_2);
		detailViewList.add(tmpInfo_3);
		detailViewList.add(tmpInfo_4);
		detailViewList.add(tmpInfo_5);
		detailViewList.add(tmpInfo_6);

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
				tmpPrice.setText(m.getPrice());
			}
			return v;

		}
	}

}
