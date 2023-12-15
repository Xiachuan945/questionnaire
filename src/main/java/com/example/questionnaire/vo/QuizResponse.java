package com.example.questionnaire.vo;

import java.util.List;

import com.example.questionnaire.constants.RtnCode;
import com.example.questionnaire.entity.Question;
import com.example.questionnaire.entity.Questionnaire;

public class QuizResponse {

//	private List<User> uesrList ;
	
	private List<QuizVo> quizVoList ;
	
	private List<QnQuVo> qnQuVoList ;
	
	private RtnCode rtnCode ;

	public QuizResponse() {
		super();
	}
	
	public QuizResponse(List<QuizVo> quizVoList, List<QnQuVo> qnQuVoList, RtnCode rtnCode) {
		super();
		this.quizVoList = quizVoList;
		this.qnQuVoList = qnQuVoList;
		this.rtnCode = rtnCode;
	}

	public QuizResponse(RtnCode rtnCode) {
		super();
		this.rtnCode = rtnCode;
	}

	public QuizResponse(List<QuizVo> quizVoList, RtnCode rtnCode) {
		super();
		this.quizVoList = quizVoList;
		this.rtnCode = rtnCode;
	}

	public List<QuizVo> getQuizVoList() {
		return quizVoList;
	}

	public void setQuizVoList(List<QuizVo> quizVoList) {
		this.quizVoList = quizVoList;
	}

	public RtnCode getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(RtnCode rtnCode) {
		this.rtnCode = rtnCode;
	}

	public List<QnQuVo> getQnQuVoList() {
		return qnQuVoList;
	}

	public void setQnQuVoList(List<QnQuVo> qnQuVoList) {
		this.qnQuVoList = qnQuVoList;
	}

		
}
