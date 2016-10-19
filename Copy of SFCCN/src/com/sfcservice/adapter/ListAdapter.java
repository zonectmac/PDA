package com.sfcservice.adapter;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class ListAdapter<T> extends BaseAdapter {
	private Context mContext;
	private List<T> list;

	public ListAdapter(Context mContext, List<T> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list == null ? 0 : list.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

}
