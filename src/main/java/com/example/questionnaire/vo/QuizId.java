package com.example.questionnaire.vo;

import java.util.List;

public class QuizId {

	private List<Integer> qnList ;
	
	private List<Integer> quList ;
	
	// ���F�e�ݪ��e�x�����D�إͦ� int quId
//	private int quId ;
	
	private int qnId ;

	public QuizId() {
		super();
		// TODO Auto-generated constructor stub
	}
	// ���F�e�ݪ��e�x�����D�إͦ��غc��k
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
