package kr.ac.hansung;

import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class CategoryGraphActivity extends Activity {
	private GraphicalView mChartView;
	private SQLiteDatabase db;
	private CardDB Cdb;
	private Cursor c;
	private int SelMonth;
	final static int[] colors = new int[] { Color.BLUE, Color.GREEN,
			Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.RED, Color.WHITE,
			Color.DKGRAY, Color.LTGRAY };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_graph_view);

		CardDB Cdb = new CardDB(this);
		db = Cdb.getReadableDatabase();

		Date date = new Date();
		int currentYear = date.getYear() + 1900;
		int currentMonth = date.getMonth() + 1;

		String strQuery = "Select Distinct category from breakdowstats where pYear = "
				+ currentYear + " and pMonth = " + currentMonth + ";";
		c = db.rawQuery(strQuery, null);

		ArrayList<String> categories = new ArrayList<String>();
		ArrayList<Double> values = new ArrayList<Double>();
		while (c.moveToNext()) {
			categories.add(c.getString(0));
		}

		c.moveToFirst();

		for (int i = 0; i < categories.size(); i++) {
			String ss = "Select price from breakdowstats where pYear = "
					+ currentYear + " and pMonth = " + currentMonth
					+ " and category = '" + categories.get(i) + "';";
			
			c = db.rawQuery(ss, null);
			double sum = 0;
			while (c.moveToNext()) {
				double value = c.getInt(0);
				sum += value;
			}
			values.add(sum);
		}

		// Pie차트 그리기
		LinearLayout incomePiechartLayout = (LinearLayout) findViewById(R.id.pie_graph_layout);

		// sereise
		CategorySeries series = new CategorySeries("!1");
		int cIndex = 0;
		for (double value : values) {
			series.add(categories.get(cIndex), value);
			cIndex++;
		}

		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		
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
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsColor(Color.BLACK);
		// ChartFactory.getPieChartView
		mChartView = ChartFactory.getPieChartView(this, series, renderer);
		// mChartView.setBackgroundColor(Color.WHITE);
		incomePiechartLayout.addView(mChartView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
}
