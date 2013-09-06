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
	 * ��ȡ�����ڴ�
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
	 * ��ȡ�����ڴ�(�����ֻ�ר��)
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
		// ϵͳ�ڴ�
		String path = "/proc/meminfo";
		// �洢������
		String content = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path), 8);
			String line;
			if ((line = br.readLine()) != null) {
				// �ɼ��ڴ���Ϣ
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
		// �ɼ��������ڴ�
		content = content.substring(begin + 1, end).trim();
		// ת��ΪInt��
		mTotal = Integer.parseInt(content);
		return mTotal;
	}
}
