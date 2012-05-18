package kr.ac.hansung;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * MyCardActivity.java ����ī�� Activity
 * @author Junu Kim
 */
public class MyCardActivity extends ListActivity {
	private final static int GO_EDIT_CARD_OPTION = 1;
	private final static int MY_CARD_ADD = 2;
	private final static int EMPTY_INPUT_VALUE = 10;
	
	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;
	MyCardAdapter mAdapter;
	
	ListView cardListView;
	TextView clickedTextView;
	
	TextView addCardPaymentDay;
	TextView addCardCardType;
	EditText addCardName;
	EditText addCardNumber;
	EditText addCardTargetPrice;
	
	int longClickedPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card);
		
		ArrayList<MyCardInfo> myCardList = new ArrayList<MyCardInfo>();

		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();

		String myCardQuery = "SELECT * FROM myCard;";
		c = db.rawQuery(myCardQuery, null);

		while (c.moveToNext()) {
			int cardPrimaryKey = c.getInt(c.getColumnIndex("myCardKey"));
			String cardName = c.getString(c.getColumnIndex("cardName"));
			String cardNumber = c.getString(c.getColumnIndex("cardNumber"));
			int paymentDay = c.getInt(c.getColumnIndex("paymentDay"));
			int tAmount = c.getInt(c.getColumnIndex("tAmount"));
			String cardType = c.getString(c.getColumnIndex("cardType"));
			
			MyCardInfo tmpCardInfo = new MyCardInfo(cardPrimaryKey, cardName, cardNumber, paymentDay, tAmount, cardType);
			tmpCardInfo.setCardImage(setAutoCardImage(cardName));

			myCardList.add(tmpCardInfo);
		}
		db.close();

		mAdapter = new MyCardAdapter(this, R.layout.my_card_list_layout, myCardList);
		setListAdapter(mAdapter);
		
		cardListView = this.getListView();
		cardListView.setOnItemLongClickListener(new myCardListItemLongClickListener());
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		if (requestCode == GO_EDIT_CARD_OPTION) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bdl = data.getBundleExtra("sendBdl");
				
				mAdapter.getItem(longClickedPosition).setPaymentDay(bdl.getInt("paymentDay"));
				mAdapter.getItem(longClickedPosition).setCardType(bdl.getString("cardType"));
				mAdapter.getItem(longClickedPosition).setTAmount(bdl.getInt("tAmount"));
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * myCardListItemLongClickListener ����ī�� ����Ʈ�� �׸��� Long Click�������� Event Handler
	 * @author Junu Kim
	 */
	public class myCardListItemLongClickListener implements AdapterView.OnItemLongClickListener {
		public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
			String[] items = { getResources().getString(R.string.edit_my_card), getResources().getString(R.string.delete_my_card) };
			final int position = pos;
			
			new AlertDialog.Builder(MyCardActivity.this)
			.setTitle(R.string.detete_my_card_dlg_title)
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0 :
						Bundle containBdl = new Bundle();
						
						containBdl.putInt("cardPrimaryKey", mAdapter.getItem(position).getCardPrimaryKey());
						containBdl.putInt("imageRsc", mAdapter.getItem(position).getCardImage());
						containBdl.putString("cardName", mAdapter.getItem(position).getCardName());
						containBdl.putString("cardNumber", mAdapter.getItem(position).getCardNumber());
						containBdl.putString("cardType", mAdapter.getItem(position).getCardType());
						containBdl.putInt("paymentDay", mAdapter.getItem(position).getPaymentDay());
						containBdl.putInt("tAmount", mAdapter.getItem(position).getTAmount());
						
						longClickedPosition = position;
						
						Intent containItent = new Intent(MyCardActivity.this, CardInfoEditActivity.class);
						containItent.putExtras(containBdl);
						
						startActivityForResult(containItent, GO_EDIT_CARD_OPTION);
						break;
					
					case 1 :
						new AlertDialog.Builder(MyCardActivity.this)
						.setTitle("ī�� ���� �ϱ�")
						.setMessage("ī�带 �����ϸ� �ش� ī���� �󼼳����� �Բ� ���� �˴ϴ�. �׷��� ���� �Ͻðڽ��ϱ�?")
						.setPositiveButton("����", null)
						.setNegativeButton("���", null)
						.show();
						break;
					}
				}
			})
			.show();
			
			
			
			return true;
		}
	}
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent clickedCardDetail = new Intent(MyCardActivity.this, DetailViewActivity.class);
		
		clickedCardDetail.putExtra("cardName", mAdapter.getItem(position).getCardName());
		clickedCardDetail.putExtra("cardNumber", mAdapter.getItem(position).getCardNumber());
		
		startActivity(clickedCardDetail);
	}

	
	/**
	 * Method callCardCompany ACTION_DIAL intent�� �������ִ� �޼ҵ�
	 * @param v CardName�� �ִ� View
	 */
	public void callCardCompany(TextView v) {
		TextView tmpTv = v;
		Resources tmpRes = this.getResources();
		Intent callCompany;

		if (tmpTv.getText().equals(tmpRes.getString(R.string.NH_card))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_NH)));
			startActivity(callCompany);
		} else if (tmpTv.getText().equals(tmpRes.getString(R.string.KB_check)) || tmpTv.getText().equals(tmpRes.getString(R.string.KB_credit))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_KB)));
			startActivity(callCompany);
		} else {
			// Other Card Phone Number Add
		}
	}
	
	/**
	 * Method setAutoCardImage ����ī�忡 �߰��� ī���� �̹����� �ڵ����� ã�Ƽ� �̹������ҽ�ID�� �����ϴ� �޼ҵ�
	 * @param cardName
	 * @return int Image Resource ID
	 */
	public int setAutoCardImage(String cardName) {
		Resources autoCardRsc = this.getResources();
		
		if (cardName.equals(autoCardRsc.getString(R.string.NH_card))) {
			return (int)(R.drawable.nh_chaum);
		} else if (cardName.equals(autoCardRsc.getString(R.string.KB_check))
				|| cardName.equals(autoCardRsc.getString(R.string.KB_credit))) {
			return (int)(R.drawable.kb_star);
		}
		
		return (int)(R.drawable.questionmark_card);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MY_CARD_ADD, 0, R.string.add_my_card);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MY_CARD_ADD :
			LayoutInflater cardAddLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			View cardAddView = cardAddLayoutInflater.inflate(R.layout.my_card_add_dlg_layout, (ViewGroup) findViewById(R.id.my_card_add_root_view));
			
			addCardName = (EditText) cardAddView.findViewById(R.id.my_card_add_card_name);
			addCardNumber = (EditText) cardAddView.findViewById(R.id.my_card_add_card_number);
			addCardPaymentDay = (TextView) cardAddView.findViewById(R.id.my_card_add_payment_day);
			addCardCardType = (TextView) cardAddView.findViewById(R.id.my_card_add_card_type);
			addCardTargetPrice = (EditText) cardAddView.findViewById(R.id.my_card_add_target_price);
			
			AddMyCardDlgListener addCardListener = new AddMyCardDlgListener();

			addCardPaymentDay.setOnClickListener(addCardListener);
			
			addCardCardType.setOnClickListener(addCardListener);
			
			AddMyCardListener addMyCardListener = new AddMyCardListener();
			
			new AlertDialog.Builder(MyCardActivity.this)
			.setView(cardAddView)
			.setTitle(R.string.add_my_card)
			.setPositiveButton(R.string.add_string, addMyCardListener) 
			.setNegativeButton(R.string.cancel_string, addMyCardListener)
			.show();
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * addMyCardListner ī�� �����߰����� �߰���ư�� ���������� Event Handler
	 * @author Junu Kim
	 */
	public class AddMyCardListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE :
				String cardName = addCardName.getText().toString();
				String cardNumber = addCardNumber.getText().toString();
				String cardPaymentDay = addCardPaymentDay.getText().toString();;
				String cardType = addCardCardType.getText().toString();
				String cardTargetPrice = addCardTargetPrice.getText().toString();;
				
				if (cardName.equals("") || cardNumber.equals("")) {
					Toast.makeText(MyCardActivity.this, R.string.alert_message, Toast.LENGTH_LONG).show();
				}
				
				
