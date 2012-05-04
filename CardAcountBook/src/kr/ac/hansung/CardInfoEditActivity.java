package kr.ac.hansung;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CardInfoEditActivity extends Activity implements CardList {
	private Intent parentIntent;
	
	private TextView menuEditImage;
	private TextView menuEditPayment;
	private TextView menuEditCardType;
	private TextView menuEditTAmount;
	
	private ImageView titleCardImage; 
	private TextView titleCardName;
	private TextView titleCardNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.card_info_edit);
		super.onCreate(savedInstanceState);
		parentIntent = this.getIntent();
		
		Bundle parentBdl = new Bundle();
		parentBdl = parentIntent.getExtras();
		
		titleCardImage = (ImageView) findViewById(R.id.edit_card_image);
		titleCardName = (TextView) findViewById(R.id.edit_card_title);
		titleCardNumber = (TextView) findViewById(R.id.edit_card_number);
		
		menuEditImage = (TextView) findViewById(R.id.edit_image);
		menuEditPayment = (TextView) findViewById(R.id.edit_payment_day);
		menuEditCardType = (TextView) findViewById(R.id.edit_card_type);
		menuEditTAmount = (TextView) findViewById(R.id.edit_t_amount);
		
		menuEditImage.setOnClickListener(new menuClickListener());
		menuEditPayment.setOnClickListener(new menuClickListener());
		menuEditCardType.setOnClickListener(new menuClickListener());
		menuEditTAmount.setOnClickListener(new menuClickListener());
		
		titleCardImage.setImageResource(parentBdl.getInt("imageRsc"));
		titleCardName.setText(parentBdl.getString("cardName").toString());
		titleCardNumber.setText(parentBdl.getString("cardNumber").toString());
		
	}

	public class menuClickListener implements View.OnClickListener {
		Drawable res = getResources().getDrawable(R.drawable.nh_chaum);
		
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
				break;
			case R.id.edit_card_type :
				break;
			case R.id.edit_t_amount :
				break;
			}
		}
	}
}
