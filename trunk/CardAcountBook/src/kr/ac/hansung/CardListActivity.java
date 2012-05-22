package kr.ac.hansung;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CardListActivity extends ListActivity implements CardInfoList {
	private final static int CARD_ADD_DIALOG_SHOW = 0;
	
	private MyCardInfo returnCardObj;
	private MyCardAdapter cardListAdapter;
	
	private TextView addCardPaymentDay;
	private TextView addCardCardType;
	private EditText addCardNumber;
	private EditText addCardTargetPrice;
	private TextView addCardName;
	private ImageView addCardImage;
	
	String tmpCardName;
	String tmpReduceCardName;
	int tmpCardImage;
	
	int dayListWhich = 0; 
	
	String[] dayList;
	String[] cardTypeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.my_card_add_card);

		dayList = getResources().getStringArray(R.array.day_list);
		cardTypeList = getResources().getStringArray(R.array.card_type_list);
		
		ArrayList<MyCardInfo> cardList = new ArrayList<MyCardInfo>();
		MyCardInfo cardElement;
		
		for (int i=0; i<CardInfoList.kbCardImg.length; i++) {
			cardElement = new MyCardInfo();
			cardElement.setCardImage(kbCardImg[i]);
			cardElement.setCardName(getResources().getString(kbCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.KB_card));
			cardList.add(cardElement);
		}
		
		cardListAdapter = new MyCardAdapter(this, R.layout.my_card_add_card_list_layout, cardList);
		setListAdapter(cardListAdapter);
		
