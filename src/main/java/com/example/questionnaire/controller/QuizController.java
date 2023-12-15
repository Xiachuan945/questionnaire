package com.example.questionnaire.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.questionnaire.entity.User;
import com.example.questionnaire.service.ifs.QuizService;
import com.example.questionnaire.vo.QuestionRes;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizId;
import com.example.questionnaire.vo.QuizRequest;
import com.example.questionnaire.vo.QuizResponse;
import com.example.questionnaire.vo.QuizSearchRequest;
import com.example.questionnaire.vo.UserReq;
import com.example.questionnaire.vo.UserRes;

@RestController
@CrossOrigin
public class QuizController {

	@Autowired
	private QuizService service ; 
	
	// 放入資料庫
	@PostMapping(value = "api/quiz/create")
	public QuizResponse create(@RequestBody QuizRequest request) {
		return service.create(request) ;
	}	
	
	// 更新資料庫
	@PostMapping(value = "api/quiz/update")
	public QuizResponse update(@RequestBody QuizRequest request) {
		return service.update(request) ;
	}	
	
	// 資料庫拿資料
	@GetMapping(value = "api/quiz/search")
	public QuizResponse search(@RequestBody QuizSearchRequest request) {
		String title = StringUtils.hasText(request.getTitle()) ? request.getTitle() : "" ;		
		LocalDate startDate =  request.getStartDate() != null ?  request.getStartDate() : LocalDate.of(1971, 1, 1) ;
		LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.of(2099, 12, 31) ;
		return service.search(title, startDate, endDate) ;
	}
	
	// 資料庫拿前端問卷對應的 quId 資料 (自建方法)
//	@GetMapping(value = "api/quiz/getQuId")
//	public QuizResponse getQuId(@RequestBody QuizId quizid) {
//		return service.getQuId(quizid.getQuId()) ;
//	}
	
	// 刪除問卷
//	@PostMapping(value = "api/quiz/deleteQuestionnaire") 
//	public QuizResponse deleteQuestionnaire(@RequestBody List<Integer> qnidList) {
//		return service.deleteQuestionnaire(qnidList) ;
//	}	
	
	// 刪除問卷 (另建 QuizId 設定 qnList. quList . qnId )
	@PostMapping(value = "api/quiz/deleteQuestionnaire") 
	public QuizResponse deleteQuestionnaire(@RequestBody QuizId quizid) {
		return service.deleteQuestionnaire(quizid.getQnList()) ;
	}	

	// 刪問卷底下的題目
//	@PostMapping(value = "api/quiz/deleteQuestion")
//	public QuizResponse deleteQuestion(@RequestBody int qnId , List<Integer> quIdList) {
//		return service.deleteQuestion(qnId, quIdList) ;
//	}
	
	// 刪問卷底下的題目 (另建 QuizId 設定 qnList. quList . qnId )
	@PostMapping(value = "api/quiz/deleteQuestion")
	public QuizResponse deleteQuestion(@RequestBody QuizId quizid) {
		return service.deleteQuestion(quizid.getQnId(), quizid.getQuList()) ;
	}
	
	 // 搜尋列表裡的所有問卷(boolean是要查看是否發布)
	@GetMapping (value="api/quiz/searchQuestionnaireList")
	public QuestionnaireRes searchQuestionnaireList1(
			// @RequestParam → 加上後才能接受前端的資料
			// @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) → 前端傳回之日期要這個轉換成後端才能看得懂的格式
		@RequestParam(value = "title", required = false, defaultValue = "") String title,
	   	@RequestParam(value = "startDate", required = false)	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate startDate,
	    @RequestParam(value = "endDate", required = false)	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate endDate,
	    @RequestParam(value = "isPublished", required = false) boolean isPublished) {
			// 假設前端傳入的資料為空值，底下為預設值帶入
			title = StringUtils.hasText(title) ? title : "";
			startDate = startDate != null ? startDate : LocalDate.of(1971,01,01);
			endDate = endDate != null ? endDate : LocalDate.of(2099,01,01);
			// 執行對資料庫要做的服務
			return service.searchQuestionnaireList(title, startDate,endDate,isPublished);  
	}

	 // 搜尋列表裡的所有問卷(boolean是要查看是否發布)
//	@GetMapping(value = "api/quiz/searchQuestionnaireList1")
//	public QuizResponse searchQuestionnaireList(@RequestBody QuizSearchRequest request, boolean is_published) {
//		String title = StringUtils.hasText(request.getTitle()) ? request.getTitle() : "" ;		
//		LocalDate startDate =  request.getStartDate() != null ?  request.getStartDate() : LocalDate.of(1971, 1, 1) ;
//		LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.of(2099, 12, 31) ;
//		return service.searchQuestionnaireList(title, startDate, endDate, boolean) ;
//	}

	 // 搜尋問卷裡的所有問題 (12/3將public後的QuizResponse改為QuestionRes)
	@GetMapping(value = "api/quiz/searchQuestionList")
	public QuestionRes searchQuestionList(@RequestParam int qnId) {
	    return service.searchQuestionList(qnId);
	}
	
	// 回傳問卷數據至DB (12/10修改)
	@PostMapping(value = "api/quiz/setUser")
	public QuizResponse setUser(@RequestBody  UserReq userList) {
		return service.setUser(userList.getUserList()) ;
	}
	
	// 搜尋DB裡的問卷數據，顯示在統計上 
	@GetMapping(value = "api/quiz/getUser")
	public UserRes getUser(@RequestParam  int qnId) {
	    return service.getUser(qnId);
	}

}
