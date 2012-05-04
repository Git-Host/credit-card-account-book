package kr.ac.hansung;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.Date;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class OptionViewActivity extends Activity {
	private Button csvBtn, passBtn;
	private CardDB cdb;
	public static final String CSV_COLNAMES[] = {"날짜,카드명,카드번호,사용내역,사용금액,카테고리"};
	public static final String ENG_CSV_COLNAMES[] = {"DATE,CardName,CardNumber,Place,Price,Category"};
	public static final String ROOT_DIR = "/data/data/kr.ac.hansung/";
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

	protected void ExportCSV() {
		File dbFile = getDatabasePath("Card.db");

		File exportDir = new File(ROOT_DIR, "");
		Toast.makeText(OptionViewActivity.this, ROOT_DIR, Toast.LENGTH_SHORT).show();
		File file = new File(exportDir, "excerDB.csv");
		
		try {

			file.createNewFile();
			 
			CSVWriter csvWrite = new CSVWriter(new FileWriter(file),'\t',' ');
			cdb = new CardDB(this);
			SQLiteDatabase db = cdb.getReadableDatabase();
						
			Cursor curCSV = db.rawQuery("SELECT * FROM breakdowstats", null);
						
			csvWrite.writeNext(ENG_CSV_COLNAMES);
			

			while (curCSV.moveToNext()) {
				
				Date date = new Date();
				date.setYear(curCSV.getInt(2)-1900);
				date.setMonth(curCSV.getInt(3)+1);
				date.setDate(curCSV.getInt(4));
				
				String strDate = android.text.format.DateFormat.format("yyyy-MM-dd",date).toString();
				
				String arrStr[] = {strDate +","+ curCSV.getString(1)+","+
						curCSV.getString(8) +","+ curCSV.getString(5)+","+
						String.valueOf(curCSV.getInt(6))+","+ curCSV.getString(7)};

				csvWrite.writeNext(arrStr);
			}

			csvWrite.close();

			curCSV.close();

		} catch (SQLException sqlEx) {

			Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

		} catch (IOException e) {

			Log.e("MainActivity", e.getMessage(), e);
		}
	}

}
