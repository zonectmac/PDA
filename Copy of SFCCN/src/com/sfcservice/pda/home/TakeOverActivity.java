package com.sfcservice.pda.home;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sfcservice.adapter.ListAdapter;
import com.sfcservice.bean.TakeOverInfo;
import com.sfcservice.component.ProDialog;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class TakeOverActivity extends BaseActivity implements OnClickListener {
	private TextView tv_head_title1, tv_head_title2, tv_head_title3;
	private EditText et_take_search;
	private ListView lv_takeOver;
	private List<TakeOverInfo> list;
	private List<TakeOverInfo> searchlist;
	private TakeOverInfo takeInfo;
	HasPickAdapter haspickadapter = null;
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
				MyTool.toastShow2(TakeOverActivity.this, "连接服务器失败");
				break;
			case MyConfig.RESULTS:
				// MyTool.playSuccessSound();
				dialog.dismiss();
				list = new ArrayList<TakeOverInfo>();
				list = MyConnection.getMyConnection().getHasPickup();
				haspickadapter = new HasPickAdapter(TakeOverActivity.this, list);
				for (int i = 0; i < list.size(); i++) {
					System.out.println("============" + list.get(i).toString());
				}
				lv_takeOver.setAdapter(haspickadapter);
				et_take_search.setText("");
				break;
			case MyConfig.RESULTF:
				// 没有未完成的盘点单
				dialog.dismiss();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow2(TakeOverActivity.this, strMsg + "非法访问");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected int getLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_takeover;
	}

	@Override
	protected String getActivityTitle() {
		// TODO Auto-generated method stub
		return "收货";
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getPickInfo("getPickupTableList");
		System.out.println("========onResume=====");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		System.out.println("========onNewIntent=====");
	}

	@Override
	protected void initView() {
		dialog = new ProDialog(this);
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_take_search).setOnClickListener(this);
		et_take_search = (EditText) findViewById(R.id.et_take_search);
		tv_head_title1 = (TextView) findViewById(R.id.tv_head_title1);
		tv_head_title2 = (TextView) findViewById(R.id.tv_head_title2);
		tv_head_title3 = (TextView) findViewById(R.id.tv_head_title3);
		tv_head_title1.setText("客户ID");
		tv_head_title2.setText("锁带编号");
		tv_head_title3.setText("创建时间");
		lv_takeOver = (ListView) findViewById(R.id.lv_takeOver);
		lv_takeOver.setOnItemClickListener(listener);
	}

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
			takeInfo = list.get(position);

			Intent intent = new Intent(TakeOverActivity.this,
					PickDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("takeInfo", takeInfo);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	class HasPickAdapter extends ListAdapter<TakeOverInfo> {

		public HasPickAdapter(Context mContext, List<TakeOverInfo> list) {
			super(mContext, list);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.layout_pick_item, null);
				new HolderView(convertView);
			}
			HolderView holder = (HolderView) convertView.getTag();
			TakeOverInfo toi = getItem(position);
			holder.tv_clientId.setText(toi.getUserCode());
			holder.tv_clientNames_text.setText(toi.getLockCode());
			holder.tv_clientPhones_text.setText(toi.getCreateTime());
			return convertView;
		}

	}

	class HolderView {
		private TextView tv_clientId = null;
		private TextView tv_clientNames_text = null;
		private TextView tv_clientPhones_text = null;

		public HolderView(View v) {
			tv_clientId = (TextView) v.findViewById(R.id.tv_clientId);
			tv_clientNames_text = (TextView) v
					.findViewById(R.id.tv_clientName_text);
			tv_clientPhones_text = (TextView) v
					.findViewById(R.id.tv_clientPhone_text);
			v.setTag(this);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_search:
			searchlist = new ArrayList<TakeOverInfo>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i)
							.getUserCode()
							.contains(
									et_take_search.getText().toString().trim())
							|| list.get(i)
									.getLockCode()
									.contains(
											et_take_search.getText().toString()
													.trim())) {
						searchlist.add(list.get(i));
					}
				}
				if (searchlist.size() > 0) {
					haspickadapter = new HasPickAdapter(TakeOverActivity.this,
							searchlist);
					lv_takeOver.setAdapter(haspickadapter);
					// et_take_search.setText("");
				} else {
					if (!et_take_search.getText().toString().trim().equals("")) {
						Toast.makeText(
								TakeOverActivity.this,
								"没有与"
										+ et_take_search.getText().toString()
												.trim() + "有关的信息",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
			break;
		case R.id.btn_back:
			finish();
			break;
		default:
			break;
		}

	}

	/**
	 * 获取收货列表
	 */
	private void getPickInfo(String action) {
		dialog.show();
		MyConnection.getMyConnection().acceptServer(MyConfig.URL_GET_DELIVERY,
				MyConnection.getMyConnection().writeJsonWithPickInfo(action),
				handler);
	}

}