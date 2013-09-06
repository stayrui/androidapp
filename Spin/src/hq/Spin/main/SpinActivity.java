package hq.Spin.main;


import hq.Spin.R;
import hq.Spin.service.FloatService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class SpinActivity extends Activity {
	/**
	 * ��ʼ��ť
	 */
	Button btnstart;
	/**
	 * �ر�������ť
	 */
	Button btnstop;
	/**
	 * ˵������
	 */
	TextView tv;
	private Context mContext=null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		FloatService.width = dm.widthPixels; // �õ����
		Init();
		MobclickAgent.onError(this);
		btnstart.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				Intent service = new Intent();
				service.setClass(SpinActivity.this, FloatService.class);
				startService(service);
				finish();
			}
		});
		com.umeng.common.Log.LOG = true;
		UmengUpdateAgent.setUpdateOnlyWifi(false); // Ŀǰ����Ĭ����Wi-Fi��������²Ž����Զ����ѡ�����Ҫ���������绷���½��и����Զ����ѣ�������Ӹ��д���
		UmengUpdateAgent.setUpdateAutoPopup(false);
		System.out.println("UmengUpdateAgent--->");
		UmengUpdateAgent.update(SpinActivity.this);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() 
		{ 
			@Override 
			public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo){
				switch (updateStatus) {
				case 0: // has update
					System.out.println( "callback result");
					UmengUpdateAgent.showUpdateDialog(SpinActivity.this, updateInfo);
					break;
				case 1: // has no update
					break;
				case 2: // none wifi
					break;
				case 3: // time out
					Toast.makeText(SpinActivity.this, "��ʱ", Toast.LENGTH_SHORT)
							.show();
					break;
				case 4: // is updating
					Toast.makeText(SpinActivity.this, "�������ظ���...", Toast.LENGTH_SHORT)
							.show();
					break;
				}}});
		btnstop.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent serviceStop = new Intent();
				serviceStop.setClass(SpinActivity.this, FloatService.class);
				stopService(serviceStop);
			}
		});

	}
	
	/**
	 * ��ʼ����������Ϣ���ֵ���ʾ���ؼ��ĳ�ʼ
	 */
	private void Init() {
		// TODO Auto-generated method stub
		btnstart = (Button) findViewById(R.id.btnstart);
		btnstop = (Button) findViewById(R.id.btnstop);
		tv = (TextView) findViewById(R.id.tv);
		String str = new StringBuilder()

				.append(this.getResources().getString(R.string.description))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item1))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item2))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item3))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item4))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item5))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item6))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item7))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item8))
				.append("\n")
				.append(this.getResources().getString(
						R.string.description_item9)).append("\n").toString();
		tv.setText(str);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		MobclickAgent.onPause(this);
		super.onPause();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		MobclickAgent.onResume(this);
		super.onResume();
	}
}