package kr.ac.hansung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.zip.Inflater;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Admin
 * 
 */
public class OptionViewActivity extends Activity {
	private Button csvBtn, passBtn;
	private CardDB cdb;
	public static final String CSV_COLNAMES = "날짜,카드명,카드번호,사용내역,사용금액,카테고리\n";
	public static final String ROOT_DIR = "/mnt/sdcard/";
	LayoutInflater inflater;
	TextView text;

	final static int DIALOG_1 = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option_view);

		csvBtn = (Button) findViewById(R.id.csv);
		passBtn = (Button) findViewById(R.id.password);

		MyOnClickListener l = new MyOnClickListener();

		csvBtn.setOnClickListener(l);
		passBtn.setOnClickListener(l);
		text = (TextView) findViewById(R.id.text);
	}

	class MyOnClickListener implements OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.csv:
				ExportCSV();
				break;
			case R.id.password:

				break;
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(id, args);
	}

	protected void ExportCSV() {

		try {

			cdb = new CardDB(this);
			SQLiteDatabase db = cdb.getReadableDatabase();

			Cursor curCSV = db.rawQuery("SELECT * FROM breakdowstats", null);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(ROOT_DIR + "/excerDB.csv"), "MS949"));
			writer.write(CSV_COLNAMES);

			while (curCSV.moveToNext()) {

				Date date = new Date();
				date.setYear(curCSV.getInt(2) - 1900);
				date.setMonth(curCSV.getInt(3) + 1);
				date.setDate(curCSV.getInt(4));

				String strDate = android.text.format.DateFormat.format(
						"yyyy-MM-dd", date).toString();

				String arrStr = strDate + "," + curCSV.getString(1) + ","
						+ curCSV.getString(8) + "," + curCSV.getString(5) + ","
						+ String.valueOf(curCSV.getInt(6)) + ","
						+ curCSV.getString(7) + "\n";

				writer.write(arrStr);

			}

			curCSV.close();

			writer.close();

		} catch (SQLException sqlEx) {

			Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

		} catch (IOException e) {

			Log.e("MainActivity", e.getMessage(), e);

		}
		AccountManager mgr = AccountManager.get(this);
		Account[] accts = mgr.getAccounts();
		Account acct = accts[0];

		String szSendFilePath = ROOT_DIR + "/excerDB.csv";
		File f = new File(szSendFilePath);
		if (!f.exists()) {
			Toast.makeText(this, "파일이 없습니다.", Toast.LENGTH_SHORT).show();
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

}
