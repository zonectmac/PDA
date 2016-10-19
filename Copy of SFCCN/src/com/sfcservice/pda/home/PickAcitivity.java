package com.sfcservice.pda.home;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sfcservice.adapter.ListAdapter;
import com.sfcservice.bean.PickInfo;
import com.sfcservice.component.ProDialog;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class PickAcitivity extends BaseActivity implements OnClickListener {
	private ListView lv_pick;
	private EditText et_inptutClientId;
	private List<PickInfo> list;// 全部
	private List<PickInfo> unpicklist;// 未取的
	private List<PickInfo> haspicklist;// 已取的
	private List<PickInfo> serachicklist;// 已取的
	PickInfo pickinfo = null;
	PickAdapter pa = null;
	private ProDialog dialog;
	private Spinner sp_pickup;
	boolean isSearch = false;// 解决选中未取点击搜索显示改变了spinner为全部，但结果也为全部的问题
	private static final String[] m = { "未取", "已取", "全部" };
	private ArrayAdapter<String> adapter;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyConfig.ACCESSS:
				dialog.setTvShow("正在检测数据...");
				break;
			case MyConfig.ACCESSF:
				MyTool.playFailedSound();
				dialog.dismiss();
				MyTool.toastShow2(PickAcitivity.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				// MyTool.playSuccessSound();
				dialog.dismiss();
				sp_pickup.setSelection(0);
				list = new ArrayList<PickInfo>();
				unpicklist = new ArrayList<PickInfo>();
				haspicklist = new ArrayList<PickInfo>();
				list = MyConnection.getMyConnection().getPickInfoData();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getPickTag().equals("1")) {
						unpicklist.add(list.get(i));
					} else {
						haspicklist.add(list.get(i));
					}
				}
				if (list.size() == 0) {
					Bundle data = msg.getData();
					String strMsg = data.getString(MyConfig.TAG);
					MyTool.toastShow2(PickAcitivity.this, strMsg);
				}
				pa = new PickAdapter(PickAcitivity.this, unpicklist);
				lv_pick.setAdapter(pa);
				et_inptutClientId.setText("");
				break;
			case MyConfig.RESULTF:
				// 没有未完成的盘点单
				dialog.dismiss();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow2(PickAcitivity.this, strMsg + "非法访问");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected int getLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activit_pick;
	}

	@Override
	protected String getActivityTitle() {
		// TODO Auto-generated method stub
		return "取件";
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getPickInfo("getPickupBillList");
	}

	@Override
	protected void initView() {
		dialog = new ProDialog(this);
		findViewById(R.id.btn_scanPickUp_all).setOnClickListener(this);
		findViewById(R.id.iv_scanClientId).setOnClickListener(this);
		et_inptutClientId = (EditText) findViewById(R.id.et_inptutClientId);
		findViewById(R.id.btn_back).setOnClickListener(this);
		et_inptutClientId.addTextChangedListener(watcherlistenter);
		lv_pick = (ListView) findViewById(R.id.lv_pick);
		lv_pick.setOnItemClickListener(listener);
		sp_pickup = (Spinner) findViewById(R.id.sp_pickup);
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m);

		// 设置下拉列表的风格
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 将adapter 添加到spinner中
		sp_pickup.setAdapter(adapter);
		// 添加事件Spinner事件监听
		sp_pickup.setOnItemSelectedListener(new SpinnerSelectedListener());
	}

	// 使用数组形式操作
	class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (position == 0) {
				pa = new PickAdapter(PickAcitivity.this, unpicklist);
				isSearch = false;
			} else if (position == 1) {
				pa = new PickAdapter(PickAcitivity.this, haspicklist);
				isSearch = false;
			} else {
				if (!isSearch) {
					pa = new PickAdapter(PickAcitivity.this, list);
					isSearch = false;
				}
			}
			lv_pick.setAdapter(pa);
			if (!isSearch) {
				et_inptutClientId.setText("");
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	private TextWatcher watcherlistenter = new TextWatcher() {

		@SuppressLint("DefaultLocale")
		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (hasLower(s)) {
				int index = 0;// 当前光标位置
				if (et_inptutClientId.getSelectionStart() != 0) {
					index = et_inptutClientId.getSelectionStart();
				}
				String temp = s.toString().toUpperCase();// 小写转换大写
				et_inptutClientId.setText(temp);
				et_inptutClientId.setSelection(index);
			}
		}
	};

	private boolean hasLower(CharSequence charSequence) {
		for (int i = 0; i < charSequence.length(); i++) {
			if (charSequence.charAt(i) >= 'a' && charSequence.charAt(i) <= 'z') {
				return true;
			}
		}
		return false;
	}

	private OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			pickinfo = list.get(position);

			Intent intent = new Intent(PickAcitivity.this,
					PickDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("pickinfo", pickinfo);
			intent.putExtras(bundle);
			startActivity(intent);
		}

	};

	class PickAdapter extends ListAdapter<PickInfo> {

		public PickAdapter(Context mContext, List<PickInfo> list) {
			super(mContext, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.layout_pick_item, null);
				new HolderView(convertView);
			}
			HolderView holder = (HolderView) convertView.getTag();
			PickInfo pickinfo = getItem(position);
			holder.tv_clientId.setText(pickinfo.getClientId());
			if (pickinfo.getPickTag().equals("1")) {
				holder.iv_haspick.setVisibility(View.INVISIBLE);
			}
			holder.tv_clientNames_text.setText(pickinfo.getClientName());
			holder.tv_clientPhones_text.setText(pickinfo.getExceptPickTime());
			return convertView;
		}
	}

	class HolderView {
		private TextView tv_clientId;
		private TextView tv_clientNames_text;
		private TextView tv_clientPhones_text;
		private ImageView iv_haspick;

		public HolderView(View v) {
			tv_clientId = (TextView) v.findViewById(R.id.tv_clientId);
			tv_clientNames_text = (TextView) v
					.findViewById(R.id.tv_clientName_text);
			tv_clientPhones_text = (TextView) v
					.findViewById(R.id.tv_clientPhone_text);
			iv_haspick = (ImageView) v.findViewById(R.id.iv_haspick);
			v.setTag(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_scanPickUp_all:
			serachicklist = new ArrayList<PickInfo>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i)
							.getClientId()
							.contains(
									et_inptutClientId.getText().toString()
											.trim())) {
						serachicklist.add(list.get(i));
					}
				}

				if (serachicklist.size() > 0) {
					pa = new PickAdapter(PickAcitivity.this, serachicklist);
					lv_pick.setAdapter(pa);
					isSearch = true;
					sp_pickup.setSelection(2, false);
				} else {
					if (!et_inptutClientId.getText().toString().trim()
							.equals("")) {
						Toast.makeText(
								PickAcitivity.this,
								"没有与"
										+ et_inptutClientId.getText()
												.toString().trim() + "有关的信息",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
			break;
		case R.id.iv_scanClientId:
			Intent it = new Intent(PickAcitivity.this,
					MipcaActivityCapture.class);
			it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(it, 1);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case 1:
			if (data != null) {
				Bundle bundle = data.getExtras();
				// 显示扫描到的内容
				String result = bundle.getString("result");
				if (result != null) {
					if (isExitClientId(result)) {
						Intent intent = new Intent(PickAcitivity.this,
								PickDetailActivity.class);
						Bundle bundle2 = new Bundle();
						bundle2.putSerializable("pickinfo", pickinfo);
						intent.putExtras(bundle2);
						startActivity(intent);
					} else {
						Toast.makeText(PickAcitivity.this, "客户ID不存在",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 判断输入的客户ID是否存在
	 * 
	 * @param id
	 * @return
	 */
	private boolean isExitClientId(String id) {
		for (int i = 0; i < list.size(); i++) {
			if (id.equals(list.get(i).getClientId())) {
				pickinfo = list.get(i);
				return true;
			}
		}
		return false;

	}

	/**
	 * 获取收件人列表
	 */
	private void getPickInfo(String action) {
		dialog.show();
		MyConnection.getMyConnection().acceptServer(MyConfig.URL_GET_DELIVERY,
				MyConnection.getMyConnection().writeJsonWithPickInfo(action),
				handler);
	}

}