//				db.execSQL("CREATE TABLE myCard (myCardKey INTEGER PRIMARY KEY, cardName TEXT, cardNumber TEXT, paymentDay INTEGER, tAmount INTEGER, cardType TEXT);");
				
				break;
			case DialogInterface.BUTTON_NEGATIVE :
				
				break;
			}
		}
		
	}
	
	
	/**
	 * AddMyCardDlgListener ī�� ���� �߰����� ������, ī������ ���� Ŭ�� ������
	 * @author Junu Kim
	 */
	public class AddMyCardDlgListener implements View.OnClickListener {
		String[] dayList = getResources().getStringArray(R.array.day_list);
		String[] cardTypeList = getResources().getStringArray(R.array.card_type_list);
		String paymentDay = addCardPaymentDay.getText().toString().replace("�ſ� ", "");
		String cardType = addCardCardType.getText().toString();
		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.my_card_add_payment_day :
				new AlertDialog.Builder(MyCardActivity.this)
				.setTitle(R.string.my_card_add_payment_day_title)
				.setSingleChoiceItems(dayList, autoCheckDay(paymentDay), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						addCardPaymentDay.setText(getResources().getString(R.string.pDay_every_month) + " " + dayList[which]);
						paymentDay = dayList[which];
						dialog.dismiss();
					}
				})
				.show();
				
				break;
			case R.id.my_card_add_card_type :
				new AlertDialog.Builder(MyCardActivity.this)
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
	
	/**
	 * MyCardAdapter ListView�� ����Ʈ�� ������ �������ִ� Adapter 
	 * @author Junu Kim
	 */
	public class MyCardAdapter extends ArrayAdapter<MyCardInfo> {

		private ArrayList<MyCardInfo> items;

		public MyCardAdapter(Context context, int textViewResourceId,
				ArrayList<MyCardInfo> objects) {
			super(context, textViewResourceId, objects);
			this.items = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.my_card_list_layout, null);
			}
			
			MyCardInfo m = items.get(position);
			
			if (m != null) {
				ImageView tmpCImage = (ImageView) v.findViewById(R.id.card_imageview);
				ImageView tmpPImage = (ImageView) v.findViewById(R.id.card_phone_imageview);
				final TextView tmpCName = (TextView) v.findViewById(R.id.card_name_textview);
				TextView tmpCNum = (TextView) v.findViewById(R.id.card_num_textview);

				tmpCImage.setImageResource(m.getCardImage());
				tmpPImage.setImageResource(m.getPhoneImage());
				tmpCName.setText(m.getCardName());
				tmpCNum.setText(m.getCardNumber());
				
				tmpPImage.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						callCardCompany(tmpCName);
					}
					
				});
				
			}
			return v;
		}
	}

}
