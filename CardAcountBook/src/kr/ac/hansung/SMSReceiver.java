package kr.ac.hansung;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;

// 일단 SMS가 수신되면 메세지내용이 출력되는 리시버
/**
 * SMSReceiver.java
 * 결제SMS수신시 동작하는 BroadcastReceiver
 * @author Junu Kim
 */
public class SMSReceiver extends BroadcastReceiver {
	private NotificationManager smsNotificationManager;
	private Notification smsNotification;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String SmsAddress = "";
		String SmsBody = "";
		Resources res = context.getResources();
		
		// Notification Manager
		smsNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		smsNotification = new Notification();
		smsNotification.icon = R.drawable.ic_launcher;
		smsNotification.tickerText = res.getString(R.string.notification_ticker_text);
		smsNotification.when = System.currentTimeMillis();
		smsNotification.number = 0;
		smsNotification.flags = Notification.FLAG_AUTO_CANCEL;
		
		Intent itnt = new Intent(context.getApplicationContext(), DetailViewActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, itnt, 0);
		
		smsNotification.setLatestEventInfo(context.getApplicationContext(), res.getString(R.string.notification_title), res.getString(R.string.notification_text), pi);
		
		
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				SmsAddress = msgs[i].getOriginatingAddress();
				SmsBody = msgs[i].getMessageBody().toString();
			}
		}
		
		if (SmsAddress.equals(res.getString(R.string.phoneNum_KB)) || SmsAddress.equals(res.getString(R.string.phoneNum_NH))) {
			SQLiteDatabase db;
			CardDB Cdb = new CardDB(context);
			db = Cdb.getReadableDatabase();
			db.execSQL(SmsInfo.scatterMessage(SmsAddress, SmsBody));
			db.close();
			smsNotificationManager.notify(0, smsNotification);
		} 
	}
}
