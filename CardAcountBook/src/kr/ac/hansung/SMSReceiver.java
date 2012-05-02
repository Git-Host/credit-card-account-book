package kr.ac.hansung;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

// 일단 SMS가 수신되면 메세지내용이 출력되는 리시버
public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String SmsAddress = "";
		String SmsBody = "";
		Resources res = context.getResources();
		
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				SmsAddress = msgs[i].getOriginatingAddress();
				SmsBody = msgs[i].getMessageBody().toString();
			}
		}
		if (SmsAddress.equals(res.getString(R.string.phoneNum_KB))|| SmsAddress.equals(res.getString(R.string.phoneNum_NH))) {
			SQLiteDatabase db;
			CardDB Cdb = new CardDB(context);
			db = Cdb.getReadableDatabase();
			db.execSQL(SmsInfo.scatterMessage(SmsAddress, SmsBody));
			db.close();
		}
	}
}
