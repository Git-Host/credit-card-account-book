package kr.ac.hansung;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Admin
 * 
 */
public class CategoryGraphActivity extends Activity {
	
	private final static int GO_DETAIL = 0;
	Date date = new Date();
	int currentMonth = date.getMonth() + 1;
	int selmonth = currentMonth;
	
	int[] colors = new int[] { Color.parseColor("#F08080"),
			Color.parseColor("#7CFC00"), Color.parseColor("#EE82EE"),
			Color.parseColor("#87CEFA"), Color.parseColor("#FFD700"),
			Color.parseColor("#90EE90"), Color.parseColor("#7B68EE"),
			Color.parseColor("#FF69B4"), Color.parseColor("#A0A0FF"),
			Color.parseColor("#7BFF75") };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_graph_view);
		ImageView back = (ImageView)findViewById(R.id.month_back);
		ImageView forward = (ImageView)findViewById(R.id.month_forward);
		ClickListener l = new ClickListener();
		back.setOnClickListener(l);
		forward.setOnClickListener(l);
		TextView mtext = (TextView)findViewById(R.id.month_view);
		mtext.setTextColor(Color.WHITE);
		mtext.setText(currentMonth+"월");
		setGraph(currentMonth);
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		setGraph(currentMonth);
		super.onActivityResult(requestCode, resultCode, data);
	}
	public class ClickListener implements OnClickListener{
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView mtext = (TextView)findViewById(R.id.month_view);; 
			mtext.setTextColor(Color.WHITE);
			switch(v.getId()){
			case R.id.month_back:
				if(selmonth>1){
					selmonth--;
					mtext.setText(selmonth+"월");
					setGraph(selmonth);
				}
				break;
			case R.id.month_forward:
				if(selmonth<12){
					selmonth++;
					mtext.setText(selmonth+"월");
					setGraph(selmonth);
				}
				break;
			
			}
		}
		
	}


	public void setGraph(int currentMonth){
		
		final GraphicalView mChartView;
		final ArrayList<String> categories = new ArrayList<String>();
		
		SQLiteDatabase db;
		Cursor c;
		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();
		
		// Pie차트 그리기
		LinearLayout incomePiechartLayout = (LinearLayout) findViewById(R.id.pie_graph_layout);
		incomePiechartLayout.removeAllViews();

		
		int currentYear = date.getYear() + 1900;
		

		String strQuery = "Select Distinct category from breakdowstats where pYear = "
				+ currentYear + " and pMonth = " + currentMonth + " AND deleteFlag = 0;";
		c = db.rawQuery(strQuery, null);
		ArrayList<String> Icategories = new ArrayList<String>();
		ArrayList<Double> values = new ArrayList<Double>();

		while (c.moveToNext()) {
			for (int i = 0; i < CategoryList.i_category.length; i++) {
				for (int j = 0; j < CategoryList.i_category[i].length; j++) {
					String getCategory = getResources().getString(CategoryList.i_category[i][j]);
					
					if (c.getString(0).matches(getCategory)) {
						Icategories.add(CategoryList.High_Category[i]);

					}
				}
			}
		}
		HashSet<String> hs = new HashSet<String>(Icategories);
		Iterator it = hs.iterator();
		while (it.hasNext()) {
			categories.add((String) it.next());
		}
		

		c.moveToFirst();
		for (int i = 0; i < CategoryList.High_Category.length; i++) {
			int j = categories.indexOf(CategoryList.High_Category[i]);

			double sum = 0;
			if (j != -1) {
				for (int k = 0; k < CategoryList.i_category[i].length; k++) {
					String s = getResources().getString(
							CategoryList.i_category[i][k]);
					String ss = "Select price from breakdowstats where pYear = "
							+ currentYear
							+ " and pMonth = "
							+ currentMonth
							+ " and category = '" + s + "' AND deleteFlag = 0;";
					c = db.rawQuery(ss, null);
					while (c.moveToNext()) {
						double value = c.getInt(0);
						sum += value;
					}
				}
				values.add(sum);
			}

		}

		

		// sereise
		CategorySeries series = new CategorySeries("!1");
		int cIndex = 0;
		for (double value : values) {
			series.add(categories.get(cIndex), value);
			cIndex++;
		}
		

		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(25);
		renderer.setShowAxes(true);
		db.close();

		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int i = 0; i < categories.size(); i++) {
			int color = colors[i];
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		renderer.setZoomButtonsVisible(false);
		renderer.setZoomEnabled(false);
		renderer.setLabelsColor(Color.BLACK);
		// ChartFactory.getPieChartView
		renderer.setShowLegend(true);
		renderer.setShowLabels(false);
		mChartView = ChartFactory.getPieChartView(this, series, renderer);
		
		mChartView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int c = mChartView.toBitmap().getPixel((int) event.getX(),
						(int) event.getY());
				String category = getResources().getString(R.string.c_etc);
				Intent detailViewIntent = new Intent(
						CategoryGraphActivity.this, DetailViewActivity.class);
				if (c == colors[0]) {
					category = categories.get(0);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[1]) {
					category = categories.get(1);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[2]) {
					category = categories.get(2);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[3]) {
					category = categories.get(3);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[4]) {
					category = categories.get(4);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[5]) {
					category = categories.get(5);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[6]) {
					category = categories.get(6);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[7]) {
					category = categories.get(7);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[8]) {
					category = categories.get(8);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				} else if (c == colors[9]) {
					category = categories.get(9);
					if (event.getAction() == MotionEvent.ACTION_UP) {
						detailViewIntent.putExtra("selCategory", category);
						startActivityForResult(detailViewIntent, GO_DETAIL);
					}
				}				
				return true;
			}
		});
		// mChartView.setBackgroundColor(Color.WHITE);
		incomePiechartLayout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		if (mChartView != null)

		{
			
			mChartView.invalidate();

			mChartView.repaint();

		}
		
	}
}
