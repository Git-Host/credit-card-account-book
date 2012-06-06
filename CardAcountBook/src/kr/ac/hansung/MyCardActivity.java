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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * MyCardActivity.java 나의카드 Activity
 * @author Junu Kim
 */
public class MyCardActivity extends ListActivity {
	private final static int GO_EDIT_CARD_OPTION = 1;
	private final static int GO_CARD_LIST_RESULT_OK = 3;
	private final static int MY_CARD_ADD = 2;
	private final static int DELETE_CARD_DIALOG = 4;
	
	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;
	
	MyCardAdapter mAdapter;
	ArrayList<MyCardInfo> myCardList;
	
	ListView cardListView;
	TextView clickedTextView;
	
	int longClickedPosition;
	
	private ImageView addCardImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card);
		
		myCardList = new ArrayList<MyCardInfo>();

		Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();

		String myCardQuery = "SELECT * FROM myCard WHERE deleteFlag = 0;";
		c = db.rawQuery(myCardQuery, null);

		while (c.moveToNext()) {
			int cardPrimaryKey = c.getInt(c.getColumnIndex("myCardKey"));
			String cardName = c.getString(c.getColumnIndex("cardName"));
			String cardNumber = c.getString(c.getColumnIndex("cardNumber"));
			int paymentDay = c.getInt(c.getColumnIndex("paymentDay"));
			int tAmount = c.getInt(c.getColumnIndex("tAmount"));
			String cardType = c.getString(c.getColumnIndex("cardType"));
			String cardImageUri = c.getString(c.getColumnIndex("cardImageUri"));
			
			MyCardInfo tmpCardInfo = new MyCardInfo(cardPrimaryKey, cardName, cardNumber, paymentDay, tAmount, cardType, cardImageUri);

			myCardList.add(tmpCardInfo);
		}
		db.close();

		mAdapter = new MyCardAdapter(this, R.layout.my_card_list_layout, myCardList);
		setListAdapter(mAdapter);
		
		cardListView = this.getListView();
		cardListView.setOnItemLongClickListener(new myCardListItemLongClickListener());
		
		addCardImageView = (ImageView) findViewById(R.id.empty_card_add_view);
		addCardImageView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent goCardListActivity = new Intent(MyCardActivity.this, CardExpandableListActivity.class);
				startActivityForResult(goCardListActivity, GO_CARD_LIST_RESULT_OK);
			}
		});
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode == GO_EDIT_CARD_OPTION) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bdl = data.getBundleExtra("sendBdl");
				mAdapter.getItem(longClickedPosition).setPaymentDay(bdl.getInt("paymentDay"));
				mAdapter.getItem(longClickedPosition).setCardType(bdl.getString("cardType"));
				mAdapter.getItem(longClickedPosition).setTAmount(bdl.getInt("tAmount"));
				
				mAdapter.notifyDataSetChanged();
			}
		} else if (requestCode == GO_CARD_LIST_RESULT_OK) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bdl = data.getBundleExtra("sendBdl");
				MyCardInfo tmpObj = new MyCardInfo(bdl.getInt("myCardKey"), bdl.getString("cardName"), bdl.getString("cardNumber"), bdl.getInt("paymentDay"),
										bdl.getInt("tAmount"), bdl.getString("cardType"), bdl.getString("cardImageUri"));

				mAdapter.add(tmpObj);
				mAdapter.notifyDataSetChanged();
			
			} else if (resultCode == Activity.RESULT_CANCELED) {
				mAdapter.notifyDataSetInvalidated();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * myCardListItemLongClickListener 나의카드 리스트의 항목을 Long Click했을때의 Event Handler
	 * @author Junu Kim
	 */
	public class myCardListItemLongClickListener implements AdapterView.OnItemLongClickListener {
	
		public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
			String[] items = { getResources().getString(R.string.edit_my_card), getResources().getString(R.string.delete_my_card) };
			final int position = pos;
			
			longClickedPosition = position;
			
			new AlertDialog.Builder(MyCardActivity.this)
			.setTitle(R.string.detete_my_card_dlg_title)
			.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Bundle containBdl;
					switch (which) {
					case 0 :
						containBdl = new Bundle();
						
						containBdl.putInt("cardPrimaryKey", mAdapter.getItem(position).getCardPrimaryKey());
						containBdl.putString("cardImageUri", mAdapter.getItem(position).getCardImageUri());
						containBdl.putString("cardName", mAdapter.getItem(position).getCardName());
						containBdl.putString("cardNumber", mAdapter.getItem(position).getCardNumber());
						containBdl.putString("cardType", mAdapter.getItem(position).getCardType());
						containBdl.putInt("paymentDay", mAdapter.getItem(position).getPaymentDay());
						containBdl.putInt("tAmount", mAdapter.getItem(position).getTAmount());
						
						Intent containItent = new Intent(MyCardActivity.this, CardInfoEditActivity.class);
						containItent.putExtras(containBdl);
						
						startActivityForResult(containItent, GO_EDIT_CARD_OPTION);
						break;
					
					case 1 :
						containBdl = new Bundle();
						
						containBdl.putInt("cardPrimaryKey", mAdapter.getItem(position).getCardPrimaryKey());
						containBdl.putString("cardImageUri", mAdapter.getItem(position).getCardImageUri());
						containBdl.putString("cardName", mAdapter.getItem(position).getCardName());
						containBdl.putString("cardNumber", mAdapter.getItem(position).getCardNumber());
						containBdl.putString("cardType", mAdapter.getItem(position).getCardType());
						containBdl.putInt("paymentDay", mAdapter.getItem(position).getPaymentDay());
						containBdl.putInt("tAmount", mAdapter.getItem(position).getTAmount());
						
						showDialog(DELETE_CARD_DIALOG, containBdl);
						
						break;
					}
				}
			})
			.show();
			
			return true;
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		final Bundle tmpBdl = args;
		
		switch (id) {
		case DELETE_CARD_DIALOG :
			return new AlertDialog.Builder(MyCardActivity.this)
			.setTitle(R.string.delete_my_card)
			.setMessage(R.string.delete_my_card_description)
			.setPositiveButton(R.string.delete_string, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					db = Cdb.getWritableDatabase();
					String updateQuery = "UPDATE myCard SET deleteFlag = 1 WHERE myCardKey = " + tmpBdl.getInt("cardPrimaryKey") + ";";
					db.execSQL(updateQuery);
					db.close();

					removeDialog(DELETE_CARD_DIALOG);
					myCardList.remove(longClickedPosition);
					mAdapter.notifyDataSetChanged();
				}
			})
			.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(DELETE_CARD_DIALOG);
				}
			})
			.show();
		}
		return super.onCreateDialog(id, args);
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
	 * Method callCardCompany ACTION_DIAL intent를 전송해주는 메소드
	 * @param v CardName이 있는 View
	 */
	public void callCardCompany(TextView v) {
		TextView tmpTv = v;
		Resources tmpRes = this.getResources();
		Intent callCompany = new Intent(Intent.ACTION_DIAL);
		callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_NH)));

		// NH카드 고객센터 전화번호 등록
		if (tmpTv.getText().equals(tmpRes.getString(R.string.NH_card)) || tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_NH))
				|| tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_NH_chaum))
				|| tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_NH_nonghyup))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_NH)));
	
		// KB국민카드 고객센터 전화번호 등록
		} else if (tmpTv.getText().equals(tmpRes.getString(R.string.KB_check)) || tmpTv.getText().equals(tmpRes.getString(R.string.KB_credit))
				|| tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_KB))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_KB)));
		
		// 롯데카드 고객센터 전화번호 등록
		} else if (tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_lotte))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_lotte)));
		
		// 우리카드 고객센터 전화번호 등록
		} else if (tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_woori))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_woori)));
		
		// 삼성카드 고객센터 전화번호 등록
		} else if (tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_samsung))) {
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_samsung)));
		
		// 신한카드 고객센터 전화번호 등록
		} else if (tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_shinhan))){
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_SHINHAN)));
						
		// 현대카드 고객센터 전화번호 등록
		} else if (tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_hyundai))){
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_HYUNDAI)));
			
		// KEB카드 고객센터 전화번호 등록
		}  else if (tmpTv.getText().toString().contains(tmpRes.getString(R.string.call_company_keb))){
			callCompany = new Intent(Intent.ACTION_DIAL);
			callCompany.setData(Uri.parse("tel:" + tmpRes.getString(R.string.phoneNum_KEB)));
		}	
		startActivity(callCompany);
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
			Intent goCardListActivity = new Intent(MyCardActivity.this, CardExpandableListActivity.class);
			startActivityForResult(goCardListActivity, GO_CARD_LIST_RESULT_OK);
			
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * MyCardAdapter ListView의 리스트에 정보를 공급해주는 Adapter 
	 * @author Junu Kim
	 */
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
				v = vi.inflate(R.layout.my_card_list_layout, null);
			}
			
			MyCardInfo m = items.get(position);
			
			if (m != null) {
				ImageView tmpCImage = (ImageView) v.findViewById(R.id.card_imageview);
				ImageView tmpPImage = (ImageView) v.findViewById(R.id.card_phone_imageview);
				final TextView tmpCName = (TextView) v.findViewById(R.id.card_name_textview);
				TextView tmpCNum = (TextView) v.findViewById(R.id.card_num_textview);

				tmpCImage.setImageResource(getResources().getIdentifier(m.getCardImageUri(), "drawable", getPackageName()));
				tmpPImage.setImageResource(m.getPhoneImage());
				tmpCName.setText(m.getCardName());
				tmpCNum.setText(m.getCardNumber());
				
				tmpCName.setSelected(true);
				tmpCNum.setSelected(true);
				
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
