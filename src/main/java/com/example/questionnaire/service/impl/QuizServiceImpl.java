package com.example.questionnaire.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.questionnaire.constants.RtnCode;
import com.example.questionnaire.entity.Question;
import com.example.questionnaire.entity.Questionnaire;
import com.example.questionnaire.repoistory.QuestionDao;
import com.example.questionnaire.repoistory.QuestionnaireDao;
import com.example.questionnaire.service.ifs.QuizService;
import com.example.questionnaire.vo.QnQuVo;
import com.example.questionnaire.vo.QuestionRes;
import com.example.questionnaire.vo.QuestionnaireRes;
import com.example.questionnaire.vo.QuizRequest;
import com.example.questionnaire.vo.QuizResponse;
import com.example.questionnaire.vo.QuizVo;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuestionnaireDao qnDao;

	@Autowired
	private QuestionDao quDao;

	// 新增問卷及題目
	// 交易。一張表存多筆資料或跨表儲存時，全成功進DB/全失敗皆不進DB。只能用於public，不能用於private，可直接放於class上
	@Transactional
	@Override
	public QuizResponse create(QuizRequest request) {
		// 檢查參數
		QuizResponse checkResult = checkParam(request);
		if (checkResult != null) {
			return checkResult;
		}
		// 問卷存進DB
		int quId = qnDao.save(request.getQuestionnaire()).getId();
		// 題目為空時(尚未有題目)，參數檢查方法內的題目參數檢查(for迴圈)就不會執行
		List<Question> quList = request.getQuestionList();
		if (quList.isEmpty()) {
			return new QuizResponse(RtnCode.SUCCESSFUL);
		}
		// 不為空時(有題目)，再執行以下操作 → 取得問卷流水號 (set給題目)
		for (Question qu : quList) {
			qu.setQnId(quId);
		}
		// 題目存進DB
		quDao.saveAll(quList);
		return new QuizResponse(RtnCode.SUCCESSFUL);
	}

	// 檢查參數的方法 (抽出來定義一個私有方法)(回傳兩種型態：null→檢查成功 ； QuizRes→沒有東西 -> RtnCode)
	private QuizResponse checkParam(QuizRequest request) {
		// 問卷參數檢查
		Questionnaire qn = request.getQuestionnaire();
		if (!StringUtils.hasText(qn.getTitle()) || !StringUtils.hasText(qn.getDescription())
				|| qn.getStartDate() == null || qn.getEndDate() == null || qn.getStartDate().isAfter(qn.getEndDate())) {
			return new QuizResponse(RtnCode.QUESTIONNAIRE_PARAM_ERROR);
		}
		// 題目參數檢查 ( getQnId() 不用檢查，因還未生成，故無法帶入)
		// 空陣列(尚未有題目) for迴圈不會執行
		List<Question> quList = request.getQuestionList();
		for (Question qu : quList) {
			if (qu.getQuId() <= 0 || !StringUtils.hasText(qu.getqTitle()) || !StringUtils.hasText(qu.getOptionType())
					|| !StringUtils.hasText(qu.getOption())) {
				return new QuizResponse(RtnCode.QUESTION_PARAM_ERROR);
			}
		}
		return null;
	}

	// 編輯問卷及題目
	@Transactional
	@Override
	public QuizResponse update(QuizRequest request) {
		// 檢查參數
		QuizResponse checkResult = checkParam(request);
		if (checkResult != null) {
			return checkResult;
		}
		// 檢查參數 - 問卷流水號
		checkResult = checkQuestionnaireId(request);
		if (checkResult != null) {
			return checkResult;
		}
		// 檢查資料是否存在 (因 JPA save 方法，id 為 PK ，其不存在即新增)
		// 因為後續還要進行操作而直接使用 findById，existById 僅能傳回布林值
		Optional<Questionnaire> qnOp = qnDao.findById(request.getQuestionnaire().getId());
		if (qnOp.isEmpty()) {
			return new QuizResponse(RtnCode.QUESTIONNAIRE_ID_NOT_FOUND);
		}
		// collect deleted_question_id
		List<Integer> deledtQuIdList = new ArrayList<>();
		for (Question qu : request.getDeleteQuestionList()) {
			deledtQuIdList.add(qu.getQuId());
		}
		Questionnaire qn = qnOp.get();
		// 可以進行編輯的條件
		// 1. 尚未發布：is_published == false
		// 2. 已發布但尚未開始進行：is_published == true +當前時間必須小於 start_date
		if (!qn.isPublished() || qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate())) {
			qnDao.save(request.getQuestionnaire());
			quDao.saveAll(request.getQuestionList());
//			if (!deledtQuIdList.isEmpty()) {
//				quDao.deleteAllByQnIdAndQuIdIn(qn.getId(), deledtQuIdList);
//			}
			return new QuizResponse(RtnCode.SUCCESSFUL);
		}
		return new QuizResponse(RtnCode.UPDATE_ERROR);
	}

	// 檢查參數-問卷流水號的方法
	private QuizResponse checkQuestionnaireId(QuizRequest request) {
		// 檢查參數-問卷流水號
		if (request.getQuestionnaire().getId() <= 0) {
			return new QuizResponse(RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
		}
		// 問卷流水號是否與題目對應的 qnId 相同。問卷的 id 因創建後才有Ai 生成之流水號，題目才能 get ，故在更新(編輯題目)時，也需檢查
		List<Question> quList = request.getQuestionList();
		for (Question qu : quList) {
			if (qu.getQnId() != request.getQuestionnaire().getId()) {
				return new QuizResponse(RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
			}
		}
		List<Question> quDelList = request.getDeleteQuestionList();
		for (Question qu : quDelList) {
			if (qu.getQnId() != request.getQuestionnaire().getId()) {
				return new QuizResponse(RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
			}
		}
		return null;
	}

	// 刪除問卷
	@Transactional
	@Override
	public QuizResponse deleteQuestionnaire(List<Integer> qnidList) {
		// findByIdIn 不需檢查 <= 0 或 null，有就有，沒有找到就沒有
		List<Questionnaire> qnList = qnDao.findByIdIn(qnidList);
		List<Integer> idList = new ArrayList<>();
		// 符合條件就刪除
		for (Questionnaire qn : qnList) {
			if (!qn.isPublished() || qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate())) {
//				qnDao.deleteById(qn.getId());
				idList.add(qn.getId());
			}
		}
		// 符合條件不為空時，才連線資料庫進行刪除問卷，並且刪除問卷內的題目
		if (!idList.isEmpty()) {
			qnDao.deleteAllById(idList);
			quDao.deleteAllByQnIdIn(idList);
		}
		return new QuizResponse(RtnCode.SUCCESSFUL);
	}

	// 刪除題目
	@Transactional
	@Override
	public QuizResponse deleteQuestion(int qnId, List<Integer> quIdList) {
		// 找問卷流水號 1 1,2,3
		Optional<Questionnaire> qnOp = qnDao.findById(qnId);
		// 問卷流水號為空時，並不會進行刪除「不存在」的問卷
		if (qnOp.isEmpty()) {
			return new QuizResponse(RtnCode.SUCCESSFUL);
		}
		// 問卷存在時檢查是否符合條件，1. 尚未發布 2. 已發布但尚未開始進行，才可進行題目刪除
		Questionnaire qn = qnOp.get();
		if (!qn.isPublished() || qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate())) {
			quDao.deleteAllByQnIdIn(quIdList);
		}
		return new QuizResponse(RtnCode.SUCCESSFUL);
	}

	// 模糊搜尋方法 1
	@Cacheable(cacheNames = "search",
			// key = #title_#startDate_#endDate (用 .concat() 串接，特殊符號用單引號 ' ' 串接)
			// key = "test_2023-11-10_2023-11-30"
			key = "#title.concat('_').concat(#startDate.toString()).concat('_').concat(#endDate.toString())", unless = "#result.rtnCode.code != 200")
	@Override
	public QuizResponse search(String title, LocalDate startDate, LocalDate endDate) {
//		title = StringUtils.hasText(title) ? title : "" ;		
//		startDate =  startDate != null ?  startDate : LocalDate.of(1971, 1, 1) ;
//		endDate = endDate != null ? endDate : LocalDate.of(2099, 12, 31) ;

		// 撈出搜尋後的問卷列表 (qnList：問卷清單，內容有符合搜尋條件之問卷，不含題目)
		List<Questionnaire> qnList = qnDao
				.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(title, startDate, endDate);
		// 同時撈出問卷內對應的題目
		List<Integer> qnIds = new ArrayList<>(); // qnIds：問卷流水號清單，存放符合條件問卷的流水號
		// 對 qnList 遍歷，取得其問卷的流水號，放入 List(qnIds) ：存放符合條件問卷的流水號
		for (Questionnaire qu : qnList) {
			qnIds.add(qu.getId());
		}
		// 依據題目內的問卷流水號 QnId，放入List(quList)，quList 內含資料庫所
		List<Question> quList = quDao.findAllByQnIdIn(qnIds);
		List<QuizVo> quizVoList = new ArrayList<>();
		// 問卷與題目配對 (第一個 for 迴圈是問卷列表)
		for (Questionnaire qn : qnList) { // 對符合搜尋條件之問卷 (qnList) 遍歷，為了分別將每張問卷分別裝入各自的 vo (組合)
			QuizVo vo = new QuizVo(); // 配對完裝回去 QuizVo ，問卷有幾張，就會有幾個 vo
			vo.setQuestionnaire(qn);
			List<Question> questionList = new ArrayList<>();
			for (Question qu : quList) { // 對 quList (內含資料庫所有題目內的問卷流水號 QnId) 遍歷，為了比對
				if (qu.getQnId() == qn.getId()) { // 比對題目的問卷流水號與符合問卷的流水號相同時，將符合之題目放入 questionList
					questionList.add(qu);
				}
			}
			vo.setQuestionList(questionList); // 比對後的題目清單 set 回 vo
			quizVoList.add(vo);
		}
		return new QuizResponse(quizVoList, RtnCode.SUCCESSFUL);
	}

	// 模糊搜尋方法 2 (分開寫版本- 問卷)
	// 問卷列表 → 進資料庫抓已出版與未出版之問卷
	@Override
	public QuestionnaireRes searchQuestionnaireList(String title, LocalDate startDate, LocalDate endDate,
			boolean isPublished) {
		// title = StringUtils.hasText(title) ? title : "";
		// startDate = startDate != null ? startDate : LocalDate.of(1971, 1, 1);
		// endDate = endDate != null ? endDate : LocalDate.of(2099, 1, 1);
		List<Questionnaire> qnList = new ArrayList<>();
		if (isPublished) {
			qnList = qnDao.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
					title, startDate, endDate);
			return new QuestionnaireRes(qnList, RtnCode.SUCCESSFUL);
		} else {
			qnList = qnDao.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(title, startDate,
					endDate);
			return new QuestionnaireRes(qnList, RtnCode.SUCCESSFUL);
		}
	}
	
