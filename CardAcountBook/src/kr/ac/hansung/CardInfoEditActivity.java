package kr.ac.hansung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * CardInfoEditActivity
 * 카드정보수정 Activity
 * @author Junu Kim
 */
public class CardInfoEditActivity extends Activity implements CardList {
	SQLiteDatabase db;
	CardDB Cdb;

	MyCardInfo myCardObj;
	
	private Intent parentIntent;
	
	private TextView menuEditImage;
	private TextView menuEditPayment;
	private TextView menuEditPaymentDayShow;
	private TextView menuEditCardType;
	private TextView menuEditCardTypeShow;
	
	private TextView menuEditTAmount;
	private TextView menuEditTAmountShow;
	
	private ImageView titleCardImage; 
	private TextView titleCardName;
	private TextView titleCardNumber;

	private String[] dayList;
	private String[] cardTypeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.card_info_edit);
		super.onCreate(savedInstanceState);
		MenuClickListener menuClickListener=  new MenuClickListener();
		parentIntent = this.getIntent();
		
		Cdb = new CardDB(this);
		
		dayList = getResources().getStringArray(R.array.day_list);
		cardTypeList = getResources().getStringArray(R.array.card_type_list);
		
		Bundle parentBdl = new Bundle();
		parentBdl = parentIntent.getExtras();
		
		myCardObj = new MyCardInfo();
		myCardObj.setCardPrimaryKey(parentBdl.getInt("cardPrimaryKey"));
		myCardObj.setCardImage(parentBdl.getInt("imageRsc"));
		myCardObj.setCardName(parentBdl.getString("cardName"));
		myCardObj.setCardNumber(parentBdl.getString("cardNumber"));
		myCardObj.setCardType(parentBdl.getString("cardType"));
		myCardObj.setPaymentDay(parentBdl.getInt("paymentDay"));
		myCardObj.setTAmount(parentBdl.getInt("tAmount"));
		
		titleCardImage = (ImageView) findViewById(R.id.edit_card_image);
		titleCardName = (TextView) findViewById(R.id.edit_card_title);
		titleCardNumber = (TextView) findViewById(R.id.edit_card_number);
		
		menuEditImage = (TextView) findViewById(R.id.edit_image);
		
		menuEditPayment = (TextView) findViewById(R.id.edit_payment_day);
		menuEditPaymentDayShow = (TextView) findViewById(R.id.edit_payment_day_show);
		
		menuEditCardType = (TextView) findViewById(R.id.edit_card_type);
		menuEditCardTypeShow = (TextView) findViewById(R.id.edit_card_type_show);

		menuEditTAmount = (TextView) findViewById(R.id.edit_t_amount);
		menuEditTAmountShow = (TextView) findViewById(R.id.edit_t_amount_show);
		
		if (myCardObj.getPaymentDay() != 0) {
			menuEditPaymentDayShow.setText(getResources().getString(R.string.pDay_every_month) + " "+ myCardObj.getPaymentDay() + getResources().getString(R.string.pDay_day));
		}
		
		if (!myCardObj.getCardType().equals("")) {
			menuEditCardTypeShow.setText(myCardObj.getCardType());
		}
		
		if (myCardObj.getTAmount() != 0) {
			menuEditTAmountShow.setText(SmsInfo.decimalPointToString(myCardObj.getTAmount()));
		}
		
		menuEditImage.setOnClickListener(menuClickListener);
		menuEditPayment.setOnClickListener(menuClickListener);
		menuEditCardType.setOnClickListener(menuClickListener);
		menuEditTAmount.setOnClickListener(menuClickListener);
		
		titleCardImage.setImageResource(myCardObj.getCardImage());
		titleCardName.setText(myCardObj.getCardName());
		titleCardNumber.setText(myCardObj.getCardNumber());
	}

	/**
	 * MenuClickListener
	 * 카드정보수정 Event Handler
	 * @author Junu Kim
	 */
	public class MenuClickListener implements View.OnClickListener {
		EditText targetPriceEdit;
		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.edit_image :
//				new AlertDialog.Builder(CardInfoEditActivity.this)
//				.setTitle(R.string.edit_image)
//				.setAdapter(adapter, new DialogInterface.OnClickListener() {
//					
//					public void onClick(DialogInterface dialog, int which) {
//						
//					}
//				}).create().show();
//				.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
//					
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						
//					}
//				});
				break;
				
			case R.id.edit_payment_day :
				new AlertDialog.Builder(CardInfoEditActivity.this)
				.setTitle(R.string.edit_payment_day)
				.setSingleChoiceItems(dayList, cardInfoAutoDay(), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						db = Cdb.getReadableDatabase();
						menuEditPaymentDayShow.setText(getResources().getString(R.string.pDay_every_month) + " " + dayList[which]);
						
						int tmpPaymentDay = Integer.parseInt(dayList[which].replace("일", ""));
						String pDayUpdateQuery = "UPDATE myCard SET paymentDay = " + tmpPaymentDay + " WHERE myCardKey = " + myCardObj.getCardPrimaryKey() + ";";
						
						myCardObj.setPaymentDay(tmpPaymentDay);
						
						db.execSQL(pDayUpdateQuery);
						db.close();
						dialog.dismiss();
					}
				})
				.show();

				break;
				
			case R.id.edit_card_type :
				new AlertDialog.Builder(CardInfoEditActivity.this)
				.setTitle(R.string.edit_card_type)
				.setSingleChoiceItems(cardTypeList, cardAutoType(), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						db = Cdb.getReadableDatabase();
						String tmpCardType = cardTypeList[which];
						
						menuEditCardTypeShow.setText(tmpCardType);
						String cardTypeUpdateQuery = "UPDATE myCard SET cardType = '" + tmpCardType + "' WHERE myCardKey = " + myCardObj.getCardPrimaryKey() + ";";
						
						myCardObj.setCardType(tmpCardType);
						
						db.execSQL(cardTypeUpdateQuery);
						db.close();
						
						dialog.dismiss();
					}
				})
				.show();
				break;
				
			case R.id.edit_t_amount :
				LayoutInflater tAmountInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
				View tmpView = tAmountInflater.inflate(R.layout.my_card_edit_t_amount_layout, (ViewGroup) findViewById(R.id.target_price_edit_view));

				targetPriceEdit = (EditText) tmpView.findViewById(R.id.target_price_edit_view);
				
				new AlertDialog.Builder(CardInfoEditActivity.this)
				.setTitle(R.string.edit_t_amount)
				.setView(tmpView)
				.setPositiveButton(R.string.register_string, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						String targetPrice = targetPriceEdit.getText().toString();
						if (targetPrice.equals("")) {
							dialog.dismiss();
						} else {
							
							menuEditTAmountShow.setText(SmsInfo.decimalPointToString(Integer.parseInt(targetPrice)));
							
							db = Cdb.getReadableDatabase();
							
							String tAmountUpdateQuery = "UPDATE myCard SET tAmount = '" + targetPrice + "' WHERE myCardKey = " + myCardObj.getCardPrimaryKey() + ";";
							
							myCardObj.setTAmount(Integer.parseInt(targetPrice));
							
							db.execSQL(tAmountUpdateQuery);
							db.close();
							
							dialog.dismiss();
						}
					}
				})
				.show();
				
				break;
			}
		}
	}

	/**
	 * Method cardInfoAutoDay 결제일 등록하기에서 자동으로 기존 결제일 선택해주는 메소드
	 * @return int dayList에서의 index
	 */
	public int cardInfoAutoDay() {
		String tmpDayShow = menuEditPaymentDayShow.getText().toString();
		int index = -1;
		
		if (tmpDayShow.equals("")) {
			return index;
		} else {
			for (int i=0; i<dayList.length; i++) {
				if (dayList[i].equals(tmpDayShow)) {
					return i;
				} 
			}
		}
		return index;
	}
	
	/**
	 * Method cardAutoType 카드종류 등록하기에서 자동으로 기존 종류를  선택해주는 메소드
	 * @return int cardTypeList에서의 index
	 */
	public int cardAutoType() {
		String tmpCardType = menuEditCardTypeShow.getText().toString();
		if (tmpCardType.equals("")) {
			return -1;
		} else {
			for (int i=0; i<cardTypeList.length; i++) {
				if (cardTypeList[i].equals(tmpCardType)) {
					return i;
				}
			}
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		Intent intent = new Intent();
		Bundle bdl = new Bundle();
		
		bdl.putInt("cardPrimaryKey", myCardObj.getCardPrimaryKey());
		bdl.putInt("imageRsc", myCardObj.getCardImage());
		bdl.putString("cardName", myCardObj.getCardName());
		bdl.putString("cardNumber", myCardObj.getCardNumber());
		bdl.putString("cardType", myCardObj.getCardType());
		bdl.putInt("paymentDay", myCardObj.getPaymentDay());
		bdl.putInt("tAmount", myCardObj.getTAmount());
		
		intent.putExtra("sendBdl", bdl);
		setResult(Activity.RESULT_OK, intent);
		super.finish();
	}
}
