package kr.ac.hansung;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;

public class OptionViewActivity extends Activity {
	private Button csvBtn, passBtn;
	private CardDB cdb;

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

		File exportDir = new File(Environment.getDataDirectory(), "");
		Toast.makeText(OptionViewActivity.this, exportDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();

		File file = new File(exportDir, "excerDB.csv");

		try {

			file.createNewFile();

			CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

			SQLiteDatabase db = cdb.getReadableDatabase();

			Cursor curCSV = db.rawQuery("SELECT * FROM breakdowstats", null);

			csvWrite.writeNext(curCSV.getColumnNames());
	
			while (curCSV.moveToNext()) {

				String arrStr[] = { String.valueOf(curCSV.getInt(0)), curCSV.getString(1),
						String.valueOf(curCSV.getInt(2)), String.valueOf(curCSV.getInt(3)), 
						String.valueOf(curCSV.getInt(4)),String.valueOf(curCSV.getInt(5)),
						String.valueOf(curCSV.getInt(6)), curCSV.getString(7)};

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
