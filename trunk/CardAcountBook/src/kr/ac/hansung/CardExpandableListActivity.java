package kr.ac.hansung;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * CardExpandableListActivity.java
 * MyCardActivity.java 에서 옵션메뉴 클릭으로 카드 수동 추가시 호출되는 Activity
 * @author Junu Kim
 */
public class CardExpandableListActivity extends ExpandableListActivity implements CardInfoList {
	private final static int CARD_ADD_DIALOG_SHOW = 0;
	private final static int INVALID_INPUT = 1;
	
	private CardExpandableListAdapter cardAdapter;
	private ExpandableListView cardExpandableListView; 
	
	private MyCardInfo returnCardObj;
	
	int dayListWhich = 0;
	
	private TextView addCardPaymentDay;
	private TextView addCardCardType;
	private EditText addCardNumber;
	private EditText addCardTargetPrice;
	private TextView addCardName;
	private ImageView addCardImage;
	
	String tmpCardName;
	String tmpReduceCardName;
	int tmpCardImage;
	
	String[] dayList;
	String[] cardTypeList;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_card_expandable_list_layout);
		MyCardInfo cardElement;
		
		dayList = getResources().getStringArray(R.array.day_list);
		cardTypeList = getResources().getStringArray(R.array.card_type_list);
		
		// Group List
		ArrayList<String> cardCompanyList = new ArrayList<String>();
		cardCompanyList.add(getResources().getString(R.string.KB_card));
		cardCompanyList.add(getResources().getString(R.string.NH_card));
		cardCompanyList.add(getResources().getString(R.string.Lotte_card));
		cardCompanyList.add(getResources().getString(R.string.Woori_card));
		cardCompanyList.add(getResources().getString(R.string.Samsung_card));
		cardCompanyList.add(getResources().getString(R.string.Sinhan_card));
		cardCompanyList.add(getResources().getString(R.string.Hyundai_card));
		
		// Child List
		ArrayList<ArrayList<MyCardInfo>> cardList = new ArrayList<ArrayList<MyCardInfo>>();
		ArrayList<MyCardInfo> kbChildList = new ArrayList<MyCardInfo>();
		ArrayList<MyCardInfo> nhChildList = new ArrayList<MyCardInfo>();
		ArrayList<MyCardInfo> lotteChildList = new ArrayList<MyCardInfo>();
		ArrayList<MyCardInfo> WooriChildList = new ArrayList<MyCardInfo>();
		ArrayList<MyCardInfo> samsungChildList = new ArrayList<MyCardInfo>();
		ArrayList<MyCardInfo> SinhanChildList = new ArrayList<MyCardInfo>();
		ArrayList<MyCardInfo> HyundaiChildList = new ArrayList<MyCardInfo>();
		
		// KB child ADD
		for (int i=0; i<CardInfoList.kbCardImg.length; i++) {
			cardElement = new MyCardInfo();
			cardElement.setCardImage(kbCardImg[i]);
			cardElement.setCardName(getResources().getString(kbCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.KB_card));
			kbChildList.add(cardElement);
		}
		
		// NH child ADD
		for (int i=0; i<CardInfoList.nhCardImg.length; i++) {
			cardElement = new MyCardInfo();
			cardElement.setCardImage(nhCardImg[i]);
			cardElement.setCardName(getResources().getString(nhCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.NH_card));
			nhChildList.add(cardElement);
		}
		
		//Lotte child ADD
		for(int i=0;i<CardInfoList.LotteCardImg.length;i++){
			cardElement = new MyCardInfo();
			cardElement.setCardImage(LotteCardImg[i]);
			cardElement.setCardName(getResources().getString(LotteCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.Lotte_card));
			lotteChildList.add(cardElement);
		}
		
		//Woori child ADD
		for(int i =0;i<CardInfoList.WooriCardImg.length;i++){
			cardElement = new MyCardInfo();
			cardElement.setCardImage(WooriCardImg[i]);
			cardElement.setCardName(getResources().getString(WooriCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.Woori_card));
			WooriChildList.add(cardElement);
		}
		
		//Samsung child ADD
		for(int i =0;i<CardInfoList.SamsungCardImg.length;i++){
			cardElement = new MyCardInfo();
			cardElement.setCardImage(SamsungCardImg[i]);
			cardElement.setCardName(getResources().getString(SamsungCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.Samsung_card));
			samsungChildList.add(cardElement);
			
		}
		
		//Shinhan child ADD
		for(int i=0;i<CardInfoList.SinhanCardImg.length;i++){
			cardElement = new MyCardInfo();
			cardElement.setCardImage(SinhanCardImg[i]);
			cardElement.setCardName(getResources().getString(SinhanCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.Sinhan_card));
			SinhanChildList.add(cardElement);
			
		}
		
		//Hyundai Child ADD
		for(int i=0;i<CardInfoList.HyundaiCardImg.length;i++){
			cardElement = new MyCardInfo();
			cardElement.setCardImage(HyundaiCardImg[i]);
			cardElement.setCardName(getResources().getString(HyundaiCardName[i]));
			cardElement.setReduceCardName(getResources().getString(R.string.Hyundai_card));
			HyundaiChildList.add(cardElement);
			
		}
		
		// Attach child list to group ist
		cardList.add(kbChildList);
		cardList.add(nhChildList);
		cardList.add(lotteChildList);
		cardList.add(WooriChildList);
		cardList.add(samsungChildList);
		cardList.add(SinhanChildList);
		cardList.add(HyundaiChildList);
		
		cardAdapter = new CardExpandableListAdapter(cardCompanyList, cardList);
		setListAdapter(cardAdapter);
		
		cardExpandableListView = getExpandableListView();
		cardExpandableListView.expandGroup(0);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog addDialog;
		AlertDialog.Builder addBuilder;
		switch (id) {
		
		case CARD_ADD_DIALOG_SHOW :
			addBuilder = new AlertDialog.Builder(this);
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
		
		case INVALID_INPUT :
			addBuilder = new AlertDialog.Builder(this);
			
			addBuilder
			.setTitle(R.string.invalid_input_alert_title)
			.setMessage(R.string.invalid_input_alert_description)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(R.string.identify_string, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(INVALID_INPUT);
				}
			});
			
			addDialog = addBuilder.create();
			return addDialog;
		}
		return super.onCreateDialog(id, args);
	}

	public class AddMyCardListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE :
				int tAmount = 0;

				// When CardName, CardNumber were Blank
				if (addCardNumber.getText().toString().equals("")) {
					removeDialog(CARD_ADD_DIALOG_SHOW);
					showDialog(INVALID_INPUT);
					break;
					
				} else if (!addCardTargetPrice.getText().toString().equals("") && !addCardNumber.getText().toString().equals("")) {
					tAmount = Integer.parseInt(addCardTargetPrice.getText().toString());
				}
				
				int paymentDay = Integer.parseInt(dayList[dayListWhich].replace("일", ""));
				int cardImage = tmpCardImage;
				String cardName = tmpCardName;
				String cardNumber = addCardNumber.getText().toString();
				String cardType = addCardCardType.getText().toString();
				String cardImageUri = getResources().getResourceName(cardImage);
				int primaryKey = 0;
				
				if (tmpReduceCardName.equals(getResources().getString(R.string.KB_card))) {
					if (cardType.equals(cardTypeList[0])) {
						cardName = tmpReduceCardName + getResources().getString(R.string.just_card);
					} else {
						cardName = tmpReduceCardName + cardTypeList[1];
					}
				}
				
				
				SQLiteDatabase db;
				CardDB Cdb = new CardDB(getApplicationContext());
				db = Cdb.getReadableDatabase();
				String insertQuery = "INSERT INTO myCard VALUES (null, '" + cardName + "', '" + cardNumber + "', " + paymentDay + ", "
									  + tAmount + ",'" + cardType + "', '" + cardImageUri + "', 0);";
				db.execSQL(insertQuery);
				
				String getKeyQuery = "SELECT myCardKey FROM myCard WHERE cardName = '" + cardName + "' AND cardNumber = '"
									 + cardNumber + "' AND cardImageUri = '" + cardImageUri + "';";
				Cursor c = db.rawQuery(getKeyQuery, null);
				
				while (c.moveToNext()) {
					primaryKey = c.getInt(c.getColumnIndex("myCardKey"));
				}
				db.close();

				returnCardObj = new MyCardInfo(primaryKey, cardName, cardNumber, paymentDay, tAmount, cardType, cardImageUri);
					
				Intent intent = new Intent();
				Bundle bdl = new Bundle();
					
				bdl.putInt("myCardKey", returnCardObj.getCardPrimaryKey());
				bdl.putString("cardName", returnCardObj.getCardName());
				bdl.putString("cardNumber", returnCardObj.getCardNumber());
				bdl.putString("cardType", returnCardObj.getCardType());
				bdl.putInt("paymentDay", returnCardObj.getPaymentDay());
				bdl.putInt("tAmount", returnCardObj.getTAmount());
				bdl.putString("cardImageUri", returnCardObj.getCardImageUri());
					
				intent.putExtra("sendBdl", bdl);
				setResult(Activity.RESULT_OK, intent);
				finish();
				
				break;
			case DialogInterface.BUTTON_NEGATIVE :
				setResult(Activity.RESULT_CANCELED);
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
				new AlertDialog.Builder(CardExpandableListActivity.this)
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
				new AlertDialog.Builder(CardExpandableListActivity.this)
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
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		MyCardInfo tmpObj = (MyCardInfo) cardAdapter.getChild(groupPosition, childPosition);
	
		Bundle bdl = new Bundle();
		bdl.putString("cardName", tmpObj.getCardName());
		bdl.putString("reduceCardName", tmpObj.getReduceCardName());
		bdl.putInt("cardImage", tmpObj.getCardImage());
		showDialog(CARD_ADD_DIALOG_SHOW, bdl);
		
		return true;
	}

	public class CardExpandableListAdapter extends BaseExpandableListAdapter {
		private ArrayList<String> groups;
		private ArrayList<ArrayList<MyCardInfo>> children;
		
		public CardExpandableListAdapter(ArrayList<String> groups, ArrayList<ArrayList<MyCardInfo>> children) {
			this.groups = groups;
			this.children = children;
		}
		
		@Override
		public void onGroupCollapsed(int groupPosition) {
			super.onGroupCollapsed(groupPosition);
		}

		@Override
		public void onGroupExpanded(int groupPosition) {
			
			int groupSize = getGroupCount();
			boolean[] isGroupExpanded = new boolean[groupSize];
			
			for (int i=0; i<groupSize; i++)
				isGroupExpanded[i] = false;
			
			isGroupExpanded[groupPosition] = true;
			
			for (int i=0; i<groupSize; i++) {
				if (isGroupExpanded[i] == false) {
					cardExpandableListView.collapseGroup(i);
				} else {
					cardExpandableListView.expandGroup(i);
				}
			}
			super.onGroupExpanded(groupPosition);
		}


		public Object getChild(int groupPosition, int childPosition) {
			return children.get(groupPosition).get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			MyCardInfo tmpMyCard = (MyCardInfo) getChild(groupPosition, childPosition);
			
			if (convertView == null) {
				LayoutInflater childLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = childLayoutInflater.inflate(R.layout.my_card_expandable_child_layout, null);
			}
			
			ImageView tmpCardImageView  = (ImageView) convertView.findViewById(R.id.expandable_child_card_image_view);
			TextView tmpCardTextView = (TextView) convertView.findViewById(R.id.expandable_child_card_name_view);
			
			tmpCardImageView.setImageResource(tmpMyCard.getCardImage());
			tmpCardTextView.setText(tmpMyCard.getCardName());
			
			return convertView;
		}

		public int getChildrenCount(int groupPosition) {
			return children.get(groupPosition).size();
		}

		public Object getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		public int getGroupCount() {
			return groups.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			String group = (String) getGroup(groupPosition);
			
			if (convertView == null) {
				LayoutInflater groupLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = groupLayoutInflater.inflate(R.layout.my_card_expandable_group_layout, null);
			}
			
			TextView tmpCompany = (TextView) convertView.findViewById(R.id.expandable_group_card_name_view);
			ImageView arrowImage = (ImageView) convertView.findViewById(R.id.expandable_group_arrow_image_view);
			
			if (isExpanded == true) {
				arrowImage.setImageResource(R.drawable.expandable_group_arrow_up);
			} else {
				arrowImage.setImageResource(R.drawable.expandable_group_arrow_down);
			}
			
			tmpCompany.setText(group);
			return convertView;
		}

		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}
		
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}
		
	}

}
