package com.sfcservice.bean;

import java.io.Serializable;

public class PickInfo implements Serializable {
	private String clientId;// 客户ID
	private String pickAddress;// 取货地址
	private String clientName;// 联系人
	private String clientPhone;// 联系电话
	private String exceptPickTime;// 期望取货时间,单次取件
	private String pickTime;// 取货时间，固定取件
	private String clientManager;// 客户经理
	private String pickupClass;// 取件方式，1、固定取件，0、单次取件
	private String id;// id
	private String pickTag;// 1位未取，2为已取

	public PickInfo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "PickInfo [clientId=" + clientId + ", pickAddress="
				+ pickAddress + ", clientName=" + clientName + ", clientPhone="
				+ clientPhone + ", exceptPickTime=" + exceptPickTime
				+ ", pickTime=" + pickTime + ", clientManager=" + clientManager
				+ ", pickupClass=" + pickupClass + ", id=" + id + ", pickTag="
				+ pickTag + "]";
	}

	public String getPickTag() {
		return pickTag;
	}

	public void setPickTag(String pickTag) {
		this.pickTag = pickTag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPickTime() {
		return pickTime;
	}

	public void setPickTime(String pickTime) {
		this.pickTime = pickTime;
	}

	public String getPickupClass() {
		return pickupClass;
	}

	public void setPickupClass(String pickupClass) {
		this.pickupClass = pickupClass;
	}

	public PickInfo(String clientId, String pickAddress, String clientName,
			String clientPhone, String exceptPickTime, String clientManager,
			String pickupClass, String pickTime, String id, String pickTag) {
		this.clientId = clientId;
		this.pickAddress = pickAddress;
		this.clientName = clientName;
		this.clientPhone = clientPhone;
		this.exceptPickTime = exceptPickTime;
		this.clientManager = clientManager;
		this.pickupClass = pickupClass;
		this.pickTime = pickTime;
		this.id = id;
		this.pickTag = pickTag;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPickAddress() {
		return pickAddress;
	}

	public void setPickAddress(String pickAddress) {
		this.pickAddress = pickAddress;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientPhone() {
		return clientPhone;
	}

	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}

	public String getExceptPickTime() {
		return exceptPickTime;
	}

	public void setExceptPickTime(String exceptPickTime) {
		this.exceptPickTime = exceptPickTime;
	}

	public String getClientManager() {
		return clientManager;
	}

	public void setClientManager(String clientManager) {
		this.clientManager = clientManager;
	}

}
