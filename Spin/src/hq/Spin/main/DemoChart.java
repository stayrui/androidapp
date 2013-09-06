package hq.Spin.main;

import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class DemoChart {

	private XYMultipleSeriesDataset dataset;

	private XYMultipleSeriesRenderer renderer;
	int length = 0;
	TimeSeries series = null;
	long value = new Date().getTime();
	public DemoChart() {
		super();
		dataset = new XYMultipleSeriesDataset();
		renderer = new XYMultipleSeriesRenderer();
		series = new TimeSeries("曲线图内存数据");
	}

	public DemoChart(boolean bool) {
		super();
		dataset = new XYMultipleSeriesDataset();
		renderer = new XYMultipleSeriesRenderer();
	}

	public TimeSeries getSeries() {
		return series;
	}

	public void setSeries(TimeSeries series) {
		this.series = series;
		System.out.println(this.series.getItemCount() + "=====0000");
	}

	public GraphicalView getChartGraphicalView(Context context) {
		return ChartFactory.getTimeChartView(context, dataset, renderer, null);
	}

	public XYMultipleSeriesDataset bulidBasicDataset() {
		return dataset;
	}

	public XYMultipleSeriesRenderer buildRenderer() {
		return renderer;
	}

	public void setRandererBasicProperty(String title, String xTitle,
			String yTitle, int axeColor, int labelColor) {

		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		// renderer.setRange(new double[]{0,100,0,10});

		renderer.setAxesColor(axeColor);
		renderer.setLabelsColor(labelColor);
		renderer.setXLabels(5);
		renderer.setYLabels(15);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(60);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.LEFT);

		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(2f);

		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		renderer.setShowGrid(true);
		renderer.setZoomEnabled(false, false);
	}

	public XYMultipleSeriesDataset getLastestDateset() {
		return dataset;
	}

	public XYMultipleSeriesRenderer getLastestRenderer() {
		return renderer;
	}

	public GraphicalView getDemoChartGraphicalView(Context context) {
		setRandererBasicProperty("", "X", "Y", Color.WHITE, Color.GRAY);
		XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
		xyRenderer.setColor(Color.RED);
		xyRenderer.setPointStyle(PointStyle.CIRCLE);
		if (dataset != null || renderer != null) {
			dataset.addSeries(series);
			renderer.addSeriesRenderer(xyRenderer);
		}
		return getChartGraphicalView(context);
	}

	public void updateData(double rate) {
		series.add(new Date(), rate);
	}

	public int getNumT() {
		return dataset.getSeries().length;

	}
}