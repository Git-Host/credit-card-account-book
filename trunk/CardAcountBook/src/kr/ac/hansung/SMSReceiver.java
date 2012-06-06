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
import android.util.Log;

// 일단 SMS가 수신되면 메세지내용이 출력되는 리시버
/**
 * SMSReceiver.java
 * 결제SMS수신시 동작하는 BroadcastReceiver
 * @author Junu Kim
 */
public class SMSReceiver extends BroadcastReceiver {
	private SmsInfo smsInfoClass;
	private String receivedQuery;
	
	private NotificationManager smsNotificationManager;
	private Notification smsNotification;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String smsAddress = "";
		String smsBody = "";
		smsInfoClass = new SmsInfo(context);
		Resources res = context.getResources();
		
		// Notification Manager
		smsNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		smsNotification = new Notification();
		smsNotification.icon = R.drawable.status_bar_icon_i_card;
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
				smsAddress = msgs[i].getOriginatingAddress();
				smsBody = smsBody + msgs[i].getMessageBody().toString();
			}
		}
		
		if (smsAddress.equals(res.getString(R.string.phoneNum_KB)) || smsAddress.equals(res.getString(R.string.phoneNum_NH))
			|| smsAddress.equals(res.getString(R.string.phoneNum_CITY)) || smsAddress.equals(res.getString(R.string.phoneNum_KEB))
			|| smsAddress.equals(res.getString(R.string.phoneNum_saving_bank)) || smsAddress.equals(res.getString(R.string.phoneNum_SHINHAN))
			
				|| smsAddress.equals("01042434994"))	{
		
			SQLiteDatabase db;
			CardDB Cdb = new CardDB(context);
			db = Cdb.getReadableDatabase();

			receivedQuery = smsInfoClass.scatterMessage(smsAddress, smsBody);
			
			if (!receivedQuery.equals(res.getString(R.string.sms_form_error))) {
				db.execSQL(receivedQuery);
				smsNotificationManager.notify(0, smsNotification);
			}
			db.close();
		} 
	}
}
