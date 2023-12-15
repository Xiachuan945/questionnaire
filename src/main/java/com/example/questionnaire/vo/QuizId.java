package com.example.questionnaire.vo;

import java.util.List;

public class QuizId {

	private List<Integer> qnList ;
	
	private List<Integer> quList ;
	
	// 為了前端的前台索取題目生成 int quId
//	private int quId ;
	
	private int qnId ;

	public QuizId() {
		super();
		// TODO Auto-generated constructor stub
	}
	// 為了前端的前台索取題目生成建構方法
//	public QuizId(int quId) {
//		super();
//		this.quId = quId;
//	}
//
//	public int getQuId() {
//		return quId;
//	}
//	public void setQuId(int quId) {
//		this.quId = quId;
//	}
	public QuizId(List<Integer> qnList, List<Integer> quList, int qnId) {
		super();
		this.qnList = qnList;
		this.quList = quList;
		this.qnId = qnId;
	}

	public List<Integer> getQnList() {
		return qnList;
	}

	public void setQnList(List<Integer> qnList) {
		this.qnList = qnList;
	}

	public List<Integer> getQuList() {
		return quList;
	}

	public void setQuList(List<Integer> quList) {
		this.quList = quList;
	}

	public int getQnId() {
		return qnId;
	}

	public void setQnId(int qnId) {
		this.qnId = qnId;
	}

}
