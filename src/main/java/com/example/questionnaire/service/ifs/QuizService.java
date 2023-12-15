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
	// ���F�e�ݪ��e�x�����D�إͦ����A��
//	public QuizResponse getQuId(int quId);
	// �^�ǰݨ��ƾڦ�DB 
	public QuizResponse setUser(List<User> userList) ;
	// �j�MDB�̪��ݨ��ƾڡA��ܦb�έp�W 
//	public QuizResponse getUser(List<User> userList) ;
	// �j�MDB�̪��ݨ��ƾڡA��ܦb�έp�W 
//	public QuizResponse getUser(int num,String name,LocalDateTime dateTime,String ans) ;
	// �j�MDB�̪��ݨ��ƾڡA��ܦb�έp�W (12/11�s�W)
	public UserRes getUser(int qnId) ;
}
