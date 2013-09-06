package hq.Spin.model;

import hq.Spin.R;

import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

class MyAdapter extends SimpleAdapter {
	private static int myState = -1;
	private int[] appTo;
	private String[] appFrom;
	private ViewBinder appViewBinder;
	private List<? extends Map<String, ?>> appData;
	private int appResource;
	private LayoutInflater appInflater;
	private Context my;

	public int getState() {
		return myState;
	}

	public void setState(int s) {
		myState = s;
	}

	public MyAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.my = context;
		appData = data;
		appResource = resource;
		appFrom = from;
		appTo = to;
		appInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
				// TODO Auto-generated method stub
				Log.i("Force Kill Progress", "" + dataSet.get(from[2]));
				ActivityManager manager = (ActivityManager) my
						.getSystemService(Context.ACTIVITY_SERVICE);
				manager.restartPackage(dataSet.get(from[2]).toString());

				Animation scale = AnimationUtils
						.loadAnimation(my, R.anim.alpha);
				vv.startAnimation(scale);
//				��
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
					/**
					 * �Զ�����������������������ݴ��ݹ����Ŀؼ��Լ�ֵ���������ͣ�
					 * ִ����Ӧ�ķ��������Ը����Լ���Ҫ�������if��䡣���⣬CheckBox��
					 * ������TextView�Ŀؼ�Ҳ�ᱻʶ���TextView�������Ҫ�ж�ֵ������
					 */
					if (v instanceof TextView) {
						// �����TextView�ؼ��������SimpleAdapter�Դ��ķ����������ı�
						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {
						// �����ImageView�ؼ��������Լ�д�ķ���������ͼƬ
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
