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

public class MonthlyGraphActivity extends Activity{
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setGraph();
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		setGraph();
		super.onActivityResult(requestCode, resultCode, data);
	}



	public void setGraph(){
		final GraphicalView gv;
		SQLiteDatabase db;
		CardDB Cdb;
		Cursor c;
		int iYear,iMonth;
		
		setContentView(R.layout.monthly_graph_view);

		Cdb = new CardDB(this);

		List<double[]> values = new ArrayList<double[]>();

		db = Cdb.getReadableDatabase();
		Calendar calendar = Calendar.getInstance();
		iYear = calendar.get(Calendar.YEAR);
		iMonth = calendar.get(Calendar.MONTH)+1;
		if(iMonth<6){
			iMonth = 5;
		}
		
		double monthlyPrice[] = new double[iMonth];
		String strQuery[] = new String[iMonth];

		for (int i = 0; i < strQuery.length; i++) {
			int month = i + 1;
			monthlyPrice[i] = 0;
			strQuery[i] = "Select price From breakdowstats where pYear = "
					+ iYear + " and pMonth = " + month + " AND deleteFlag = 0;";
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
		
		
		// 표시할 수치값
		double YAxisMax = monthlyPrice[0]/1000;

		/** 그래프 출력을 위한 그래픽 속성 지정객체 */
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

		// 분류에 대한 이름
		String[] titles = new String[] { "월별 카드 사용량" };

		// 항목을 표시하는데 사용될 색상값
		int[] colors = new int[] { Color.parseColor("#90EE90") };
		
		// 분류명 글자 크기 및 각 색상 지정
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
		YAxisMax = tmp * 100;
		
		// X,Y축 항목이름과 글자 크기
		renderer.setXTitle("월");
		renderer.setAxisTitleTextSize(20);
				
		// 수치값 글자 크기 / X축 최소,최대값 / Y축 최소,최대값
		renderer.setLabelsTextSize(20);
		renderer.setXAxisMin(0.5);
		renderer.setXAxisMax(iMonth+0.5);
		renderer.setYAxisMax(YAxisMax);
		renderer.setYAxisMin(0);
		
		
		renderer.setPanLimits(new double[]{0.5,iMonth+0.5,0,0});
		// X,Y축 라인 색상
		renderer.setAxesColor(Color.BLACK);
		// 상단제목, X,Y축 제목, 수치값의 글자 색상
		renderer.setLabelsColor(Color.DKGRAY);
		
		// X축의 표시 간격
		renderer.setXLabels(iMonth);
		// Y축의 표시 간격
		renderer.setYLabels(10);

		// X,Y축 정렬방향
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		// X,Y축 스크롤 여부 ON/OFF
		renderer.setPanEnabled(true, false);
		// ZOOM기능 ON/OFF
		renderer.setZoomEnabled(false, false);
		// ZOOM 비율
		renderer.setZoomRate(1.0f);
		// 막대간 간격
		renderer.setBarSpacing(0.5f);
		
		renderer.setApplyBackgroundColor(true);
		renderer.setMargins(new int[] { 60, 60, 50, 10 });
		renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0));
		
		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setGridColor(Color.parseColor("#D3D3D3"));
		renderer.setShowGrid(true);
		
		
		
	
		// 설정 정보 설정
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		for (int i = 0; i < titles.length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]/1000);
			}
			dataset.addSeries(series.toXYSeries());

		}
		renderer.setClickEnabled(true);
		
		
		
		
		renderer.getSeriesRendererAt(0).setChartValuesTextSize(25);
		// 그래프 객체 생성
		gv = ChartFactory
				.getBarChartView(this, dataset, renderer, Type.DEFAULT);
		gv.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SeriesSelection seriesSelection = gv.getCurrentSeriesAndPoint();

				if (seriesSelection != null) {
					Intent detailViewIntent = new Intent(MonthlyGraphActivity.this,
							DetailViewActivity.class);
					detailViewIntent.putExtra("selMonth", seriesSelection.getXValue());
					startActivityForResult(detailViewIntent,0);

				}
			}
		});

		// 그래프를 LinearLayout에 추가
		LinearLayout llBody = (LinearLayout) findViewById(R.id.Monthly);
		llBody.addView(gv);
		if(gv != null)

		   {

		          gv.invalidate();

		          gv.repaint();

		   }
		
	}
	
}