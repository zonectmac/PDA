package com.sfcservice.pda.home;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sfcservice.bean.PickInfo;
import com.sfcservice.bean.TakeOverInfo;
import com.sfcservice.component.MyDialogGood;
import com.sfcservice.component.MyDialogGood.Dialogcallback;
import com.sfcservice.component.ProDialog;
import com.sfcservice.net.MyConnection;
import com.sfcservice.pda.R;
import com.sfcservice.pda.SFCPopWindow;
import com.sfcservice.pda.SFCPopWindow.BtnClickCallBack;
import com.sfcservice.pda.config.MyConfig;
import com.sfcservice.pda.config.MyTool;

public class PickDetailActivity extends BaseActivity implements OnClickListener {

	private TextView tv_clientId_text, tv_clientManager, tv_clientName,
			tv_clientPhone, tv_pickupType, tv_title;
	private EditText et_exceptTime, et_pickupAddress, et_totalNum, et_weight,
			tv_lockNum;
	private RelativeLayout relativeLayout1;
	private ArrayAdapter<String> adapter;
	private Spinner spinner;
	private static final String[] m = { "��", "��" };
	private boolean isfirst = true;// �Ƿ��һ�ν���
	private String type = "";
	private Button btn_scanPickDetail;
	private ImageView iv_phone;
	private ProDialog dialog;
	private MyDialogGood logDialog;
	private String[] content = null;
	PickInfo pickinfo = null;
	TakeOverInfo tai = null;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int m = msg.what;
			switch (m) {
			case MyConfig.ACCESSS:
				dialog.setTvShow("���ڼ������...");
				break;
			case MyConfig.ACCESSF:
				dialog.dismiss();
				MyTool.toastShow(PickDetailActivity.this, "���ӷ�����ʧ��");
				break;
			case MyConfig.RESULTS:
				dialog.dismiss();
				content = MyConnection.getMyConnection().getCommitBack();
				// logDialog.setContent(content[0]);
				// logDialog.show();
				MyTool.toastShow2(PickDetailActivity.this, content[0]);
				if (isfirst) {
					if (TextUtils.equals(content[1], "5")
							|| TextUtils.equals(content[1], "9")
							|| TextUtils.equals(content[1], "10")
							|| TextUtils.equals(content[1], "11")) {

					} else {
						et_totalNum.setText("");
						et_weight.setText("");
						tv_lockNum.setText("");
					}
				} else {
					if (TextUtils.equals(content[1], "2")) {
						Intent intent = new Intent(PickDetailActivity.this,
								TakeOverActivity.class);
						startActivity(intent);
					}
				}
				break;
			case MyConfig.RESULTF:
				dialog.dismiss();
				Bundle data = msg.getData();
				String strMsg = data.getString(MyConfig.TAG);
				MyTool.toastShow2(PickDetailActivity.this, strMsg);
				break;

			}
		}
	};

	@Override
	protected int getLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_pickdetail;
	}

	@Override
	protected String getActivityTitle() {
		// TODO Auto-generated method stub
		return "ȡ������";
	}

	@Override
	protected void initView() {
		dialog = new ProDialog(this);
		logDialog = new MyDialogGood(this);
		logDialog.setDialogCallback(dialogcallback);
		iv_phone = (ImageView) findViewById(R.id.iv_phone);
		iv_phone.setOnClickListener(this);
		tv_pickupType = (TextView) findViewById(R.id.tv_pickupType);
		findViewById(R.id.btn_back).setOnClickListener(this);
		tv_clientId_text = (TextView) findViewById(R.id.tv_clientId_text);
		tv_clientManager = (TextView) findViewById(R.id.tv_clientManager);
		tv_clientName = (TextView) findViewById(R.id.tv_clientName);
		btn_scanPickDetail.setOnClickListener(this);
		tv_clientPhone = (TextView) findViewById(R.id.tv_clientPhone);
		tv_clientPhone.setOnClickListener(this);
		et_exceptTime = (EditText) findViewById(R.id.et_exceptTime);
		et_pickupAddress = (EditText) findViewById(R.id.et_pickupAddress);
		et_pickupAddress.setSingleLine(false);
		et_pickupAddress.setHorizontallyScrolling(false);
		Intent intent = this.getIntent();
		pickinfo = (PickInfo) intent.getSerializableExtra("pickinfo");
		tai = (TakeOverInfo) intent.getSerializableExtra("takeInfo");
		if (pickinfo != null) {
			if (pickinfo.getClientPhone().equals("")) {
				iv_phone.setVisibility(View.INVISIBLE);
			}
		}
		tv_title = (TextView) findViewById(R.id.tv_basetitle);
		findViewById(R.id.btn_commit).setOnClickListener(this);
		findViewById(R.id.btn_setAgain).setOnClickListener(this);
		findViewById(R.id.iv_scancode).setOnClickListener(this);
		et_totalNum = (EditText) findViewById(R.id.et_totalNum);
		et_weight = (EditText) findViewById(R.id.et_weight);
		tv_lockNum = (EditText) findViewById(R.id.tv_lockNum);
		relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
		spinner = (Spinner) findViewById(R.id.spinner1);
		// ����ѡ������ArrayAdapter��������
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, m);

		// ���������б�ķ��
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// ��adapter ��ӵ�spinner��
		spinner.setAdapter(adapter);
		// ����¼�Spinner�¼�����
		spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		if (tai != null) {// �ж��Ƿ��ǵ�һ�ν���
			tv_title.setText("ȡ��");
			et_totalNum.setText(tai.getTotalNum());
			et_weight.setText(tai.getWeight());
			tv_lockNum.setText(tai.getLockCode());
			if (tai.getPickType().equals("1")) {
				spinner.setSelection(0, true);
			} else {
				spinner.setSelection(1, true);
			}
			tv_pickupType.setVisibility(View.INVISIBLE);
			relativeLayout1.setVisibility(View.GONE);
			isfirst = false;
		} else {
			isfirst = true;
		}
		SetTextString();
	}

	// ʹ��������ʽ����
	class SpinnerSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (m[position].equals("��")) {
				type = "1";
			} else {
				type = "2";
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	private Dialogcallback dialogcallback = new Dialogcallback() {

		@Override
		public boolean exitActivity() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void btnConfirm() {
			logDialog.dismiss();
			if (isfirst) {
				if (TextUtils.equals(content[1], "5")
						|| TextUtils.equals(content[1], "9")
						|| TextUtils.equals(content[1], "10")
						|| TextUtils.equals(content[1], "11")) {

				} else {
					et_totalNum.setText("");
					et_weight.setText("");
					tv_lockNum.setText("");
				}
			} else {
				if (TextUtils.equals(content[1], "2")) {
					Intent intent = new Intent(PickDetailActivity.this,
							TakeOverActivity.class);
					startActivity(intent);
				}
			}
		}

		@Override
		public void btnCancel() {
			logDialog.dismiss();

		}
	};

	private void SetTextString() {
		if (pickinfo != null) {
			tv_clientId_text.setText(pickinfo.getClientId());
			tv_clientManager.setText(pickinfo.getClientManager());
			tv_clientName.setText(pickinfo.getClientName());
			tv_clientPhone.setText(pickinfo.getClientPhone());
			tv_clientPhone.setTextColor(Color.RED);
			et_pickupAddress.setText(pickinfo.getPickAddress());
			et_exceptTime.setText(pickinfo.getExceptPickTime());
			if (pickinfo.getPickupClass().equals("0")) {
				// et_exceptTime.setText(pickinfo.getPickTime());
				tv_pickupType.setText("����ȡ��");
			} else {
				tv_pickupType.setText("�̶�ȡ��");
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_clientPhone:
			Intent intentcall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ tv_clientPhone.getText().toString()));
			startActivity(intentcall);
			break;
		case R.id.iv_phone:
			Intent intentcall2 = new Intent(Intent.ACTION_CALL,
					Uri.parse("tel:" + tv_clientPhone.getText().toString()));
			startActivity(intentcall2);
			break;
		case R.id.btn_commit:
			if (!tv_lockNum.getText().toString().equals("")) {
				if (Double.valueOf(et_weight.getText().toString()) > 0
						|| et_weight.getText().toString().equals("")) {
					if (!et_totalNum.getText().toString().equals("")) {
						if (Integer.valueOf(et_totalNum.getText().toString()) > 0) {
							if (isfirst) {
								commitScanPick(pickinfo.getId(), "");
							} else {
								commitScanPick("", tai.getPickId());
							}
						} else {
							Toast.makeText(PickDetailActivity.this,
									"�ܼ������������!", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(PickDetailActivity.this, "�ܼ���Ϊ��!",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(PickDetailActivity.this, "�������������!",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(PickDetailActivity.this, "�������Ϊ��!",
						Toast.LENGTH_SHORT).show();

			}
			break;
		case R.id.btn_setAgain:
			et_totalNum.setText("");
			et_weight.setText("");
			tv_lockNum.setText("");
			break;
		case R.id.iv_scancode:
			Intent it = new Intent(PickDetailActivity.this,
					MipcaActivityCapture.class);
			it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(it, 1);
			break;
		case R.id.btn_back:
			finish();
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
				// ��ʾɨ�赽������
				String result = bundle.getString("result");
				if (result != null) {
					tv_lockNum.setText(result);
				}
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isfirst) {
				SFCPopWindow.getSFCPopWindow().show(this, "�Ƿ��˳���ӣ�",
						findViewById(R.id.line_scan), new BtnClickCallBack() {

							@Override
							public void btnClick() {
								// TODO Auto-generated method stub
								finish();
							}
						});
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �ύ
	 */
	private void commitScanPick(String id, String ptid) {
		MyConnection.getMyConnection().acceptServer(
				MyConfig.URL_GET_DELIVERY,
				MyConnection.getMyConnection().writeJsonWithUserInfo(
						new String[] { "pt_type", "pt_weight", "pt_lock_code",
								"id", "pt_piece", "pt_id" },
						new String[] { type, et_weight.getText().toString(),
								tv_lockNum.getText().toString(), id,
								et_totalNum.getText().toString(), ptid },
						"pdaPickupEnter"), handler);

	}

}
