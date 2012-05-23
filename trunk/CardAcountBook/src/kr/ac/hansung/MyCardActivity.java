package kr.ac.hansung;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageView;
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
	private final static int EMPTY_INPUT_VALUE = 10;
	
	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;
	MyCardAdapter mAdapter;
	
	ListView cardListView;
	TextView clickedTextView;
	
	int longClickedPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card);
		
		final ArrayList<MyCardInfo> myCardList = new ArrayList<MyCardInfo>();

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
		} else if (requestCode == GO_CARD_LIST_RESULT_OK) {
			if (resultCode == Activity.RESULT_OK) {
				Bundle bdl = data.getBundleExtra("sendBdl");
				MyCardInfo tmpObj = new MyCardInfo(bdl.getString("cardName"), bdl.getString("cardNumber"), 
									bdl.getInt("paymentDay"), bdl.getInt("tAmount"), bdl.getString("cardType"), bdl.getInt("imageRsc"));
				
				mAdapter.add(tmpObj);
				mAdapter.notifyDataSetChanged();
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
						.setTitle("카드 삭제 하기")
						.setMessage("카드를 삭제하면 해당 카드의 상세내역도 함께 삭제 됩니다. 그래도 삭제 하시겠습니까?")
						.setPositiveButton("삭제", null)
						.setNegativeButton("취소", null)
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
	 * Method callCardCompany ACTION_DIAL intent를 전송해주는 메소드
	 * @param v CardName이 있는 View
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
	 * Method setAutoCardImage 나의카드에 추가될 카드의 이미지를 자동으로 찾아서 이미지리소스ID를 리턴하는 메소드
	 * @param cardName
	 * @return int Image Resource ID
	 */
	public int setAutoCardImage(String cardName) {
		Resources autoCardRsc = this.getResources();
		
		if (cardName.equals(autoCardRsc.getString(R.string.NH_card))) {
			return (int)(R.drawable.nh_chaum);
		} else if (cardName.equals(autoCardRsc.getString(R.string.KB_check))
				|| cardName.equals(autoCardRsc.getString(R.string.KB_credit))) {
			return (int)(R.drawable.kb_kookmin_star);
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
			Intent goCardListActivity = new Intent(MyCardActivity.this, CardListActivity.class);
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
