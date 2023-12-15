package com.example.questionnaire.vo;

import java.util.List;

import com.example.questionnaire.constants.RtnCode;
import com.example.questionnaire.entity.User;

public class UserRes {

	private List<User> userList;

	private RtnCode rtnCode;

	public UserRes() {
		super();
	}
	
	public UserRes(List<User> userList) {
		super();
		this.userList = userList;
	}

	public UserRes(List<User> userList, RtnCode rtnCode) {
		super();
		this.userList = userList;
		this.rtnCode = rtnCode;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public RtnCode getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(RtnCode rtnCode) {
		this.rtnCode = rtnCode;
	}
	
}