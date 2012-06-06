package kr.ac.hansung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Admin
 *
 */
/**
 * @author Admin
 * 
 */
public class OptionViewActivity extends ListActivity {
	private static final int OPTION_PASSWORD_SET = 0;
	private static final int OPTION_CSV_OUT = 1;

	private CardDB cdb;
	public static final String CSV_COLNAMES = "날짜,카드명,카드번호,사용내역,사용금액,카테고리\n";
	public static final String ROOT_DIR = "/mnt/sdcard/";

	private ArrayList<OptionObj> optionList;
	private OptionListAdapter optionListAdapter;

	LayoutInflater inflater;

	private class OptionObj {
		String optionTitle;
		String optionDescription;

		public OptionObj(String optionTitle, String optionDescription) {
			this.optionTitle = optionTitle;
			this.optionDescription = optionDescription;
		}

		public void setOptionTitle(String optionTitle) {
			this.optionTitle = optionTitle;
		}

		public void setOptionDescription(String optionDescription) {
			this.optionDescription = optionDescription;
		}

		public String getOptionTitle() {
			return optionTitle;
		}

		public String getOptionDescription() {
			return optionDescription;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_view);
		
		optionList = new ArrayList<OptionObj>();

		OptionObj passwordSet = new OptionObj(getResources().getString(
				R.string.option_password_title), getResources().getString(
				R.string.option_password_description));
		OptionObj csvOutput = new OptionObj(getResources().getString(
				R.string.option_csv_title), getResources().getString(
				R.string.option_csv_description));

		optionList.add(passwordSet);
		optionList.add(csvOutput);

		optionListAdapter = new OptionListAdapter(this,
				R.layout.option_view_list_layout, optionList);
		setListAdapter(optionListAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		switch (position) {
		case OPTION_PASSWORD_SET:
			showDialog(1);
			break;
		case OPTION_CSV_OUT:
			ExportCSV();
			break;
		}

		super.onListItemClick(l, v, position, id);
	}

	public class OptionListAdapter extends ArrayAdapter<OptionObj> {
		private ArrayList<OptionObj> items;

		public OptionListAdapter(Context context, int textViewResourceId,
				ArrayList<OptionObj> objects) {
			super(context, textViewResourceId, objects);
			items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View optionView = convertView;

			if (optionView == null) {
				LayoutInflater optionLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				optionView = optionLayoutInflater.inflate(
						R.layout.option_view_list_layout, null);
			}

			OptionObj tmpObj = items.get(position);

			if (tmpObj != null) {
				TextView tmpOptionTitle = (TextView) optionView
						.findViewById(R.id.option_title);
				TextView tmpOptionDescription = (TextView) optionView
						.findViewById(R.id.option_description);

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
			Toast.makeText(OptionViewActivity.this, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
		}

		// File객체로부터 Uri값 생성
		final Uri fileUri = Uri.fromFile(f);
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("plain/text");

		// 수신인 주소 - tos배열의 값을 늘릴 경우 다수의 수신자에게 발송됨
		String[] tos = { acct.name };
		it.putExtra(Intent.EXTRA_EMAIL, tos);

		it.putExtra(Intent.EXTRA_SUBJECT, "살아있네!!");
		it.putExtra(Intent.EXTRA_TEXT, "상세 내역 csv 파일입니다.");

		// 파일첨부
		it.putExtra(Intent.EXTRA_STREAM, fileUri);

		startActivity(it);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final View linear = (View) inflater.inflate(
				R.layout.password_dialog_layout, null);
		final EditText password = (EditText) linear.findViewById(R.id.Pass);
		final EditText confirmPass = (EditText) linear
				.findViewById(R.id.commitPass);
		password.setSingleLine();
		confirmPass.setSingleLine();

		int maxLength = 4;
		InputFilter[] filterArray = new InputFilter[1];
		filterArray[0] = new InputFilter.LengthFilter(maxLength);
		password.setFilters(filterArray);
		confirmPass.setFilters(filterArray);
		
		password.setInputType(InputType.TYPE_CLASS_NUMBER);
		password.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		confirmPass.setInputType(InputType.TYPE_CLASS_NUMBER);
		confirmPass.setTransformationMethod(PasswordTransformationMethod
				.getInstance());

		return new AlertDialog.Builder(OptionViewActivity.this)
				.setTitle("비밀번호")
				.setIcon(R.drawable.ic_launcher)
				.setView(linear)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String Pass;
						String Confirm;
						

						Pass = password.getText().toString();
						Confirm = confirmPass.getText().toString();
						if (Pass.equals(Confirm)&&(!Pass.isEmpty())) {

							SharedPreferences pref = getSharedPreferences(
									"Pref", Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = pref.edit();
														
							editor.putBoolean("setPass", true);
							editor.putString("password", Pass);
							
							editor.commit();

						} else
							Toast.makeText(OptionViewActivity.this,
									"비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
						password.setText("");
						confirmPass.setText("");
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						password.setText("");
						confirmPass.setText("");
					}
				}).create();

	}

}
