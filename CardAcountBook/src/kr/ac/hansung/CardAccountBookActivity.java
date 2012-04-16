package kr.ac.hansung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CardAccountBookActivity extends Activity {
	private Button myCardBtn;
	private Button detailViewBtn;
	private Button chartViewBtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Button Create
		myCardBtn = (Button) findViewById(R.id.my_card_btn);
		detailViewBtn = (Button) findViewById(R.id.detail_view_btn); 
		chartViewBtn = (Button)findViewById(R.id.breakdown_stats_btn);
		
		// My Card Btn Click
		myCardBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myCardIntent = new Intent(CardAccountBookActivity.this,
						MyCardActivity.class);
				startActivity(myCardIntent);

			}
		});

		// Detail View Btn Click
		detailViewBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent detailViewIntent = new Intent(
						CardAccountBookActivity.this, DetailViewActivity.class);
				startActivity(detailViewIntent);
			}
		});
		//Chart View Btn Click
		chartViewBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent GraphViewIntent = new Intent(CardAccountBookActivity.this, GraphViewActivity.class);
				startActivity(GraphViewIntent);
			}
		});

	}
}