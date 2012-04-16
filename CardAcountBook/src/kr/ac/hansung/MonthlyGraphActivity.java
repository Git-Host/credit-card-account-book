package kr.ac.hansung;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MonthlyGraphActivity extends Activity implements OnClickListener {
	GraphicalView gv;
	SQLiteDatabase db;
	CardDB Cdb;
	Cursor c;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.monthly_grapgh_view);
		
		int iYear;
		double monthlyPrice[] = new double[12];
		
		CardDB Cdb = new CardDB(this);
		
		List<double[]> values = new ArrayList<double[]>();
		
		
		db = Cdb.getReadableDatabase();
		Calendar calendar = Calendar.getInstance();
		iYear = calendar.get(Calendar.YEAR);
		
		String strQuery[] = new String[12];
		
		for(int i = 0;i<strQuery.length;i++){
			int month = i+1;
			monthlyPrice[i] = 0;
			strQuery[i] = "Select price From breakdowstats where pYear = "+iYear+" and pMonth = "+month+";";
		}
				
		for(int i = 0;i<strQuery.length;i++){
			int prices = 0;
			c =db.rawQuery(strQuery[i], null);
			while(c.moveToNext()){
				prices += c.getInt(0);
			}	
			
			monthlyPrice[i] = prices;
		}
		db.close();
		values.add(monthlyPrice);
		
		// ǥ���� ��ġ��
		

		/** �׷��� ����� ���� �׷��� �Ӽ� ������ü */
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// ��� ǥ�� ����� ���� ũ��
		renderer.setChartTitle("���� ���");
		renderer.setChartTitleTextSize(20);

		// �з��� ���� �̸�
		String[] titles = new String[] { "���� ī�� ��뷮" };

		// �׸��� ǥ���ϴµ� ���� ����
		int[] colors = new int[] { Color.YELLOW };

		// �з��� ���� ũ�� �� �� ���� ����
		renderer.setLegendTextSize(15);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);

			renderer.addSeriesRenderer(r);
		}

		// X,Y�� �׸��̸��� ���� ũ��
		renderer.setXTitle("��");

		renderer.setYTitle("��뷮", 0);
		renderer.setAxisTitleTextSize(12);

		// ��ġ�� ���� ũ�� / X�� �ּ�,�ִ밪 / Y�� �ּ�,�ִ밪
		renderer.setLabelsTextSize(10);
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(12.5);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(24000);

		// X,Y�� ���� ����
		renderer.setAxesColor(Color.BLACK);
		// �������, X,Y�� ����, ��ġ���� ���� ����
		renderer.setLabelsColor(Color.DKGRAY);

		// X���� ǥ�� ����
		renderer.setXLabels(12);
		// Y���� ǥ�� ����
		renderer.setYLabels(5);

		// X,Y�� ���Ĺ���
		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabelsAlign(Align.LEFT);
		// X,Y�� ��ũ�� ���� ON/OFF
		renderer.setPanEnabled(false, false);
		// ZOOM��� ON/OFF
		renderer.setZoomEnabled(false, false);
		// ZOOM ����
		renderer.setZoomRate(1.0f);
		// ���밣 ����
		renderer.setBarSpacing(0.5f);

		renderer.setApplyBackgroundColor(true);
		renderer.setMargins(new int[] { 30, 30, 30, 30 });
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0));

		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setGridColor(getResources().getColor(R.color.grey));
		renderer.setShowGrid(true);

		// ���� ���� ����
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		for (int i = 0; i < titles.length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		renderer.setClickEnabled(true);

		// �׷��� ��ü ����
		gv = ChartFactory
				.getBarChartView(this, dataset, renderer, Type.DEFAULT);
		gv.setOnClickListener(this);

		// �׷����� LinearLayout�� �߰�
		LinearLayout llBody = (LinearLayout) findViewById(R.id.Monthly);
		llBody.addView(gv);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		SeriesSelection seriesSelection = gv.getCurrentSeriesAndPoint();
		double[] xy = gv.toRealPoint(0);
		if (seriesSelection == null) {
			Toast.makeText(MonthlyGraphActivity.this,
					"No chart element was clicked", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(
					MonthlyGraphActivity.this,
					"Chart element in series index "
							+ seriesSelection.getSeriesIndex()
							+ " data point index "
							+ seriesSelection.getPointIndex() + " was clicked"
							+ " closest point value X="
							+ seriesSelection.getXValue() + ", Y="
							+ seriesSelection.getValue()
							+ " clicked point value X=" + (float) xy[0]
							+ ", Y=" + (float) xy[1], Toast.LENGTH_SHORT)
					.show();
		}
	}

}