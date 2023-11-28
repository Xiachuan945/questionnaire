package com.example.questionnaire.service.ifs;

import java.time.LocalDate;
import java.util.List;

import com.example.questionnaire.vo.QuestionRes;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizRequest;
import com.example.questionnaire.vo.QuizResponse;

public interface QuizService {

	public QuizResponse create(QuizRequest request) ;
	
	public QuizResponse update(QuizRequest request) ;
	// 刪問卷
	public QuizResponse deleteQuestionnaire(List<Integer> qnidList) ;
	// 刪問卷底下的題目
	public QuizResponse deleteQuestion(int qnId , List<Integer> quIdList) ;

	public QuizResponse search(String title, LocalDate startDate, LocalDate endDate) ;
	
	public QuizResponse searchFuzzy(String title, LocalDate startDate, LocalDate endDate) ;

	// 搜尋列表裡的所有問卷(boolean是要查看是否發布)
	public QuestionnaireRes searchQuestionnaireList(String title, LocalDate startDate, LocalDate endDate, boolean is_published) ;
	// 搜尋問卷裡的所有問題
	public QuestionRes searchQuestionList(int qnId) ;
}
