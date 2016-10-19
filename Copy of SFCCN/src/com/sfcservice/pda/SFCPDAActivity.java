package com.sfcservice.pda;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sfcservice.component.ProDialog;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.SFCPopWindow.BtnClickCallBack;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;
import com.sfcservice.pda.home.PickAcitivity;
import com.sfcservice.pda.home.TakeOverActivity;

public class SFCPDAActivity extends Activity implements OnClickListener {
	private int a = 0;// 点击的哪一个
	/**
	 * 解决在activity刚加载的时候跳转至检测货架
	 */
	private boolean bool = true;
	private TextView tvUserName, tvCode;
	private ProDialog dialog;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				dialog.setTvShow("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				dialog.dismiss();
				MyTool.toastShow(SFCPDAActivity.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				// MyTool.playSuccessSound();
				// 有未完成的盘点单
				dialog.dismiss();
				// ArrayList<CheckBean> listBean = new ArrayList<CheckBean>();
				// MyConnection.getMyConnection().getSKUInfo(listBean);
				// MyConfig.getMyConfig().setListBean(listBean);
				// Intent intent1 = new Intent(SFCPDAActivity.this,
				// SFCCheckAllSKU.class);
				// intent1.putExtra(MyConfig.TAG, MyConnection.getMyConnection()
				// .getWsCode());
				// intent1.putExtra("PDA", true);
				// startActivity(intent1);

				break;
			case MyConfig.RESULTF:
				// MyTool.playFailedSound();
				// 没有未完成的盘点单
				dialog.dismiss();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow(SFCPDAActivity.this, strMsg + "非法访问");
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sfc_home);
		init();
	}

	private void init() {
		bool = true;
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvCode = (TextView) findViewById(R.id.tvCode);
		tvUserName.setText(MyConfig.getMyConfig().getUsers()[0]);
		GridView g = (GridView) findViewById(R.id.SFCGrid);
		g.setAdapter(new MyAdapter());
		tvUserName.setOnClickListener(this);
		dialog = new ProDialog(this);
		tvCode.setText(getVerName(this));
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return MyConfig.SFCHomeItemText.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int myposition = position;
			View v = LayoutInflater.from(SFCPDAActivity.this).inflate(
					R.layout.sfc_home_item, null);

			LinearLayout lineItem = (LinearLayout) v
					.findViewById(R.id.lineItem);
			TextView tv = (TextView) v.findViewById(R.id.item_tv);
			ImageView img = (ImageView) v.findViewById(R.id.item_img);
			img.setImageResource(MyConfig.SFCHomeItemImg[position]);
			tv.setText(MyConfig.SFCHomeItemText[position]);

			lineItem.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus && v.isInTouchMode()) {
						switch (myposition) {
						case 1:
							// SFCStartActivity(SFCDetectionSKU.class);
							SFCStartActivity(TakeOverActivity.class);
							break;
						case 0:
							if (bool) {
								break;
							}
							// SFCStartActivity(ScanMain.class);
							SFCStartActivity(PickAcitivity.class);
							System.out.println("==000fouce=====" + bool);
							break;
						default:
							break;
						}
						bool = false;
					}
				}
			});
			lineItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					bool = false;
					switch (myposition) {
					case 1:
						// SFCStartActivity(SFCDetectionSKU.class);
						SFCStartActivity(TakeOverActivity.class);
						System.out.println("==111click=====" + bool);
						break;
					case 0:
						if (bool) {
							break;
						}
						// SFCStartActivity(ScanMain.class);
						SFCStartActivity(PickAcitivity.class);
						System.out.println("==000click=====" + bool);
						break;
					default:
						break;
					}
				}
			});
			return v;
		}
	}

	// 快捷键功能省去-----------------------------------------
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// System.out.println("------------>");
	// if (keyCode == KeyEvent.KEYCODE_4 || keyCode == KeyEvent.KEYCODE_G) {
	// SFCStartActivity(SFCDetectionShelves.class);
	// return true;
	// }
	// if (keyCode == KeyEvent.KEYCODE_5 || keyCode == KeyEvent.KEYCODE_J) {
	// SFCStartActivity(SFCCutSheetBack.class);
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	private void SFCStartActivity(Class<?> c) {
		Intent intent = new Intent(SFCPDAActivity.this, c);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		bool = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			SFCPopWindow.getSFCPopWindow().show(this, "是否退出",
					findViewById(R.id.line), new BtnClickCallBack() {

						@Override
						public void btnClick() {
							// TODO Auto-generated method stub
							finish();
						}
					});
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		SFCPopWindow.getSFCPopWindow().show(this, "是否退出",
				findViewById(R.id.line), new BtnClickCallBack() {

					@Override
					public void btnClick() {
						// TODO Auto-generated method stub
						finish();
					}
				});
	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	private String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.sfcservice.pda", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "版本号 : " + verName;

	}

	/**
	 * 获取数据
	 */
	public void getData() {
		dialog.show();
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_CHECK,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] {}, null, "getSkuByLastWs"), handler);
	}
}