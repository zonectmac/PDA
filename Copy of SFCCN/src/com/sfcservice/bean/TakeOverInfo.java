package com.sfcservice.bean;

import java.io.Serializable;

public class TakeOverInfo implements Serializable {
	private String pickId;
	private String lockCode;// 锁带编号
	private String userCode;// 用户ID
	private String createTime;// 创建时间
	private String weight;// 重量
	private String totalNum;// 件数
	private String pickType;// 类型

	public TakeOverInfo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "TakeOverInfo [pickId=" + pickId + ", lockCode=" + lockCode
				+ ", userCode=" + userCode + ", createTime=" + createTime
				+ ", weight=" + weight + ", totalNum=" + totalNum
				+ ", pickType=" + pickType + "]";
	}

	public TakeOverInfo(String pickId, String lockCode, String userCode,
			String createTime, String weight, String totalNum, String pickType) {
		this.pickId = pickId;
		this.lockCode = lockCode;
		this.userCode = userCode;
		this.createTime = createTime;
		this.weight = weight;
		this.totalNum = totalNum;
		this.pickType = pickType;
	}

	public String getPickType() {
		return pickType;
	}

	public void setPickType(String pickType) {
		this.pickType = pickType;
	}

	public String getPickId() {
		return pickId;
	}

	public void setPickId(String pickId) {
		this.pickId = pickId;
	}

	public String getLockCode() {
		return lockCode;
	}

	public void setLockCode(String lockCode) {
		this.lockCode = lockCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(String totalNum) {
		this.totalNum = totalNum;
	}

}
