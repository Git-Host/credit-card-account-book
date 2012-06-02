package kr.ac.hansung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OptionViewActivity extends ListActivity {
	private static final int OPTION_PASSWORD_SET = 0;
	private static final int OPTION_CSV_OUT = 1;
	
	private CardDB cdb;
	public static final String CSV_COLNAMES = "��¥,ī���,ī���ȣ,��볻��,���ݾ�,ī�װ�\n";
	public static final String ROOT_DIR = "/mnt/sdcard/";
	
	private ArrayList<OptionObj> optionList;
	private OptionListAdapter optionListAdapter;
	
	private class OptionObj {
		String optionTitle;
		String optionDescription;

		public OptionObj(String optionTitle, String optionDescription) {
			this.optionTitle = optionTitle;
			this.optionDescription = optionDescription;
		}

		public void setOptionTitle(String optionTitle) { this.optionTitle = optionTitle; }
		public void setOptionDescription(String optionDescription) { this.optionDescription = optionDescription; }

		public String getOptionTitle() { return optionTitle; }
		public String getOptionDescription() { return optionDescription; }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_view);

		optionList = new ArrayList<OptionObj>();
		
		OptionObj passwordSet = new OptionObj(getResources().getString(R.string.option_password_title), getResources().getString(R.string.option_password_description));
		OptionObj csvOutput = new OptionObj(getResources().getString(R.string.option_csv_title), getResources().getString(R.string.option_csv_description));
		
		optionList.add(passwordSet);
		optionList.add(csvOutput);
		
		optionListAdapter = new OptionListAdapter(this, R.layout.option_view_list_layout, optionList);
		setListAdapter(optionListAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case OPTION_PASSWORD_SET :
			break;
		case OPTION_CSV_OUT :
			ExportCSV();
			break;
		}
		
		super.onListItemClick(l, v, position, id);
	}
	
	public class OptionListAdapter extends ArrayAdapter<OptionObj> {
		private ArrayList<OptionObj> items;

		public OptionListAdapter(Context context, int textViewResourceId, ArrayList<OptionObj> objects) {
			super(context, textViewResourceId, objects);
			items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View optionView = convertView;

			if (optionView == null) {
				LayoutInflater optionLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				optionView = optionLayoutInflater.inflate(R.layout.option_view_list_layout, null);
			}

			OptionObj tmpObj = items.get(position);

			if (tmpObj != null) {
				TextView tmpOptionTitle = (TextView) optionView.findViewById(R.id.option_title);
				TextView tmpOptionDescription = (TextView) optionView.findViewById(R.id.option_description);

				tmpOptionTitle.setText(tmpObj.getOptionTitle());
				tmpOptionDescription.setText(tmpObj.getOptionDescription());
			}
			
			return optionView;
		}
	}

	

	protected void ExportCSV() {

		try {
			cdb = new CardDB(getApplicationContext());
			SQLiteDatabase db = cdb.getReadableDatabase();

			Cursor curCSV = db.rawQuery("SELECT * FROM breakdowstats WHERE deleteFlag = 0", null);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ROOT_DIR + "/excerDB.csv"), "MS949"));
			writer.write(CSV_COLNAMES);

			while (curCSV.moveToNext()) {

				Date date = new Date();
				date.setYear(curCSV.getInt(2) - 1900);
				date.setMonth(curCSV.getInt(3) - 1);
				date.setDate(curCSV.getInt(4));

				String strDate = android.text.format.DateFormat.format("yyyy-MM-dd", date).toString();

				String arrStr = strDate + "," + curCSV.getString(1) + ","
						+ curCSV.getString(8) + "," + curCSV.getString(5) + ","
						+ String.valueOf(curCSV.getInt(6)) + ","
						+ curCSV.getString(7) + "\n";

				writer.write(arrStr);

			}
			db.close();
			
			curCSV.close();

			writer.close();

		} catch (SQLException sqlEx) {

			Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

		} catch (IOException e) {

			Log.e("MainActivity", e.getMessage(), e);

		}
		AccountManager mgr = AccountManager.get(getApplicationContext());
		Account[] accts = mgr.getAccounts();
		Account acct = accts[0];

		String szSendFilePath = ROOT_DIR + "/excerDB.csv";
		File f = new File(szSendFilePath);
		if (!f.exists()) {
			Toast.makeText(OptionViewActivity.this, "������ �����ϴ�.", Toast.LENGTH_SHORT).show();
		}

		// File��ü�κ��� Uri�� ����
		final Uri fileUri = Uri.fromFile(f);
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("plain/text");

		// ������ �ּ� - tos�迭�� ���� �ø� ��� �ټ��� �����ڿ��� �߼۵�
		String[] tos = { acct.name };
		it.putExtra(Intent.EXTRA_EMAIL, tos);

		it.putExtra(Intent.EXTRA_SUBJECT, "����ֳ�!!");
		it.putExtra(Intent.EXTRA_TEXT, "�� ���� csv �����Դϴ�.");

		// ����÷��
		it.putExtra(Intent.EXTRA_STREAM, fileUri);

		startActivity(it);
	}
}
