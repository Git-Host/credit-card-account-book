package kr.ac.hansung;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardDB extends SQLiteOpenHelper {
	public CardDB(Context context) {
		super(context, "card.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		db.execSQL("CREATE TABLE card ( name TEXT PRIMARY KEY ,pDay INTEGER, tAmount INTEGER,CardType INTGER,CardNum TEXT);");
		db.execSQL("CREATE TABLE breakdowstats ( num INTEGER PRIMARY KEY ,cardName TEXT ,pYear INTEGER ,pMonth INTGER ,pDay INTGER ,pPlace TEXT"
				+ ",price INTGER, category TEXT);");

		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