//	@Override
//	public QuestionnaireRes searchQuestionnaireList(String title, LocalDate startDate, LocalDate endDate,
//			boolean isAll) {
//		// 搜尋條件
//		title = StringUtils.hasText(title) ? title : "";
//		startDate = startDate != null ? startDate : LocalDate.of(1971, 1, 1);
//		endDate = endDate != null ? endDate : LocalDate.of(2099, 12, 31);
//		List<Questionnaire> qnList = new ArrayList<>();
//		if (!isAll) {
//			qnList = qnDao.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
//					title, startDate, endDate);
//		} else {
//			qnList = qnDao.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(title, startDate,
//					endDate);
//		}
//		return new QuestionnaireRes(qnList, RtnCode.SUCCESSFUL);
//	}

	// 模糊搜尋方法 2 (分開寫版本- 題目)
	// 問卷裡的各個問題
	public QuestionRes searchQuestionList(int qnId) {
		if (qnId <= 0) {
			return new QuestionRes(null, RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
		}
		List<Question> quList = quDao.findAllByQnIdIn(Arrays.asList(qnId));
		return new QuestionRes(quList, RtnCode.SUCCESSFUL);
	}

//	@Override
//	public QuizResponse searchFuzzy(String title, LocalDate startDate, LocalDate endDate) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	// JOIN：利用不同資料表之間欄位的關連性將結合產出 (= search 找尋的結果)
	@Override
	public QuizResponse searchFuzzy(String title, LocalDate startDate, LocalDate endDate) {
		List <QnQuVo> res = qnDao.selectFuzzy(title, startDate, endDate) ;
		return new QuizResponse(null, res, RtnCode.SUCCESSFUL);
	}

}
