package kr.ac.hansung;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GraphViewActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_view);
		Button Monthly = (Button)findViewById(R.id.monthly);
		Button Category = (Button)findViewById(R.id.category);
		MyclickListener l = new MyclickListener();
		Monthly.setOnClickListener(l);
		Category.setOnClickListener(l);
	}
	public class MyclickListener implements OnClickListener
	{
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.monthly:
				Intent Monthly_Graph = new Intent(GraphViewActivity.this,
						MonthlyGraphActivity.class);
				startActivity(Monthly_Graph);
				break;
			case R.id.category:
				break;
			}
		}
		
	}
}
