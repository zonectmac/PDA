package com.sfcservice.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.App;
import com.sfcservice.pda.config.MyConfig;

public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	private static final boolean DEBUG = true;

	private static final String PATH = Environment
			.getExternalStorageDirectory().getPath() + "/ryg_test/log/";
	private static final String FILE_NAME = "crash";

	// log�ļ��ĺ�׺��
	private static final String FILE_NAME_SUFFIX = ".trace";

	private static CrashHandler sInstance = new CrashHandler();

	// ϵͳĬ�ϵ��쳣����Ĭ������£�ϵͳ����ֹ��ǰ���쳣����
	private UncaughtExceptionHandler mDefaultCrashHandler;
	private String time;
	private Context mContext;

	// ���췽��˽�У���ֹ�ⲿ������ʵ���������õ���ģʽ
	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return sInstance;
	}

	// ������Ҫ��ɳ�ʼ������
	public void init(Context context) {
		// ��ȡϵͳĬ�ϵ��쳣������
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		// ����ǰʵ����ΪϵͳĬ�ϵ��쳣������
		Thread.setDefaultUncaughtExceptionHandler(this);
		// ��ȡContext�������ڲ�ʹ��
		mContext = context.getApplicationContext();
	}

	/**
	 * �������ؼ��ĺ���������������δ��������쳣��ϵͳ�����Զ�����#uncaughtException����
	 * threadΪ����δ�����쳣���̣߳�exΪδ������쳣���������ex�����ǾͿ��Եõ��쳣��Ϣ��
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			// �����쳣��Ϣ��SD����
			dumpExceptionToSDCard(ex);
			// �������ͨ�������ϴ��쳣��Ϣ�������������ڿ�����Ա������־�Ӷ����bug
			uploadExceptionToServer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ��ӡ����ǰ����ջ��Ϣ
		ex.printStackTrace();

		// ���ϵͳ�ṩ��Ĭ�ϵ��쳣���������򽻸�ϵͳȥ�������ǵĳ��򣬷�����������Լ������Լ�
		if (mDefaultCrashHandler != null) {
			mDefaultCrashHandler.uncaughtException(thread, ex);
		} else {
			Process.killProcess(Process.myPid());
		}
		App.getAppInstance().finishActivity();// ���������е�activity
	}

	private void dumpExceptionToSDCard(Throwable ex) throws IOException {
		// ���SD�������ڻ��޷�ʹ�ã����޷����쳣��Ϣд��SD��
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			if (DEBUG) {
				Log.w(TAG, "sdcard unmounted,skip dump exception");
				return;
			}
		}

		File dir = new File(PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		long current = System.currentTimeMillis();
		time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
				current));
		// �Ե�ǰʱ�䴴��log�ļ�
		File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
		if (!file.exists()) {
			try {
				// ��ָ�����ļ����д����ļ�
				file.createNewFile();
			} catch (Exception e) {
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			PrintWriter pw = new PrintWriter(writer);
			// ���������쳣��ʱ��
			pw.println(time);

			// �����ֻ���Ϣ
			dumpPhoneInfo(pw);

			pw.println();
			// �����쳣�ĵ���ջ��Ϣ
			ex.printStackTrace(pw);
			pw.close();
		} catch (Exception e) {
			Log.e(TAG, "dump crash info failed");
		}

	}

	private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
		// Ӧ�õİ汾���ƺͰ汾��
		PackageManager pm = mContext.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
				PackageManager.GET_ACTIVITIES);
		pw.print("App Version: ");
		pw.print(pi.versionName);
		pw.print('_');
		pw.println(pi.versionCode);

		// android�汾��
		pw.print("OS Version: ");
		pw.print(Build.VERSION.RELEASE);
		pw.print("_");
		pw.println(Build.VERSION.SDK_INT);

		// �ֻ�������
		pw.print("Vendor: ");
		pw.println(Build.MANUFACTURER);

		// �ֻ��ͺ�
		pw.print("Model: ");
		pw.println(Build.MODEL);

		// cpu�ܹ�
		pw.print("CPU ABI: ");
		pw.println(Build.CPU_ABI);
	}

	/**
	 * �ϴ��쳣��������
	 */
	private void uploadExceptionToServer() {
		// TODO Upload Exception Message To Your Web Server
		new ReadFile().readTxtFile(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
		new Thread(runnable).start();

	}

	Runnable runnable = new Runnable() {
		public void run() {
			File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
			String request = MyConnection.getMyConnection().uploadFile(file,
					MyConfig.URL_CRASHUPLOAD);
			Log.i("mylog", "������Ϊ--->" + request);
		}
	};

}
