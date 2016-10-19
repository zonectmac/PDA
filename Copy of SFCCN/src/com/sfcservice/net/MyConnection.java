package com.sfcservice.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sfcservice.bean.DetectingBean;
import com.sfcservice.bean.NewProBean;
import com.sfcservice.bean.PickInfo;
import com.sfcservice.bean.ScanBean;
import com.sfcservice.bean.TakeOverInfo;
import com.sfcservice.db.SFCDBAdapter;
import com.sfcservice.pda.config.Base64Coder;
import com.sfcservice.pda.config.MyConfig;

public class MyConnection {
	private String primaryID;
	private static String[] users = new String[3];
	private String strJsonResult = "";
	private JSONObject jo = null;
	private String logisticsId = "1";
	private static SFCDBAdapter dbAdapter;
	private static SQLiteDatabase db;
	private Cursor cursor;
	private List<PickInfo> pickInfoList = null;
	private List<TakeOverInfo> haspickInfoList = null;
	private ArrayList<DetectingBean> listDetecBean = null;
	// private OnShelfBean osbean = null;
	private Bitmap bitmap;
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	/**
	 * 记录上传到了第几条
	 */
	private int position;

	public Bitmap getBitmap() {
		return bitmap;
	}

	/**
	 * 循环调用上传至服务器的方法
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				// 链接服务器成功------------->不做处理
				break;
			case MyConfig.ACCESSF:
				// 链接服务器失败------------->再次连接
				MyConfig.getMyConfig().setStop(true);
				break;
			case MyConfig.RESULTS:
				// 上传成功
				position++;
				break;
			case MyConfig.RESULTF:
				// 上传失败
				position++;
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				break;
			default:
				break;
			}
		}
	};

	private MyConnection() {

	}

	private static MyConnection myConnection;

	public static MyConnection getMyConnection() {
		if (myConnection == null) {
			myConnection = new MyConnection();
		}

		return myConnection;
	}

	public void initDB(Context context) {
		dbAdapter = new SFCDBAdapter(context, "sfc.db", null, 1);
	}

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public String uploadFile(File file, String RequestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);

			if (file != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				sb.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				Log.i("mylog", "code-->" + res);
				Log.e(TAG, "response code:" + res);
				// if(res==200)
				// {
				Log.e(TAG, "request success");
				InputStream input = conn.getInputStream();
				StringBuffer sb1 = new StringBuffer();
				int ss;
				while ((ss = input.read()) != -1) {
					sb1.append((char) ss);
				}
				result = sb1.toString();
				Log.e(TAG, "result : " + result);
				// }
				// else{
				// Log.e(TAG, "request error");
				// }
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void acceptServer(final String sUrl, final String commitData,
			final Handler handler) {
		System.out.println("commit---------------->" + commitData);
		strJsonResult = "";
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setRequestMethod("POST");
					OutputStream os = conn.getOutputStream();
					PrintWriter p = new PrintWriter(os);
					p.print("params=" + commitData);
					// os.flush();
					// os.close();
					p.flush();
					p.close();
					InputStream is = conn.getInputStream();
					handler.sendEmptyMessage(MyConfig.ACCESSS);

					int ch;
					StringBuffer b = new StringBuffer();
					while ((ch = is.read()) != -1) {
						b.append((char) ch);
					}
					is.close();
					conn.disconnect();
					strJsonResult = b.toString();
					System.out.println("---------------------->"
							+ strJsonResult);
					jo = new JSONObject(strJsonResult);
					int status = jo.getInt("status");
					String strMsg = jo.getString("msg");
					Message msg = new Message();
					// 获取数据不正确
					if (status == 0) {
						msg.what = MyConfig.RESULTF;
						handler.sendMessage(msg);
					} else {
						msg.what = MyConfig.RESULTS;
						handler.sendMessage(msg);
					}
					Bundle data = new Bundle();
					data.putString(MyConfig.TAG, strMsg);
					msg.setData(data);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

	/**
	 * 登录传出去的JSON
	 * 
	 * @param user_code
	 * @param user_password
	 * @return
	 */
	public String writeUserJosnObject(String user_code, String user_password) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();
			js.key("header");
			js.object();
			js.key("user_id");
			js.value(user_code);
			js.key("pass_word");
			js.value(user_password);
			js.endObject();
			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception

		}
		return null;
	}

	/**
	 * 获取取件列表传出去的JSON
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public String writeJsonWithPickInfo(String action) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();
			js.key("action");
			js.value(action);
			js.key("header");

			js.object();
			js.key("user_id");
			js.value(users[0]);
			js.key("user_token");
			js.value(users[1]);
			js.key("user_key");
			js.value(users[2]);
			// js.key("warehouseId");
			// js.value(warehouseId);
			js.endObject();
			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 其他情况传出去的JSON
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public String writeJsonWithUserInfo(String[] keys, String[] values,
			String methods) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			js.object();
			js.key("user_id");
			js.value(users[0]);
			js.key("user_token");
			js.value(users[1]);
			js.key("user_key");
			js.value(users[2]);
			// js.key("warehouseId");
			// js.value(warehouseId);
			js.endObject();

			js.key("data");
			js.object();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();

			js.key("action");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 只能用tempuser不然上传的 人(usercode) 不准确
	 * 
	 * @param keys
	 * @param values
	 * @return
	 */
	public String writeJsonWithTempUserInfo(String[] keys, String[] values,
			String methods) {
		try {
			JSONStringer js = new JSONStringer();
			js.object();

			js.key("header");

			String[] tempUsers = MyConfig.getMyConfig().getTemUsers();
			js.object();
			js.key("userCode");
			js.value(tempUsers[0]);
			js.key("token");
			js.value(tempUsers[1]);
			js.key("key");
			js.value(tempUsers[2]);
			js.key("warehouseId");
			js.value(logisticsId);
			js.endObject();

			js.key("data");
			js.object();

			for (int i = 0; i < keys.length; i++) {
				js.key(keys[i]);
				js.value(values[i]);
			}
			js.endObject();

			js.key("methods");
			js.value(methods);

			js.endObject();
			return js.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	/**
	 * 解析tokken与key并存储至本地数据库
	 */
	public void insertUser(String user_login_id) {
		try {
			db = dbAdapter.getWritableDatabase();
			String user_last_update = jo.getString("systime");
			JSONObject joData = jo.getJSONObject("header");
			String user_tokken = Base64Coder.encodeString(joData
					.getString("token"));
			String user_key = Base64Coder.encodeString(joData.getString("key"));
			cursor = db.query("user", null, "user_login_id=?",
					new String[] { user_login_id }, null, null, null);

			ContentValues values = new ContentValues();
			values.put("user_login_id", user_login_id);
			values.put("user_tokken", user_tokken);
			values.put("user_key", user_key);
			values.put("user_last_update", user_last_update);

			if (cursor.getCount() != 0) {
				db.update("user", values, "user_login_id=?",
						new String[] { user_login_id });
			} else {
				db.insert("user", null, values);
			}

			users[0] = user_login_id;
			users[1] = user_tokken;
			users[2] = user_key;
			MyConfig.getMyConfig().setUsers(users);
			cursor.close();
			db.close();
			dbAdapter.close();

		} catch (Exception e) {
			// TODO: handle exception
			if (cursor != null) {
				cursor.close();
			}
			if (db != null)
				db.close();
			if (dbAdapter != null) {
				dbAdapter.close();
			}
		}
	}

	/**
	 * 获取取件信息
	 * 
	 * @return
	 */
	public List<PickInfo> getPickInfoData() {
		pickInfoList = new ArrayList<PickInfo>();
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				String user_code = joList.getJSONObject(i).getString(
						"user_code");
				String contactName = joList.getJSONObject(i).getString(
						"contact");
				String clientPhone = joList.getJSONObject(i).getString(
						"pickup_phone");
				String pickup_class = joList.getJSONObject(i).getString(
						"pickup_class");
				String pickup_address = joList.getJSONObject(i).getString(
						"pickup_address");
				String adminuser_id = joList.getJSONObject(i).getString(
						"adminuser_name");
				String pickup_time = joList.getJSONObject(i).getString(
						"pickup_time");
				String pickup_day = joList.getJSONObject(i).getString(
						"pickup_day");
				String id = joList.getJSONObject(i).getString("id");
				String pick_tag = joList.getJSONObject(i).getString(
						"pickup_tag");

				pickInfoList.add(new PickInfo(user_code, pickup_address,
						contactName, clientPhone, pickup_time, adminuser_id,
						pickup_class, pickup_day, id, pick_tag));
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return pickInfoList;

	}

	/**
	 * 
	 * @return提交成功后的信息
	 */
	public String[] getCommitBack() {
		String[] Commitback = new String[2];
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			Commitback[0] = jo.getString("msg");
			Commitback[1] = jo.getString("msg_id");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return Commitback;
	}

	/**
	 * 获取已提交的数据
	 * 
	 * @return
	 */
	public List<TakeOverInfo> getHasPickup() {
		haspickInfoList = new ArrayList<TakeOverInfo>();
		try {
			JSONObject jo = new JSONObject(strJsonResult);
			JSONArray joList = jo.getJSONArray("data");
			for (int i = 0; i < joList.length(); i++) {
				String user_code = joList.getJSONObject(i).getString(
						"user_code");
				String ptId = joList.getJSONObject(i).getString("pt_id");
				String ptLockNum = joList.getJSONObject(i).getString(
						"pt_lock_code");
				String ptWeight = joList.getJSONObject(i)
						.getString("pt_weight");
				String createTime = joList.getJSONObject(i).getString(
						"pt_create_time");
				String pickupType = joList.getJSONObject(i)
						.getString("pt_type");
				String ptPiece = joList.getJSONObject(i).getString("pt_piece");

				haspickInfoList.add(new TakeOverInfo(ptId, ptLockNum,
						user_code, createTime, ptWeight, ptPiece, pickupType));
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return haspickInfoList;

	}

	/**
	 * 存储数据
	 */
	public void saveData(String table, String[] keys, String[] datas) {
		ContentValues values = new ContentValues();

		for (int i = 0; i < keys.length; i++) {
			values.put(keys[i], datas[i]);
		}

		db = dbAdapter.getWritableDatabase();
		db.insert(table, null, values);

		db.close();
		dbAdapter.close();
	}

	/**
	 * 获得产品上架的全部信息
	 */
	public boolean getNewProductInfo20(ArrayList<NewProBean> list, int STATUS) {
		try {
			db = dbAdapter.getReadableDatabase();
			// 获取产品上架所有信息

			switch (STATUS) {
			case 0:
				cursor = db.query("new_product", null, null, null, null, null,
						null);
				break;
			case 1:
				cursor = db.query("new_product", null, "status=?",
						new String[] { "1" }, null, null, null);
				break;
			case 2:
				cursor = db.query("new_product", null, "status=?",
						new String[] { "2" }, null, null, null);
				break;
			case 3:
				cursor = db.query("new_product", null, "status=?",
						new String[] { "3" }, null, null, null);
				break;

			default:
				break;
			}
			int position = list.size() > 0 ? list.size() : 0;
			if (cursor.moveToPosition(position)) {

				for (int i = 0; i < MyConfig.DB_LOADING; i++) {
					NewProBean bean = new NewProBean();

					bean.setUploadDate(cursor.getString(cursor
							.getColumnIndex("upload_date")));
					bean.setStorageDate(cursor.getString(cursor
							.getColumnIndex("storage_date")));

					bean.setUser(cursor.getString(cursor
							.getColumnIndex("user_login_id")));

					bean.setBoxNum(cursor.getString(cursor
							.getColumnIndex("box_num")));

					bean.setShelfNum(cursor.getString(cursor
							.getColumnIndex("shelf_num")));

					bean.setStatus(cursor.getString(cursor
							.getColumnIndex("status")));

					if (bean.getStatus().equals("3")) {
						bean.setStatus("Uplad fail.");
						bean.setCause(cursor.getString(cursor
								.getColumnIndex("cause")));
					} else if (bean.getStatus().equals("1")) {
						bean.setStatus("Waiting for upload");
					} else if (bean.getStatus().equals("2")) {
						bean.setStatus("Upload Completed");
					}
					list.add(bean);
					if (!cursor.moveToNext()) {
						break;
					}
				}
				closeSFCDB(cursor, db);
				return true;
			}
			closeSFCDB(cursor, db);

		} catch (Exception e) {
			// TODO: handle exception
			closeSFCDB(cursor, db);
		}
		return false;
	}

	/**
	 * 获得产品上架的全部信息
	 */
	public boolean getScanInfo20(ArrayList<ScanBean> list, int STATUS) {
		try {
			db = dbAdapter.getReadableDatabase();
			// 获取产品上架所有信息

			switch (STATUS) {
			case 0:
				cursor = db.query("scan_record", null, null, null, null, null,
						null);
				break;
			case 1:
				cursor = db.query("scan_record", null, "status=?",
						new String[] { "1" }, null, null, null);
				break;
			case 2:
				cursor = db.query("scan_record", null, "status=?",
						new String[] { "2" }, null, null, null);
				break;
			case 3:
				cursor = db.query("scan_record", null, "status=?",
						new String[] { "3" }, null, null, null);
				break;

			default:
				break;
			}
			int position = list.size() > 0 ? list.size() : 0;
			if (cursor.moveToPosition(position)) {

				for (int i = 0; i < MyConfig.DB_LOADING; i++) {
					ScanBean bean = new ScanBean();

					bean.setUploadDate(cursor.getString(cursor
							.getColumnIndex("upload_date")));
					bean.setStorageDate(cursor.getString(cursor
							.getColumnIndex("storage_date")));

					bean.setUser(cursor.getString(cursor
							.getColumnIndex("user_login_id")));

					bean.setUserCode(cursor.getString(cursor
							.getColumnIndex("user_code")));

					bean.setStatus(cursor.getString(cursor
							.getColumnIndex("status")));

					if (bean.getStatus().equals("3")) {
						bean.setStatus("Uplad fail.");
						bean.setCause(cursor.getString(cursor
								.getColumnIndex("cause")));
					} else if (bean.getStatus().equals("1")) {
						bean.setStatus("Waiting for upload");
					} else if (bean.getStatus().equals("2")) {
						bean.setStatus("Upload Completed");
					}
					list.add(bean);
					if (!cursor.moveToNext()) {
						break;
					}
				}
				closeSFCDB(cursor, db);
				return true;
			}
			closeSFCDB(cursor, db);

		} catch (Exception e) {
			// TODO: handle exception
			closeSFCDB(cursor, db);
		}
		return false;
	}

	// 清空已上传的数据
	public void clearData() {
		SQLiteDatabase db = dbAdapter.getWritableDatabase();
		Cursor cursor = db.query("scan_record", null, null, null, null, null,
				null);
		if (cursor.moveToFirst()) {
			do {
				String date = cursor.getString(cursor
						.getColumnIndex("upload_date"));
				String status = cursor.getString(cursor
						.getColumnIndex("status"));
				String id = cursor.getString(cursor.getColumnIndex("_id"));

				if (status.equals("2")) {
					String[] strs = date.split(" ");
					String[] strd = strs[0].split("-");

					int y = Integer.parseInt(strd[0]);
					int m = Integer.parseInt(strd[1]);
					int d = Integer.parseInt(strd[2]);

					if (delete(y, m, d)) {
						db.delete("scan_record", "_id=?", new String[] { id });
					}
				}
			} while (cursor.moveToNext());
		}
		closeSFCDB(cursor, db);
	}

	private boolean delete(int y, int m, int d) {
		Calendar c = Calendar.getInstance();

		int cy = c.get(Calendar.YEAR);
		int cm = c.get(Calendar.MONTH) + 1;
		int cd = c.get(Calendar.DAY_OF_MONTH);

		if (cy == y && cm == m) {
			int i = cd - d;
			if (i >= MyConfig.DELETE_DAY) {
				return true;
			}
			return false;
		}
		// 每个月都算作30天
		if (cy == y && cm != m) {
			if ((cm - m) > 1) {
				return true;
			}
			if ((cm - m == 1)) {
				int i = cd + 30 - d;
				if ((i >= MyConfig.DELETE_DAY)) {
					return true;
				}
			}
			return false;
		}
		if (cy != y) {
			if ((cy - y > 1)) {
				return true;
			}
			if ((cy - y) == 1) {
				if (cm == 1 && m == 12) {
					int i = cd + 30 - d;
					if ((i < MyConfig.DELETE_DAY)) {
						return false;
					}
				}
				return true;
			}
		}

		return false;
	}

	// 获取图片信息
	public void getImg(final String sUrl, final Handler handler) {
		MyConfig.getMyConfig().setBitmap(null);
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);

					InputStream is = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(is);
					handler.sendEmptyMessage(10);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

	public void closeSFCDB(Cursor cursor, SQLiteDatabase db) {
		if (cursor != null) {
			cursor.close();
		}
		if (db != null) {
			db.close();
		}
		if (dbAdapter != null) {
			dbAdapter.close();
		}
	}

	public void update(final String sUrl, final Handler handler) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					URL url = new URL(sUrl);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setConnectTimeout(MyConfig.TIME_OUT);
					conn.setReadTimeout(MyConfig.TIME_OUT);
					InputStream is = conn.getInputStream();

					// int ch;
					// StringBuffer b = new StringBuffer();
					// while ((ch = is.read()) != -1) {
					// b.append((char) ch);
					// }
					// is.close();
					// conn.disconnect();
					// strJsonResult = b.toString();
					// System.out.println("---------------------->"
					// + strJsonResult);

					DocumentBuilderFactory factory = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document doc = builder.parse(is);
					Element root = doc.getDocumentElement();
					String appname = root.getElementsByTagName("appname")
							.item(0).getTextContent();
					String vercode = root.getElementsByTagName("vercode")
							.item(0).getTextContent();
					String apkurl = root.getElementsByTagName("apkurl").item(0)
							.getTextContent();
					is.close();
					conn.disconnect();

					Bundle bundle = new Bundle();
					bundle.putStringArray(MyConfig.TAG, new String[] { appname,
							vercode, apkurl });
					Message msg = new Message();
					msg.what = 30;
					msg.setData(bundle);
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					handler.sendEmptyMessage(MyConfig.ACCESSF);
					return;
				}
			}
		}.start();
	}

}
