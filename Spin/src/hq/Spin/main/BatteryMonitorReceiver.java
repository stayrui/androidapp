package hq.Spin.main;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

public class BatteryMonitorReceiver extends Service {
	private int currentBatteryLevel = 0;
	private final int EVENT_LOCK_WINDOW = 0x100;
	private Timer mTimer;
	private MyTimerTask mTimerTask;
	long lon = 3000;
	StringBuffer sb = new StringBuffer();
	String strsig = "";
	GsmCellLocation gsm = null;
	CdmaCellLocation cdma = null;
	TelephonyManager telephonyManager = null;

	@Override
	public void onCreate() {
		super.onCreate();
		// 电话信息相关
		telephonyManager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(pStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			if (message.what == 0x100) {
				// 执行任务
				StartLockWindowTimer();
			}
		}
	};

	public void StartLockWindowTimer() {
		if (mTimer != null) {
			if (mTimerTask != null) {
				mTimerTask.cancel(); // 将原任务从队列中移除
			}
			mTimerTask = new MyTimerTask(); // 新建一个任务
			mTimer.schedule(mTimerTask, lon*1000);
			System.out.println(lon*1000+"------------------------>"+new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date(
					System.currentTimeMillis())));
		}
	}

	PhoneStateListener pStateListener = new PhoneStateListener() {
		@Override
		public void onSignalStrengthChanged(int asu) {
			super.onSignalStrengthChanged(asu);
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			super.onCellLocationChanged(location);
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			strsig = "信号强度："
					+ String.valueOf(-113 + 2
							* signalStrength.getGsmSignalStrength())
					+ "  dBm    "
					+ String.valueOf(signalStrength.getGsmSignalStrength()
							+ "  asu") + ",信号GsmBitErrorRate:"
					+ signalStrength.getGsmBitErrorRate() + ",信号CdmaDbm:"
					+ signalStrength.getCdmaDbm() + ",信号CdmaEcio:"
					+ signalStrength.getCdmaEcio() + ",信号EvdoDbm:"
					+ signalStrength.getEvdoDbm() + ",信号EvdoEcio:"
					+ signalStrength.getEvdoEcio() + ",信号EvdoSnr:"
					+ signalStrength.getEvdoSnr();
		}

	};

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			// 本次任务执行完毕，发送消息
			sb.append("手机基本信息:（本次采集样本的前提是GPS开启，WIFI开启）\n");
			// 电话类型信息
			sb.append("1.电话类型信息:\n");
			int type = telephonyManager.getPhoneType();
			if ((type == TelephonyManager.PHONE_TYPE_GSM)) {
				gsm = (GsmCellLocation) telephonyManager.getCellLocation();
				sb.append("手机类型：GSM,\n");
				jiZhanMsg(gsm);
			} else if (type == TelephonyManager.PHONE_TYPE_CDMA) {
				sb.append("手机类型：CDMA,\n");
				cdma = (CdmaCellLocation) telephonyManager.getCellLocation();
			} else if (type == TelephonyManager.PHONE_TYPE_NONE) {
				sb.append("手机类型：无信号,\n");
				// Toast.makeText(PhoneGpsActivity.this, "手机无信号!!",
				// Toast.LENGTH_SHORT).show();
				return;
			}
			Message msg = mHandler.obtainMessage(EVENT_LOCK_WINDOW);
			msg.sendToTarget();
		}

	}

	private void jiZhanMsg(GsmCellLocation cell) {
		// 电话的呼叫状态
		sb.append("2.电话的呼叫状态:\n");
		int callStatus = telephonyManager.getCallState();
		if (callStatus == TelephonyManager.CALL_STATE_IDLE) {
			sb.append("电话的呼叫状态:  手机空闲,\n");
		} else if (callStatus == TelephonyManager.CALL_STATE_OFFHOOK) {
			sb.append("电话的呼叫状态:  接电话中,\n");
		} else if (callStatus == TelephonyManager.CALL_STATE_RINGING) {
			sb.append("电话的呼叫状态:  电话响铃中,\n");
		}
		// 设备的位置信息
		sb.append("3.设备的位置信息:\n");
		int cid = cell.getCid();
		int lac = cell.getLac();
		int mcc = Integer.valueOf(telephonyManager.getNetworkOperator()
				.substring(0, 3));
		int mnc = Integer.valueOf(telephonyManager.getNetworkOperator()
				.substring(3, 5));
		sb.append("基站编号：" + cid + ",");
		sb.append("位置区号码：" + lac + ",");
		sb.append("移动客户国家码：" + mcc + ",");
		sb.append("移动网号(中移0，中联1)：" + mnc + ",\n");

		// 数据连接信息(状态和活动)
		sb.append("4.数据连接信息(状态和活动):\n");
		int dataConnStatus = telephonyManager.getDataState();
		switch (dataConnStatus) {
		case TelephonyManager.DATA_CONNECTED:
			sb.append("连接状态：连接" + ",\n");
			break;
		case TelephonyManager.DATA_CONNECTING:
			sb.append("连接状态：目前设立一个数据连接" + ",\n");
			break;
		case TelephonyManager.DATA_DISCONNECTED:
			sb.append("连接状态：断开" + ",\n");
			break;
		case TelephonyManager.DATA_SUSPENDED:
			sb.append("连接状态：暂停" + ",\n");
			break;
		}

		int dataConnActivity = telephonyManager.getDataState();
		switch (dataConnActivity) {
		case TelephonyManager.DATA_ACTIVITY_DORMANT:
			sb.append("连接活动：连接是积极的，但物理链路是关闭" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_IN:
			sb.append("连接活动：连接的活动：目前接收IP的PPP通信" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_INOUT:
			sb.append("连接活动：连接的活动：目前发送和接收IP PPP通信" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_NONE:
			sb.append("连接活动：连接的活动：无交通" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_OUT:
			sb.append("连接活动：连接的活动：目前发送IP的PPP通信" + ",\n");
			break;
		}
		// 手机网络信息
		sb.append("5.手机网络信息:\n");
		sb.append("有无漫游："
				+ (telephonyManager.isNetworkRoaming() ? "有漫游" : "无漫游") + ",");
		int i = telephonyManager.getNetworkType();
		if (i == TelephonyManager.NETWORK_TYPE_EDGE) {
			sb.append("手机网络类型：EDGE,\n");
		} else if (i == TelephonyManager.NETWORK_TYPE_GPRS) {
			sb.append("手机网络类型：GPRS,\n");
		} else if (i == TelephonyManager.NETWORK_TYPE_CDMA) {
			sb.append("手机网络类型：CDMA,\n");
		}

		// （供应商）SIM卡的状态信息（其他相关的信息）
		sb.append("6.（供应商）SIM卡的状态信息（其他相关的信息）:\n");
		sb.append("手机IMEI：" + telephonyManager.getDeviceId() + ",");
		sb.append("手机版本：" + telephonyManager.getDeviceSoftwareVersion() + ",");
		sb.append("手机SimCountryIso：" + telephonyManager.getSimCountryIso()
				+ ",");
		sb.append("手机SubscriberId：" + telephonyManager.getSubscriberId() + ",");
		sb.append("手机SimOperatorName：" + telephonyManager.getSimOperatorName()
				+ ",");

		String operator = telephonyManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				sb.append("运营商名称：   中国移动,");
			} else if (operator.equals("46001")) {
				sb.append("运营商名称：   中国联通,");
			} else if (operator.equals("46003")) {
				sb.append("运营商名称：    中国电信,");
			}
			sb.append("运营商编号：" + operator + ",\n");
		}

		// 周边设备的信息
		sb.append("7.周边设备的信息:\n");
		List<NeighboringCellInfo> list = telephonyManager
				.getNeighboringCellInfo();
		for (NeighboringCellInfo ll : list) {
			sb.append("其他基站编号:" + ll.getCid() + ",其他基站Lac:" + ll.getLac()
					+ ",其他基站NetworkType：" + ll.getNetworkType() + "，其他基站Psc:"
					+ ll.getPsc() + "，其他基站Rssi:" + ll.getRssi() + "\n");
		}

		// 信号相关
		sb.append("8.信号相关信息:\n");
		sb.append(strsig + "\n");
		sb.append("本次采集的时间:"
				+ new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date(
						System.currentTimeMillis())) + "，");
		sb.append("本次采集时候手机电量是:" + currentBatteryLevel + "%\n");
		sb.append("----------------------------------------------------------------------------------------------------------\n");
		//写入内存卡哦
		File f = new File("/mnt/sdcard/data.txt");
		try {
		if(!f.exists()){
				f.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(f,true);
		fos.write(sb.toString().getBytes());
		fos.flush();
		sb = new StringBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			currentBatteryLevel = intent.getIntExtra("level", 0);
			// intent.putExtra("levelnum", currentBatteryLevel);
			// intent.setAction("com.telin.ui.PhoneGpsActivity");
			// context.sendBroadcast(intent);
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		registerReceiver(batteryReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED)); // 注册一个动作改变事件捕获，这里为电量改变时即ACTION_BATTERY_CHANGED
		if(intent.getIntExtra("status", 0) == 100){
			intent.setAction("com.telin.ui.PhoneGpsActivity");
			intent.putExtra("exit", "测试已经退出，查看/sdcard/data.txt！！");
			sendBroadcast(intent);
			onDestroy();
		}else{
			lon = intent.getIntExtra("minute", 0);
			intent.setAction("com.telin.ui.PhoneGpsActivity");
			intent.putExtra("num","间隔"+lon+"分钟的后台测试已经开启，可以去睡觉喽！！");
			sendBroadcast(intent);
			if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
//				Toast.makeText(this, "SIM卡未找到！！", Toast.LENGTH_SHORT).show();
			} else {
				if (Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {
					mTimer = new Timer(true);
					StartLockWindowTimer();
				} else {
					lon = intent.getIntExtra("minute", 0);
					intent.setAction("com.telin.ui.PhoneGpsActivity");
					intent.putExtra("num", "悲催啦，内存卡不存在哎。。");
					sendBroadcast(intent);
				}
			
			}
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
//		System.exit(0);
		if(mTimerTask!=null){
			mTimerTask.cancel();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
