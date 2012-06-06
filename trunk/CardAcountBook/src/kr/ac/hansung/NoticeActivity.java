package kr.ac.hansung;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NoticeActivity extends ListActivity {
	
	private ArrayList<String> noticeList;
	private NoticeListAdapter noticeListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_view);
		
		noticeList = new ArrayList<String>();

		noticeList.add("이용방법 및 설명");
		noticeList.add("첫번째 공지사항");
		
		noticeListAdapter = new NoticeListAdapter(this, R.layout.notice_view_layout, noticeList);
		setListAdapter(noticeListAdapter);
		
		noticeListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	
		super.onListItemClick(l, v, position, id);
	}

	public class NoticeListAdapter extends ArrayAdapter<String> {
		private ArrayList<String> items;

		public NoticeListAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
			super(context, textViewResourceId, objects);
			items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View noticeView = convertView;

			if (noticeView == null) {
				LayoutInflater optionLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				noticeView = optionLayoutInflater.inflate(R.layout.notice_view_layout, null);
			}

			String tmpString = items.get(position);

			if (tmpString != null) {
				TextView tmpNoticeTitle = (TextView) noticeView.findViewById(R.id.notice_title);
				tmpNoticeTitle.setText(tmpString);
			}

			return noticeView;
		}
	}
}
