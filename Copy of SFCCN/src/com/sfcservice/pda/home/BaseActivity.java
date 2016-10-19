package com.sfcservice.pda.home;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sfcservice.pda.App;
import com.sfcservice.pda.R;

public abstract class BaseActivity extends Activity implements OnClickListener {
	protected BaseActivity base;
	private Button btn_back;
	private TextView tv_basetitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		base = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutResID());
	}

	@Override
	protected void onStart() {
		super.onStart();
		App.getAppInstance().addActivity(this);
	}

	protected abstract int getLayoutResID();

	protected abstract String getActivityTitle();

	protected abstract void initView();

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		View activity_base = View.inflate(this, R.layout.activity_base, null);
		RelativeLayout fl_baseContent = (RelativeLayout) activity_base
				.findViewById(R.id.fl_baseContent);
		getLayoutInflater().inflate(layoutResID, fl_baseContent, true);
		super.setContentView(activity_base);
		String title = getActivityTitle();
		if (!TextUtils.isEmpty(title)) {
			btn_back = (Button) findViewById(R.id.btn_back);
			btn_back.setOnClickListener(this);
			tv_basetitle = (TextView) findViewById(R.id.tv_basetitle);
			tv_basetitle.setText(title);
		} else
			findViewById(R.id.rl_Bar).setVisibility(View.GONE);

		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		}
	}
}
