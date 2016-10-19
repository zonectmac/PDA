package com.sfcservice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SFCDBAdapter extends SQLiteOpenHelper {
	public SFCDBAdapter(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// 这里的外键只是让数据不容易删除,并没有扩展数据表;因为下一次登录的时候
		String sql_user = "create table user(user_login_id text primary key not null, user_tokken text not null,user_key text not null,user_last_update text not null)";
		//String sql_cut_sheet_back = "create table cut_sheet_back(_id integer primary key autoincrement,user_login_id text not null,back_num text not null,shelf_num text not null,sku text not null,foreign key(user_login_id) references user(user_login_id));";
		//String sql_binding_shelves = "create table binding_shelves(_id integer primary key autoincrement,user_login_id text not null,sku text not null,shelf_num_new text not null,count text not null,count_confirm text not null,foreign key(user_login_id) references user(user_login_id))";
		// 产品上架有三个状态未上传、已上传、上传失败分别对应status 1 2 3,cause只有在status=3的时候才有
		String sql_scan = "create table scan_record(_id integer primary key autoincrement,user_login_id VARCHAR(32)  NOT NULL,user_code text not null,storage_date text not null,upload_date text not null,status text not null,cause text,foreign key(user_login_id) references user(user_login_id))";
		//String sql_order_pickup = "CREATE TABLE order_pickup (op_id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,op_code vARCHAR(32)  UNIQUE NULL,user_login_id VARCHAR(32)  NOT NULL,op_order_cnt INTEGER DEFAULT 0 NULL,op_product_cnt INTEGER DEFAULT 0 NULL,op_status INTEGER DEFAULT 0 NULL,op_orders_type INTEGER DEFAULT 0 NULL,op_note VARCHAR(64)  NULL,op_create_date DATE DEFAULT CURRENT_TIMESTAMP NULL,op_last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP NULL)";
		//String sql_order_pickup_map = "CREATE TABLE order_pickup_map (opm_id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,op_code VARCHAR(32)  NOT NULL,orders_code VARCHAR(32)  NOT NULL,opm_sort VARCHAR(8)  NULL,product_sku VARCHAR(16)  NULL,opm_note VARCHAR(64)  NULL,ws_code VARCHAR(32)  NOT NULL,opm_status INTEGER DEFAULT 0 NOT NULL,opm_create_date DATE DEFAULT CURRENT_TIMESTAMP NULL,opm_last_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,foreign key(user_login_id) references user(user_login_id))";
		// String sql_stock_transfer_merge =
		// "create table stock_transfer_merge()";
		// String sql_container_shelves_binding =
		// "create table container_shelves_binding()";

		db.execSQL(sql_user);
		//db.execSQL(sql_cut_sheet_back);
		//db.execSQL(sql_binding_shelves);
		db.execSQL(sql_scan);
		//db.execSQL(sql_order_pickup);
		//db.execSQL(sql_order_pickup_map);
		// db.execSQL(sql_stock_transfer_merge);
		// db.execSQL(sql_container_shelves_binding);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
