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
	// �R�ݨ�
	public QuizResponse deleteQuestionnaire(List<Integer> qnidList) ;
	// �R�ݨ����U���D��
	public QuizResponse deleteQuestion(int qnId , List<Integer> quIdList) ;

	public QuizResponse search(String title, LocalDate startDate, LocalDate endDate) ;
	
	public QuizResponse searchFuzzy(String title, LocalDate startDate, LocalDate endDate) ;

	// �j�M�C��̪��Ҧ��ݨ�(boolean�O�n�d�ݬO�_�o��)
	public QuestionnaireRes searchQuestionnaireList(String title, LocalDate startDate, LocalDate endDate, boolean is_published) ;
	// �j�M�ݨ��̪��Ҧ����D
	public QuestionRes searchQuestionList(int qnId) ;
}
