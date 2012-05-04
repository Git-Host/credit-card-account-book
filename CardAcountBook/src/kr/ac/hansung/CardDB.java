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
		db.execSQL("CREATE TABLE myCard ( key INTEGER PRIMARY KEY, cardName TEXT, cardNumber TEXT, paymentDay TEXT, tAmount INTEGER, cardType INTEGER);");
		db.execSQL("CREATE TABLE breakdowstats ( num INTEGER PRIMARY KEY ,cardName TEXT , pYear INTEGER ,pMonth INTEGER ,pDay INTEGER ,pPlace TEXT"
				+ ",price INTEGER, category TEXT, cardNumber TEXT, combineDate INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
