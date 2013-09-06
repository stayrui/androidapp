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
		// �绰��Ϣ���
		telephonyManager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(pStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			if (message.what == 0x100) {
				// ִ������
				StartLockWindowTimer();
			}
		}
	};

	public void StartLockWindowTimer() {
		if (mTimer != null) {
			if (mTimerTask != null) {
				mTimerTask.cancel(); // ��ԭ����Ӷ������Ƴ�
			}
			mTimerTask = new MyTimerTask(); // �½�һ������
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
			strsig = "�ź�ǿ�ȣ�"
					+ String.valueOf(-113 + 2
							* signalStrength.getGsmSignalStrength())
					+ "  dBm    "
					+ String.valueOf(signalStrength.getGsmSignalStrength()
							+ "  asu") + ",�ź�GsmBitErrorRate:"
					+ signalStrength.getGsmBitErrorRate() + ",�ź�CdmaDbm:"
					+ signalStrength.getCdmaDbm() + ",�ź�CdmaEcio:"
					+ signalStrength.getCdmaEcio() + ",�ź�EvdoDbm:"
					+ signalStrength.getEvdoDbm() + ",�ź�EvdoEcio:"
					+ signalStrength.getEvdoEcio() + ",�ź�EvdoSnr:"
					+ signalStrength.getEvdoSnr();
		}

	};

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			// ��������ִ����ϣ�������Ϣ
			sb.append("�ֻ�������Ϣ:�����βɼ�������ǰ����GPS������WIFI������\n");
			// �绰������Ϣ
			sb.append("1.�绰������Ϣ:\n");
			int type = telephonyManager.getPhoneType();
			if ((type == TelephonyManager.PHONE_TYPE_GSM)) {
				gsm = (GsmCellLocation) telephonyManager.getCellLocation();
				sb.append("�ֻ����ͣ�GSM,\n");
				jiZhanMsg(gsm);
			} else if (type == TelephonyManager.PHONE_TYPE_CDMA) {
				sb.append("�ֻ����ͣ�CDMA,\n");
				cdma = (CdmaCellLocation) telephonyManager.getCellLocation();
			} else if (type == TelephonyManager.PHONE_TYPE_NONE) {
				sb.append("�ֻ����ͣ����ź�,\n");
				// Toast.makeText(PhoneGpsActivity.this, "�ֻ����ź�!!",
				// Toast.LENGTH_SHORT).show();
				return;
			}
			Message msg = mHandler.obtainMessage(EVENT_LOCK_WINDOW);
			msg.sendToTarget();
		}

	}

	private void jiZhanMsg(GsmCellLocation cell) {
		// �绰�ĺ���״̬
		sb.append("2.�绰�ĺ���״̬:\n");
		int callStatus = telephonyManager.getCallState();
		if (callStatus == TelephonyManager.CALL_STATE_IDLE) {
			sb.append("�绰�ĺ���״̬:  �ֻ�����,\n");
		} else if (callStatus == TelephonyManager.CALL_STATE_OFFHOOK) {
			sb.append("�绰�ĺ���״̬:  �ӵ绰��,\n");
		} else if (callStatus == TelephonyManager.CALL_STATE_RINGING) {
			sb.append("�绰�ĺ���״̬:  �绰������,\n");
		}
		// �豸��λ����Ϣ
		sb.append("3.�豸��λ����Ϣ:\n");
		int cid = cell.getCid();
		int lac = cell.getLac();
		int mcc = Integer.valueOf(telephonyManager.getNetworkOperator()
				.substring(0, 3));
		int mnc = Integer.valueOf(telephonyManager.getNetworkOperator()
				.substring(3, 5));
		sb.append("��վ��ţ�" + cid + ",");
		sb.append("λ�������룺" + lac + ",");
		sb.append("�ƶ��ͻ������룺" + mcc + ",");
		sb.append("�ƶ�����(����0������1)��" + mnc + ",\n");

		// ����������Ϣ(״̬�ͻ)
		sb.append("4.����������Ϣ(״̬�ͻ):\n");
		int dataConnStatus = telephonyManager.getDataState();
		switch (dataConnStatus) {
		case TelephonyManager.DATA_CONNECTED:
			sb.append("����״̬������" + ",\n");
			break;
		case TelephonyManager.DATA_CONNECTING:
			sb.append("����״̬��Ŀǰ����һ����������" + ",\n");
			break;
		case TelephonyManager.DATA_DISCONNECTED:
			sb.append("����״̬���Ͽ�" + ",\n");
			break;
		case TelephonyManager.DATA_SUSPENDED:
			sb.append("����״̬����ͣ" + ",\n");
			break;
		}

		int dataConnActivity = telephonyManager.getDataState();
		switch (dataConnActivity) {
		case TelephonyManager.DATA_ACTIVITY_DORMANT:
			sb.append("���ӻ�������ǻ����ģ���������·�ǹر�" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_IN:
			sb.append("���ӻ�����ӵĻ��Ŀǰ����IP��PPPͨ��" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_INOUT:
			sb.append("���ӻ�����ӵĻ��Ŀǰ���ͺͽ���IP PPPͨ��" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_NONE:
			sb.append("���ӻ�����ӵĻ���޽�ͨ" + ",\n");
			break;
		case TelephonyManager.DATA_ACTIVITY_OUT:
			sb.append("���ӻ�����ӵĻ��Ŀǰ����IP��PPPͨ��" + ",\n");
			break;
		}
		// �ֻ�������Ϣ
		sb.append("5.�ֻ�������Ϣ:\n");
		sb.append("�������Σ�"
				+ (telephonyManager.isNetworkRoaming() ? "������" : "������") + ",");
		int i = telephonyManager.getNetworkType();
		if (i == TelephonyManager.NETWORK_TYPE_EDGE) {
			sb.append("�ֻ��������ͣ�EDGE,\n");
		} else if (i == TelephonyManager.NETWORK_TYPE_GPRS) {
			sb.append("�ֻ��������ͣ�GPRS,\n");
		} else if (i == TelephonyManager.NETWORK_TYPE_CDMA) {
			sb.append("�ֻ��������ͣ�CDMA,\n");
		}

		// ����Ӧ�̣�SIM����״̬��Ϣ��������ص���Ϣ��
		sb.append("6.����Ӧ�̣�SIM����״̬��Ϣ��������ص���Ϣ��:\n");
		sb.append("�ֻ�IMEI��" + telephonyManager.getDeviceId() + ",");
		sb.append("�ֻ��汾��" + telephonyManager.getDeviceSoftwareVersion() + ",");
		sb.append("�ֻ�SimCountryIso��" + telephonyManager.getSimCountryIso()
				+ ",");
		sb.append("�ֻ�SubscriberId��" + telephonyManager.getSubscriberId() + ",");
		sb.append("�ֻ�SimOperatorName��" + telephonyManager.getSimOperatorName()
				+ ",");

		String operator = telephonyManager.getSimOperator();
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				sb.append("��Ӫ�����ƣ�   �й��ƶ�,");
			} else if (operator.equals("46001")) {
				sb.append("��Ӫ�����ƣ�   �й���ͨ,");
			} else if (operator.equals("46003")) {
				sb.append("��Ӫ�����ƣ�    �й�����,");
			}
			sb.append("��Ӫ�̱�ţ�" + operator + ",\n");
		}

		// �ܱ��豸����Ϣ
		sb.append("7.�ܱ��豸����Ϣ:\n");
		List<NeighboringCellInfo> list = telephonyManager
				.getNeighboringCellInfo();
		for (NeighboringCellInfo ll : list) {
			sb.append("������վ���:" + ll.getCid() + ",������վLac:" + ll.getLac()
					+ ",������վNetworkType��" + ll.getNetworkType() + "��������վPsc:"
					+ ll.getPsc() + "��������վRssi:" + ll.getRssi() + "\n");
		}

		// �ź����
		sb.append("8.�ź������Ϣ:\n");
		sb.append(strsig + "\n");
		sb.append("���βɼ���ʱ��:"
				+ new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date(
						System.currentTimeMillis())) + "��");
		sb.append("���βɼ�ʱ���ֻ�������:" + currentBatteryLevel + "%\n");
		sb.append("----------------------------------------------------------------------------------------------------------\n");
		//д���ڴ濨Ŷ
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
				Intent.ACTION_BATTERY_CHANGED)); // ע��һ�������ı��¼���������Ϊ�����ı�ʱ��ACTION_BATTERY_CHANGED
		if(intent.getIntExtra("status", 0) == 100){
			intent.setAction("com.telin.ui.PhoneGpsActivity");
			intent.putExtra("exit", "�����Ѿ��˳����鿴/sdcard/data.txt����");
			sendBroadcast(intent);
			onDestroy();
		}else{
			lon = intent.getIntExtra("minute", 0);
			intent.setAction("com.telin.ui.PhoneGpsActivity");
			intent.putExtra("num","���"+lon+"���ӵĺ�̨�����Ѿ�����������ȥ˯��ඣ���");
			sendBroadcast(intent);
			if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
//				Toast.makeText(this, "SIM��δ�ҵ�����", Toast.LENGTH_SHORT).show();
			} else {
				if (Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {
					mTimer = new Timer(true);
					StartLockWindowTimer();
				} else {
					lon = intent.getIntExtra("minute", 0);
					intent.setAction("com.telin.ui.PhoneGpsActivity");
					intent.putExtra("num", "���������ڴ濨�����ڰ�����");
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
