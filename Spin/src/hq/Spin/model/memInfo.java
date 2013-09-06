package hq.Spin.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Debug;

public class memInfo {

	/**
	 * 获取可用内存
	 * 
	 * @param mContext
	 * @return
	 */
	public static long getmem_UNUSED(Context mContext) {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem / 1024;
	}

	/**
	 * 获取可用内存(天翼手机专家)
	 * 
	 * @param mContext
	 * @return
	 */
	public static long getmem_SELF(Context mContext, String packages) {
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : procInfo) {
			if (runningAppProcessInfo.processName.indexOf(packages) != -1) {
				int pids[] = { runningAppProcessInfo.pid };
				Debug.MemoryInfo self_mi[] = am.getProcessMemoryInfo(pids);
				return (long) self_mi[0].getTotalPss();
			}
		}
		return 0;
	}

	public static long getmem_TOLAL() {
		long mTotal;
		// 系统内存
		String path = "/proc/meminfo";
		// 存储器内容
		String content = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path), 8);
			String line;
			if ((line = br.readLine()) != null) {
				// 采集内存信息
				content = line;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// beginIndex
		int begin = content.indexOf(':');
		// endIndex
		int end = content.indexOf('k');
		// 采集数量的内存
		content = content.substring(begin + 1, end).trim();
		// 转换为Int型
		mTotal = Integer.parseInt(content);
		return mTotal;
	}
}
