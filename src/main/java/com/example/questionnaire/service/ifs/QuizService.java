package com.example.questionnaire.service.ifs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.questionnaire.entity.User;
import com.example.questionnaire.vo.QnQuVo;
import com.example.questionnaire.vo.QuestionRes;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizRequest;
import com.example.questionnaire.vo.QuizResponse;
import com.example.questionnaire.vo.UserRes;

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
	// 為了前端的前台索取題目生成此服務
//	public QuizResponse getQuId(int quId);
	// 回傳問卷數據至DB 
	public QuizResponse setUser(List<User> userList) ;
	// 搜尋DB裡的問卷數據，顯示在統計上 
//	public QuizResponse getUser(List<User> userList) ;
	// 搜尋DB裡的問卷數據，顯示在統計上 
//	public QuizResponse getUser(int num,String name,LocalDateTime dateTime,String ans) ;
	// 搜尋DB裡的問卷數據，顯示在統計上 (12/11新增)
	public UserRes getUser(int qnId) ;
}