//		MyCardInfo card = new MyCardInfo();
//		card.setCardName("asdfasdf");
//		
//		cardList.add(0, card);
		
		super.onCreate(savedInstanceState);
	}

	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog addDialog;
		switch (id) {
		case CARD_ADD_DIALOG_SHOW :
			AlertDialog.Builder addBuilder = new AlertDialog.Builder(this);
			tmpCardName = args.getString("cardName");
			tmpReduceCardName = args.getString("reduceCardName");
			tmpCardImage = args.getInt("cardImage");
			
			LayoutInflater cardAddLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View cardAddView = cardAddLayoutInflater.inflate(R.layout.my_card_add_dlg_layout, (ViewGroup) findViewById(R.id.my_card_add_root_view));
	
			addCardName = (TextView) cardAddView.findViewById(R.id.my_card_add_card_name);
			addCardImage = (ImageView) cardAddView.findViewById(R.id.my_card_add_card_image);
			addCardNumber = (EditText) cardAddView.findViewById(R.id.my_card_add_card_number);
			addCardPaymentDay = (TextView) cardAddView.findViewById(R.id.my_card_add_payment_day);
			addCardCardType = (TextView) cardAddView.findViewById(R.id.my_card_add_card_type);
			addCardTargetPrice = (EditText) cardAddView.findViewById(R.id.my_card_add_target_price);
			
			AddMyCardDlgListener addCardDlgListener = new AddMyCardDlgListener();
			addCardName.setText(tmpCardName);
			addCardImage.setImageResource(tmpCardImage);
			
			addCardPaymentDay.setOnClickListener(addCardDlgListener);
			addCardCardType.setOnClickListener(addCardDlgListener);

			AddMyCardListener addCardListener = new AddMyCardListener();
			
			addBuilder
			.setView(cardAddView)
			.setTitle(R.string.add_my_card)
			.setCancelable(false)
			.setNegativeButton(R.string.cancel_string, addCardListener)
			.setPositiveButton(R.string.add_string, addCardListener);
			
			addDialog = addBuilder.create();
			return addDialog;
		}
		return super.onCreateDialog(id, args);
	}
	
	public class AddMyCardListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE :
				if (tmpReduceCardName.equals(getResources().getString(R.string.KB_card))) {
					int paymentDay = Integer.parseInt(dayList[dayListWhich].replace("일", ""));
					int tAmount = Integer.parseInt(addCardTargetPrice.getText().toString());
					int cardImage = tmpCardImage;
					String cardName = tmpCardName;
					String cardNumber = addCardNumber.getText().toString();
					String cardType = addCardCardType.getText().toString();
					
					if (cardType.equals(cardTypeList[0])) {
						cardName = tmpReduceCardName + getResources().getString(R.string.just_card);
					} else {
						cardName = tmpReduceCardName + cardTypeList[1];
					}
					returnCardObj = new MyCardInfo(cardName, cardNumber, paymentDay, tAmount, cardType, cardImage);
					
					
					SQLiteDatabase db;
					CardDB Cdb = new CardDB(getApplicationContext());
					db = Cdb.getReadableDatabase();
					
					String insertQuery = "INSERT INTO myCard VALUES (null, '" + cardName + "', '" + cardNumber + "', " + paymentDay + ", "
										  + tAmount + ",'" + cardType + "');";
					db.execSQL(insertQuery);
					db.close();
					
					Intent intent = new Intent();
					Bundle bdl = new Bundle();
					
					bdl.putInt("imageRsc", returnCardObj.getCardImage());
					bdl.putString("cardName", returnCardObj.getCardName());
					bdl.putString("cardNumber", returnCardObj.getCardNumber());
					bdl.putString("cardType", returnCardObj.getCardType());
					bdl.putInt("paymentDay", returnCardObj.getPaymentDay());
					bdl.putInt("tAmount", returnCardObj.getTAmount());
					
					intent.putExtra("sendBdl", bdl);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
				
				break;
			case DialogInterface.BUTTON_NEGATIVE :
				
				removeDialog(CARD_ADD_DIALOG_SHOW);
				break;
			}
		}
	}
	
	
	
	
	
	/**
	 * AddMyCardDlgListener 카드 수동 추가에서 결제일, 카드종류 뷰의 클릭 리스너
	 * @author Junu Kim
	 */
	public class AddMyCardDlgListener implements View.OnClickListener {
		String paymentDay = addCardPaymentDay.getText().toString().replace("매월 ", "");
		String cardType = addCardCardType.getText().toString();
		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.my_card_add_payment_day :
				new AlertDialog.Builder(CardListActivity.this)
				.setTitle(R.string.my_card_add_payment_day_title)
				.setSingleChoiceItems(dayList, autoCheckDay(paymentDay), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						addCardPaymentDay.setText(getResources().getString(R.string.pDay_every_month) + " " + dayList[which]);
						paymentDay = dayList[which];
						dayListWhich = which;
						dialog.dismiss();
					}
				})
				.show();
				
				break;
			case R.id.my_card_add_card_type :
				new AlertDialog.Builder(CardListActivity.this)
				.setTitle(R.string.my_card_add_card_type_title)
				.setSingleChoiceItems(cardTypeList, autoCheckCardType(cardType), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						addCardCardType.setText(cardTypeList[which]);
						cardType = cardTypeList[which];
						dialog.dismiss();
					}
				})
				.show();
				
				break;
			}
		}
		
		public int autoCheckDay(String date) {
			for (int i=0; i<dayList.length; i++) {
				if (dayList[i].equals(date)) 
					return i;
			}
			return -1;
		}
		
		public int autoCheckCardType(String cardType) {
			for (int i=0; i<cardTypeList.length; i++) {
				if (cardTypeList[i].equals(cardType)) 
					return i;
			}
			return -1;
		}
	}
	
	
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Bundle bdl = new Bundle();
		bdl.putString("cardName", cardListAdapter.getItem(position).getCardName());
		bdl.putString("reduceCardName", cardListAdapter.getItem(position).getReduceCardName());
		bdl.putInt("cardImage", cardListAdapter.getItem(position).getCardImage());
		showDialog(CARD_ADD_DIALOG_SHOW, bdl);
	}


	public class MyCardAdapter extends ArrayAdapter<MyCardInfo> {
		private ArrayList<MyCardInfo> items;
		
		public MyCardAdapter(Context context, int textViewResourceId, ArrayList<MyCardInfo> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.my_card_add_card_list_layout, null);
			}
			
			MyCardInfo m = items.get(position);
			
			if (m != null) {
				ImageView tmpCImage = (ImageView) v.findViewById(R.id.card_add_image);
				TextView tmpCName = (TextView) v.findViewById(R.id.card_add_text);

				tmpCImage.setImageResource(m.getCardImage());
				tmpCName.setText(m.getCardName());
				return v;
			}
			return super.getView(position, convertView, parent);
		}
	}
}