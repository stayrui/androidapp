package hq.Spin.main;

import hq.Spin.R;
import hq.Spin.service.FloatService;

import java.util.Random;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;

public class ActivityShow extends Activity {

	DemoChart demo;
	Random r;
	GraphicalView chartView;
	String appname = null;
	MyBroadcastReciver myReceiver = null;
	double nextRos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showchart);
		MobclickAgent.onError(this);
		LinearLayout linearView = (LinearLayout) findViewById(R.id.chart_show);
		// 查询保存的数据啊
		if (FloatService.series != null
				&& FloatService.series.getItemCount() > 0) {
			demo = new DemoChart(false);
			demo.setSeries(FloatService.series);
		}else
			demo = new DemoChart();
		chartView = demo.getDemoChartGraphicalView(this);
		linearView.addView(chartView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		FloatService.series = demo.getSeries();
		Intent intentFilter = new Intent();
		intentFilter.setAction("testingspin");
		sendBroadcast(intentFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		myReceiver = new MyBroadcastReciver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.telenjoy.sendbrostcast");
		intentFilter.addAction("close ActivityShowActivity");
		MobclickAgent.onResume(this);
		this.registerReceiver(myReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		unregisterReceiver(myReceiver);
	}

	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.telenjoy.sendbrostcast")) {
				nextRos = intent.getDoubleExtra("memdata", 0);
				setTitle(intent.getStringExtra("appname") + " 内存情况");
				demo.updateData(nextRos);
				chartView.postInvalidate();
			} else if (intent.getAction().equals("close ActivityShowActivity")) {
				finish();
			}
		}
	}
}