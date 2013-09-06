package hq.Spin.service;

import hq.Spin.R;
import hq.Spin.main.ActivityShow;
import hq.Spin.model.ImageUtils;
import hq.Spin.model.Item;
import hq.Spin.model.MyApplication;
import hq.Spin.model.appRunning;
import hq.Spin.model.memInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achartengine.model.TimeSeries;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FloatService extends Service {

	WindowManager wm = null;
	boolean booleais = false;
	private String index = "net.chinamobile.cmccwlan";
	String appname = "�����ֻ�ר��";
	public static TimeSeries series = null;
	WindowManager.LayoutParams wmParams = null;
	private static final int ONGOING_NOTIFICATION = 0x980;
	/**
	 * ������view������һ�����ϵĺ�һ���򵥵�view
	 */
	View view;
	/**
	 * �Զ���adapter
	 */
	MyAdapter sa;
	/**
	 * �����ƶ�X����
	 */
	private float mTouchStartX;
	/**
	 * �����ƶ�Y����
	 */
	private float mTouchStartY;
	/**
	 * ���X����
	 */
	private float x;
	/**
	 * ���Y����
	 */
	private float y;

	public static int width; // �õ����
	public static int height; // �õ��߶�
	/**
	 * ����״̬
	 */
	int state;
	/**
	 * ������״̬���������ϣ�չ�����б�չ�����б�պ�
	 */
	int spin_state;
	/**
	 * ���ڴ��С
	 */
	TextView tx1;
	/**
	 * �����ڴ��С
	 */
	TextView tx,battery;
	/**
	 * �ر�ͼ��
	 */
	ImageView close;
	/**
	 * ��ɫԲȦͼ��
	 */
	ImageView meminfo;
	/**
	 * ����list
	 */
	ImageView list_iv;
	/**
	 * ����ʱ��Բ��Ϊչ��������
	 */
	ImageView spin_start;
	/**
	 * չ��ʱ����Բ�ΰ�͸������
	 */
	ImageView back;
	/**
	 * ����ʱ��Բ�ΰ�͸������
	 */
	ImageView spin_back;
	/**
	 * չ��ʱ��СԲȦͼ��
	 */
	ImageView spin_single;
	// RelativeLayout back_rl;
	/**
	 * �ƶ���ʼ��X����
	 */
	private float StartX;
	/**
	 * �ƶ���ʼ��Y����
	 */
	private float StartY;
	/**
	 * �ڴ����ݲɼ����ʱ��
	 */
	private static int delaytime = 1000;

	// int num;
	/**
	 * �Ƚϵ��ڴ��С����
	 */
	long memdata;
	/**
	 * �Ƚϵ���ʱ�ڴ��С����
	 */
	long tempdata;
	/**
	 * ���ڴ��С����
	 */
	long mem_total;
	/**
	 * ��ȫ�رգ�����Ϣ��ʾ
	 */
	private int SPIN_ON = 0;

	/**
	 * �� ��ʾ�ڴ���Ϣ
	 */
	private int SPIN_OFF = 1;
	/**
	 * ��״̬�£�����list��ʾ
	 */
	private int SPIN_LIST_ON = 2;
	/**
	 * ��״̬�£�����list����ʾ
	 */
	private int SPIN_LIST_OFF = 3;

	private ListView listItem;
	List<ApplicationInfo> pi;
	MyBroadcastReciver myReceiver;
	ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

	Drawable showAppImage = null;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("��������");
		view = LayoutInflater.from(this).inflate(R.layout.floating_small, null);
		wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		wmParams = ((MyApplication) getApplication()).getMywmParams();
		showAppImage = this.getResources().getDrawable(R.drawable.meminfo);
		StartImagineInit();
		// ������ʼ������ͼ
		createView(false);
		// ע��㲥��
				myReceiver = new MyBroadcastReciver();
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("ACTION_SPIN_RUNNING");
				intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
				intentFilter.addAction(Intent.ACTION_SCREEN_ON);
				intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
				intentFilter.addAction("testingspin");
				this.registerReceiver(myReceiver, intentFilter);
	}

	public static void setDelayTime(int i) {
		delaytime = 1000 * i;
	}

	/**
	 * ����ʱ�ĳ�ʼ��
	 */
	public void StartImagineInit() {
		spin_state = SPIN_OFF;
		spin_start = (ImageView) view.findViewById(R.id.spin_start);
		spin_start.setBackgroundDrawable(showAppImage);
		close = (ImageView) view.findViewById(R.id.close);
		close.setVisibility(View.GONE);
		spin_back = (ImageView) view.findViewById(R.id.spin_back);
		spin_single = (ImageView) view.findViewById(R.id.spin_single);

	}

	/**
	 * ���ֹ㲥 </br>SCREEN_OFF ��Ļ�رգ��ر�ѭ������ </br>SCREEN_ON ��Ļ�򿪣���ѭ������
	 * </br>ACTION_SPIN_RUNNING ����list�б仯��ˢ��
	 * 
	 */
	private class MyBroadcastReciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("ACTION_SPIN_RUNNING")) {
				newShow();
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				handler.removeCallbacks(task);
			}

			else if (action.equals(Intent.ACTION_SCREEN_ON)) {
				if (spin_state == SPIN_ON || spin_state == SPIN_LIST_OFF) {
					handler.postDelayed(task, delaytime);
				} else if (spin_state == SPIN_LIST_ON) {
					handler.postDelayed(task, delaytime);
					newShow();
				}
			}else if(action.equals("testingspin")){
				booleais = true;
			}else if(action.equals(Intent.ACTION_BATTERY_CHANGED)){
				int rawlevel = intent.getIntExtra("level", -1);//��õ�ǰ���� 
                int scale = intent.getIntExtra("scale", -1); 
                int level = -1; 
                if (rawlevel >= 0 && scale > 0) { 
                    level = (rawlevel * 100) / scale; 
                } 
                if(battery != null){
                	   battery.setText(level + "%");
                }
                System.out.println("Battery Level Remaining: " + level + "%");
			}
		}
	}

	/**
	 * ��ʾ������
	 */
	private void createView(boolean bool) {
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // �����������������Ͻ�
		SharedPreferences shared = getSharedPreferences("wmParams",
				Activity.MODE_PRIVATE);
		wmParams.x = shared.getInt("wmParamsX", 0);
		wmParams.y = shared.getInt("wmParamsY", 0);
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		wm.addView(view, wmParams);
		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// ��ȡ�����Ļ�����꣬������Ļ���Ͻ�Ϊԭ��
				x = event.getRawX();
				y = event.getRawY() - 25; // 25��ϵͳ״̬���ĸ߶�
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					// ��ȡ���View�����꣬���Դ�View���Ͻ�Ϊԭ��
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;
					updateViewPosition();
					showImg();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});

		spin_start.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				anim_start();
			}
		});

		spin_single.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (spin_state == SPIN_ON) {
					anim_close();
				} else if (spin_state == SPIN_LIST_ON) {
					anim_close();
				} else if (spin_state == SPIN_OFF) {
					anim_start();
				} else if (spin_state == SPIN_LIST_OFF) {
					anim_close();
				}
			}
		});
	}

	/**
	 * �򿪳���list
	 */
	public void anim1() {
		spin_state = SPIN_LIST_ON;
		newShow();
		meminfo.setBackgroundDrawable(showAppImage);
		Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
		Animation alpha = AnimationUtils.loadAnimation(this, R.anim.alpha2);
		meminfo.startAnimation(rotate);
		listItem.startAnimation(alpha);
	}

	/**
	 * �رճ���list
	 */
	public void anim2() {
		spin_state = SPIN_LIST_OFF;
		Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
		Animation alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
		meminfo.setBackgroundDrawable(showAppImage);
		meminfo.startAnimation(rotate);
		listItem.startAnimation(alpha);
		listItem.setVisibility(View.GONE);
	}

	/**
	 * չ��������ʱ�ĳ�ʼ��
	 */
	private void RunImagineInit() {
		spin_single = (ImageView) view.findViewById(R.id.spin_single);
		close = (ImageView) view.findViewById(R.id.close);
//		close.setVisibility(View.GONE);
		meminfo = (ImageView) view.findViewById(R.id.meminfo);
		meminfo.setBackgroundDrawable(showAppImage);
		// meminfo.setVisibility(View.GONE);
		listItem = (ListView) view.findViewById(R.id.applist);
		listItem.setVisibility(View.GONE);

		tx = (TextView) view.findViewById(R.id.memunused);
		battery = (TextView) view.findViewById(R.id.battery);
		tx1 = (TextView) view.findViewById(R.id.memtotal);
		mem_total = memInfo.getmem_UNUSED(this);
		memdata = memInfo.getmem_SELF(this, index);
		if (memdata == 0)
			tx.setText("��");
		else
			tx.setText(""
					+ new DecimalFormat("0.00").format((double) memdata / 1000)
					+ "M");
		tx1.setText(""
				+ new DecimalFormat("0.00").format((double) mem_total / 1000)
				+ "M");
		handler.postDelayed(task, delaytime);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent serviceStop = new Intent();
				// serviceStop.setClass(FloatService.this, FloatService.class);
				// stopService(serviceStop);
				if (memdata == 0) {
					Toast.makeText(FloatService.this, "��Ǹ����ѡ���˿ս���", 1000)
							.show();
					return;
				}
				Intent serviceStop = new Intent();
				serviceStop.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				serviceStop.setClass(FloatService.this, ActivityShow.class);
				startActivity(serviceStop);
				close.setVisibility(View.GONE);
			}
		});

		meminfo.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (spin_state == SPIN_ON || spin_state == SPIN_LIST_OFF) {
					anim1();

				} else if (spin_state == SPIN_LIST_ON) {
					anim2();
				}
			}
		});

	}

	public void anim_start() {
		spin_state = SPIN_ON;
		SharedPreferences shared = getSharedPreferences("wmParams",
				Activity.MODE_PRIVATE);
		// wmParams.x =shared.getInt("wmParamsX", 0);
		// wmParams.y =shared.getInt("wmParamsY", 0);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("wmParamsX", wmParams.x - 90);
		editor.putInt("wmParamsY", wmParams.y);
		editor.commit();

		wm.removeView(view);
		view = LayoutInflater.from(this).inflate(R.layout.floating, null);
		RunImagineInit();
		createView(false);
		Animation alpha_start = AnimationUtils.loadAnimation(this,
				R.anim.alpha_start);
		meminfo.startAnimation(alpha_start);
		meminfo.setVisibility(View.VISIBLE);
	}

	public void anim_close() {
		spin_state = SPIN_OFF;
		handler.removeCallbacks(task);

		SharedPreferences shared = getSharedPreferences("wmParams",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		if (wmParams.x + 50 > FloatService.width / 2) {
			editor.putInt("wmParamsX", FloatService.width);
		} else
			editor.putInt("wmParamsX", 0);
		editor.putInt("wmParamsY", wmParams.y);
		editor.commit();
		wm.removeView(view);
		view = LayoutInflater.from(this).inflate(R.layout.floating_small, null);
		StartImagineInit();
		createView(true);
		Animation spin_close = AnimationUtils.loadAnimation(this, R.anim.spin);
		spin_start.startAnimation(spin_close);
		Intent intent = new Intent();
		intent.setAction("close ActivityShowActivity");
		sendBroadcast(intent);
		if(FloatService.series != null){
			FloatService.series.clear();
		}
		appRunning.stopAppRunning(FloatService.this);
	}
	

	public void showImg() {
		if (Math.abs(x - StartX) < 2.0 && Math.abs(y - StartY) < 2.0
				&& !close.isShown()) {
			if (spin_state == SPIN_OFF) {
				anim_start();
			} else if (spin_state != SPIN_OFF) {
				close.setVisibility(View.VISIBLE);
			}
		} else if (close.isShown()) {
			close.setVisibility(View.GONE);
		}
	}

	public void newShow() {
		items.clear();
		appRunning.setItems(this);
		items = appRunning.getItems();
		listItem.setVisibility(View.VISIBLE);
		sa = new MyAdapter(this, items, R.layout.piitem, new String[] { "icon",
				"appName", "processName" }, new int[] { R.id.appicon,
				R.id.appName, -1 }, handler);
		listItem.setAdapter(sa);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 100) {
				Item items = (Item) msg.obj;
				showAppImage = items.drawable;
				index = items.index;
				appname = items.appname;
				meminfo.setBackgroundDrawable(showAppImage);
			}
		}
	};
	private Runnable task = new Runnable() {
		public void run() {
			dataRefresh();
			handler.postDelayed(this, delaytime);
			wm.updateViewLayout(view, wmParams);
		}
	};

	public void dataRefresh() {
		// ��ȡ�ֻ�ר�ҵ��ڴ��ռ��
		tempdata = memInfo.getmem_SELF(this, index);
		// �н����б���ʾ��
		if (spin_state == SPIN_LIST_ON) {
			// if (Math.abs(tempdata - memdata) > 3000) {
			Intent intent = new Intent();
			intent.setAction("ACTION_SPIN_RUNNING");
			this.sendBroadcast(intent);
			// }
		}

		memdata = tempdata;
		// �������45M�ֻ�ר�Ұ��������氡
		if (memdata >= 45000) {
			tx.setTextColor(Color.rgb(250, 2, 0));
		} else {
			tx.setTextColor(Color.rgb(255, 255, 255));
		}
		mem_total = memInfo.getmem_UNUSED(this);
		if (memdata == 0)
			tx.setText("��");
		else {
			double dd = (double) memdata / 1000;
			tx.setText("" + new DecimalFormat("0.00").format(dd) + "M");
			if(booleais){
				FloatService.series.add(new Date(), dd);
			}
			Intent intent = new Intent();
			intent.setAction("com.telenjoy.sendbrostcast");
			intent.putExtra("memdata", dd);
			intent.putExtra("appname", appname);
			sendBroadcast(intent);
		}
		tx1.setText(""
				+ new DecimalFormat("0.00").format((double) mem_total / 1000)
				+ "M");
	}

	private void updateViewPosition() {
		// ���¸�������λ�ò���
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		stopForeground(true);
		handler.removeCallbacks(task);
		unregisterReceiver(myReceiver);
		SharedPreferences shared = getSharedPreferences("wmParams",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("wmParamsX", wmParams.x);
		editor.putInt("wmParamsY", wmParams.y);
		editor.commit();
		wm.removeView(view);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

class MyAdapter extends SimpleAdapter

{
	private static int myState = -1;
	private int[] appTo;
	private String[] appFrom;
	private ViewBinder appViewBinder;
	private List<? extends Map<String, ?>> appData;
	private int appResource;
	private LayoutInflater appInflater;
	private Context my;
	ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
	private Handler handler = null;

	public int getState() {
		return myState;
	}

	public void setState(int s) {
		myState = s;
	}

	public MyAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to, Handler handler) {
		super(context, data, resource, from, to);
		this.my = context;
		appData = data;
		appResource = resource;
		appFrom = from;
		appTo = to;
		appInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.handler = handler;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent,
				appResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View v;
		if (convertView == null) {
			v = appInflater.inflate(resource, parent, false);
			final int[] to = appTo;
			final int count = to.length;
			final View[] holder = new View[count];
			for (int i = 0; i < count; i++) {
				holder[i] = v.findViewById(to[i]);
			}
			v.setTag(holder);
		} else {
			v = convertView;
		}
		bindView(position, v);
		return v;
	}

	private void bindView(int position, View view) {

		final Map dataSet = appData.get(position);
		if (dataSet == null) {
			return;
		}
		final ViewBinder binder = appViewBinder;
		final View[] holder = (View[]) view.getTag();
		final String[] from = appFrom;
		final int[] to = appTo;
		final int count = to.length;
		final View vv;

		vv = view;
		view.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Message msg = new Message();
				Item item = new Item();
//				Bitmap map = ;
				item.drawable = ImageUtils.drawableToBitmap((Drawable) dataSet.get("icon"));
				item.index = (String) dataSet.get(from[2]);
				item.appname = (String) dataSet.get("appName");
				msg.what = 100;
				msg.obj = item;
				handler.sendMessage(msg);
				// ActivityManager manager = (ActivityManager) my
				// .getSystemService(Context.ACTIVITY_SERVICE);
				// manager.restartPackage(dataSet.get(from[2]).toString());
				Animation scale = AnimationUtils
						.loadAnimation(my, R.anim.alpha);
				vv.startAnimation(scale);
				Intent intent = new Intent();
				intent.setAction("ACTION_SPIN_RUNNING");
				my.sendBroadcast(intent);
				Toast.makeText(my, "��ѡ���˲���" + dataSet.get("appName") + "���ڴ�",
						1000).show();
			}
		});

		for (int i = 0; i < count; i++) {
			final View v = holder[i];
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}
				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, data, text);
				}
				if (!bound) {
					if (v instanceof TextView) {
						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {
						setViewImage((ImageView) v, (Drawable) data);
					} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ "view that can be bounds by this SimpleAdapter");
					}
				}
			}

		}
	}

	public void setViewImage(ImageView v, Drawable value) {
		v.setImageDrawable(value);
	}
	
}
