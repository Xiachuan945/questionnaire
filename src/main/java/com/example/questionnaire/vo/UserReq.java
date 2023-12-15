package com.example.questionnaire.vo;

import java.util.List;

import com.example.questionnaire.entity.User;

public class UserReq {

	private List<User> userList;

	public UserReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserReq(List<User> userList) {
		super();
		this.userList = userList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

}
