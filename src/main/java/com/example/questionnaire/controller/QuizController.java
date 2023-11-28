package com.example.questionnaire.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.questionnaire.service.ifs.QuizService;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizRequest;
import com.example.questionnaire.vo.QuizResponse;
import com.example.questionnaire.vo.QuizSearchRequest;

@RestController
@CrossOrigin
public class QuizController {

	@Autowired
	private QuizService service ; 
	
	// ��J��Ʈw
	@PostMapping(value = "api/quiz/create")
	public QuizResponse create(@RequestBody QuizRequest request) {
		return service.create(request) ;
	}	
	
	// ��s��Ʈw
	@PostMapping(value = "api/quiz/update")
	public QuizResponse update(@RequestBody QuizRequest request) {
		return service.update(request) ;
	}	
	
	// ��Ʈw�����
	@GetMapping(value = "api/quiz/search")
	public QuizResponse search(@RequestBody QuizSearchRequest request) {
		String title = StringUtils.hasText(request.getTitle()) ? request.getTitle() : "" ;		
		LocalDate startDate =  request.getStartDate() != null ?  request.getStartDate() : LocalDate.of(1971, 1, 1) ;
		LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.of(2099, 12, 31) ;
		return service.search(title, startDate, endDate) ;
	}
	
	// �R���ݨ�
	@PostMapping(value = "api/quiz/deleteQuestionnaire") 
	public QuizResponse deleteQuestionnaire(@RequestBody List<Integer> qnidList) {
		return service.deleteQuestionnaire(qnidList) ;
	}	

	// �R�ݨ����U���D��
	@PostMapping(value = "api/quiz/deleteQuestion")
	public QuizResponse deleteQuestion(@RequestBody int qnId , List<Integer> quIdList) {
		return service.deleteQuestion(qnId, quIdList) ;
	}
	
	 // �j�M�C��̪��Ҧ��ݨ�(boolean�O�n�d�ݬO�_�o��)
	@GetMapping (value="api/quiz/searchQuestionnaireList")
	public QuestionnaireRes searchQuestionnaireList1(
			// @RequestParam �� �[�W��~�౵���e�ݪ����
			// @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) �� �e�ݶǦ^������n�o���ഫ����ݤ~��ݱo�����榡
		@RequestParam(value = "title", required = false, defaultValue = "") String title,
	   	@RequestParam(value = "startDate", required = false)	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate startDate,
	    @RequestParam(value = "endDate", required = false)	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate endDate,
	    @RequestParam(value = "isPublished", required = false) boolean isPublished) {
			// ���]�e�ݶǤJ����Ƭ��ŭȡA���U���w�]�ȱa�J
			title = StringUtils.hasText(title) ? title : "";
			startDate = startDate != null ? startDate : LocalDate.of(1971,01,01);
			endDate = endDate != null ? endDate : LocalDate.of(2099,01,01);
			// ������Ʈw�n�����A��
			return service.searchQuestionnaireList(title, startDate,endDate,isPublished);  

	 // �j�M�C��̪��Ҧ��ݨ�(boolean�O�n�d�ݬO�_�o��)
//	@GetMapping(value = "api/quiz/searchQuestionnaireList1")
//	public QuizResponse searchQuestionnaireList(@RequestBody QuizSearchRequest request, boolean is_published) {
//		String title = StringUtils.hasText(request.getTitle()) ? request.getTitle() : "" ;		
//		LocalDate startDate =  request.getStartDate() != null ?  request.getStartDate() : LocalDate.of(1971, 1, 1) ;
//		LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.of(2099, 12, 31) ;
//		return service.searchQuestionnaireList(title, startDate, endDate, boolean) ;
//	}

	// �j�M�ݨ��̪��Ҧ����D
//	@GetMapping(value = "api/quiz/searchQuestionList")
//	public QuizResponse searchQuestionList(@RequestBody int qnId) {
//		return service.searchQuestionList(qnId) ;
//	}
	

	}


}
