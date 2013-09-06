package hq.Spin.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

public class appRunning {
	private static ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

	public static ArrayList<HashMap<String, Object>> getItems() {
		return items;
	}

	public static void setItems(Context context) {
		Log.v("proinfo", "Get Running Tasks");
		items = getRunningTasksInfo(context);
	}

	private static ArrayList<HashMap<String, Object>> getRunningTasksInfo(
			Context context) {
		ArrayList<HashMap<String, Object>> items2 = new ArrayList<HashMap<String, Object>>();
		PackagesInfo pi = new PackagesInfo(context);

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// ��ȡ�������е�Ӧ��
		List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
		// ��ȡ������������������Ҫͨ��������ȡ�����ͼ��ͳ�����
		PackageManager pm = context.getPackageManager();

		for (RunningAppProcessInfo ra : run) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			// ������Ҫ�ǹ���ϵͳ��Ӧ��

			// if(
			// (pi.getInfo(ra.processName).flags&ApplicationInfo.FLAG_SYSTEM)!=0
			// || ra.processName.equals("com.SmartSpin")
			/*
			 * if( ra.processName.equals("com.SmartSpin")
			 * ||ra.processName.equals("com.nd.assistance.ServerService")
			 * ||ra.processName
			 * .equals("com.google.android.apps.maps:FriendService") ){
			 * Log.v("app",ra.processName);
			 * Log.v("loadIcon",""+(pi.getInfo(ra.processName)==null));
			 * //Log.v("loadIcon"
			 * ,pi.getInfo(ra.processName).loadLabel(pm).toString()); continue;
			 * }
			 */
			if (pi.getInfo(ra.processName) == null) {
				// Log.v("app",ra.processName);
				// map.put("icon",
				// context.getResources().getDrawable(R.drawable.appicon));
				// map.put("appName", ra.processName);
				// map.put("processName", ra.processName);
				// items2.add(map);
				continue;
			}

			else if (ra.processName.equals("hq.Spin")
					|| (pi.getInfo(ra.processName).flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				continue;
			}

			else {
				map.put("icon", pi.getInfo(ra.processName).loadIcon(pm));// ͼ��
				map.put("appName", pi.getInfo(ra.processName).loadLabel(pm)
						.toString());// Ӧ�ó�������
				map.put("processName", ra.processName);
				items2.add(map);
			}
		}
		return items2;
	}
	
	
	public static void stopAppRunning(
			Context context) {
		PackagesInfo pi = new PackagesInfo(context);
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
		long  beforeMem = memInfo.getmem_UNUSED(context);
		for (RunningAppProcessInfo ra : run) {
			if (pi.getInfo(ra.processName) == null) {
				continue;
			}
			else if (ra.processName.equals("hq.Spin")
					|| (pi.getInfo(ra.processName).flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
				continue;
			}
			else {
				String[] pkgList= ra.pkgList;
				for(int i =0;i<pkgList.length;i++){
//					2.2�����°汾
					if( android.os.Build.VERSION.SDK_INT <= 8){
						am.restartPackage(pkgList[i]);
					}else{
						System.out.println(pkgList[i]+"--->");
						am.killBackgroundProcesses(pkgList[i]);
					}
				}
			}
		}
		long after = memInfo.getmem_UNUSED(context);
		Toast.makeText(context, "�ɹ��ͷ���"+parse(after-beforeMem)+"M�ڴ棬\n��ǰ�����ڴ�"+parse(after)+"M", 1000).show();
		
	}
	
	
	
	private static String parse(long temp) {
		return new DecimalFormat("0.00").format((double) temp / 1000);
	}
}
