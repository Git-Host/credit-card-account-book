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
import android.content.Intent;
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
		setContentView(R.layout.monthly_graph_view);

		int iYear;
		double monthlyPrice[] = new double[12];

		CardDB Cdb = new CardDB(this);

		List<double[]> values = new ArrayList<double[]>();

		db = Cdb.getReadableDatabase();
		Calendar calendar = Calendar.getInstance();
		iYear = calendar.get(Calendar.YEAR);

		String strQuery[] = new String[12];

		for (int i = 0; i < strQuery.length; i++) {
			int month = i + 1;
			monthlyPrice[i] = 0;
			strQuery[i] = "Select price From breakdowstats where pYear = "
					+ iYear + " and pMonth = " + month + ";";
		}

		for (int i = 0; i < strQuery.length; i++) {
			int prices = 0;
			c = db.rawQuery(strQuery[i], null);
			while (c.moveToNext()) {
				prices += c.getInt(0);
			}

			monthlyPrice[i] = prices;
		}
		db.close();
		values.add(monthlyPrice);

		// ǥ���� ��ġ��
		double YAxisMax = monthlyPrice[0];

		/** �׷��� ����� ���� �׷��� �Ӽ� ������ü */
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// �з��� ���� �̸�
		String[] titles = new String[] { "���� ī�� ��뷮" };

		// �׸��� ǥ���ϴµ� ���� ����
		int[] colors = new int[] { Color.parseColor("#8a2BE2") };

		// �з��� ���� ũ�� �� �� ���� ����
		renderer.setLegendTextSize(25);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);

			renderer.addSeriesRenderer(r);
		}

		for (int i = 0; i < monthlyPrice.length; i++) {
			if (YAxisMax < monthlyPrice[i])
				YAxisMax = monthlyPrice[i];
		}
		double tmp = Math.ceil(YAxisMax / 100000);
		YAxisMax = tmp * 100000;

		// X,Y�� �׸��̸��� ���� ũ��
		renderer.setXTitle("��");
		renderer.setAxisTitleTextSize(20);
		renderer.setYTitle("��뷮", 0);
		renderer.setAxisTitleTextSize(20);
		
		// ��ġ�� ���� ũ�� / X�� �ּ�,�ִ밪 / Y�� �ּ�,�ִ밪
		renderer.setLabelsTextSize(15);
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(12.5);
		renderer.setYAxisMax(YAxisMax);
		renderer.setYAxisMin(0);
		
		renderer.setZoomLimits(new double[]{0,12.5,0 ,0});
		renderer.setPanLimits(new double[]{0.5,12.5,0,15});
		// X,Y�� ���� ����
		renderer.setAxesColor(Color.BLACK);
		// �������, X,Y�� ����, ��ġ���� ���� ����
		renderer.setLabelsColor(Color.DKGRAY);
		
		// X���� ǥ�� ����
		renderer.setXLabels(0);
		// Y���� ǥ�� ����
		renderer.setYLabels(0);

		// X,Y�� ���Ĺ���
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		// X,Y�� ��ũ�� ���� ON/OFF
		renderer.setPanEnabled(true, false);
		// ZOOM��� ON/OFF
		renderer.setZoomEnabled(true, false);
		// ZOOM ����
		renderer.setZoomRate(1.0f);
		// ���밣 ����
		renderer.setBarSpacing(0.5f);

		renderer.setApplyBackgroundColor(true);
		renderer.setMargins(new int[] { 50, 100, 50, 10 });
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0));

		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setShowGrid(false);

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
		
		for (int i = 1; i < 6; i++) {
			int tmp1 = (int)Math.ceil((YAxisMax /5));
			String ttt = SmsInfo.decimalPointToString(i*tmp1);
			renderer.addYTextLabel(tmp1*i, ttt);
		}
		for(int i = 0;i<13;i++){
			String tmp1 = String.valueOf(i+1);
			renderer.addXTextLabel(i+1,tmp1);
		}
		// �׷��� ��ü ����
		gv = ChartFactory
				.getBarChartView(this, dataset, renderer, Type.DEFAULT);
		gv.setOnClickListener(this);

		// �׷����� LinearLayout�� �߰�
		LinearLayout llBody = (LinearLayout) findViewById(R.id.Monthly);
		llBody.addView(gv);
		if(gv != null)

		   {

		          gv.invalidate();

		          gv.repaint();

		   }



	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		SeriesSelection seriesSelection = gv.getCurrentSeriesAndPoint();

		if (seriesSelection == null) {

		} else {

			Intent detailViewIntent = new Intent(MonthlyGraphActivity.this,
					DetailViewActivity.class);
			detailViewIntent.putExtra("selMonth", seriesSelection.getXValue());
			startActivity(detailViewIntent);

		}
	}
}