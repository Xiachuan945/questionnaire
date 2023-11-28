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

	// �s�W�ݨ����D��
	// ����C�@�i��s�h����Ʃθ���x�s�ɡA�����\�iDB/�����ѬҤ��iDB�C�u��Ω�public�A����Ω�private�A�i�������class�W
	@Transactional
	@Override
	public QuizResponse create(QuizRequest request) {
		// �ˬd�Ѽ�
		QuizResponse checkResult = checkParam(request);
		if (checkResult != null) {
			return checkResult;
		}
		// �ݨ��s�iDB
		int quId = qnDao.save(request.getQuestionnaire()).getId();
		// �D�ج��Ů�(�|�����D��)�A�Ѽ��ˬd��k�����D�ذѼ��ˬd(for�j��)�N���|����
		List<Question> quList = request.getQuestionList();
		if (quList.isEmpty()) {
			return new QuizResponse(RtnCode.SUCCESSFUL);
		}
		// �����Ů�(���D��)�A�A����H�U�ާ@ �� ���o�ݨ��y���� (set���D��)
		for (Question qu : quList) {
			qu.setQnId(quId);
		}
		// �D�ئs�iDB
		quDao.saveAll(quList);
		return new QuizResponse(RtnCode.SUCCESSFUL);
	}

	// �ˬd�Ѽƪ���k (��X�өw�q�@�Өp����k)(�^�Ǩ�ث��A�Gnull���ˬd���\ �F QuizRes���S���F�� -> RtnCode)
	private QuizResponse checkParam(QuizRequest request) {
		// �ݨ��Ѽ��ˬd
		Questionnaire qn = request.getQuestionnaire();
		if (!StringUtils.hasText(qn.getTitle()) || !StringUtils.hasText(qn.getDescription())
				|| qn.getStartDate() == null || qn.getEndDate() == null || qn.getStartDate().isAfter(qn.getEndDate())) {
			return new QuizResponse(RtnCode.QUESTIONNAIRE_PARAM_ERROR);
		}
		// �D�ذѼ��ˬd ( getQnId() �����ˬd�A�]�٥��ͦ��A�G�L�k�a�J)
		// �Ű}�C(�|�����D��) for�j�餣�|����
		List<Question> quList = request.getQuestionList();
		for (Question qu : quList) {
			if (qu.getQuId() <= 0 || !StringUtils.hasText(qu.getqTitle()) || !StringUtils.hasText(qu.getOptionType())
					|| !StringUtils.hasText(qu.getOption())) {
				return new QuizResponse(RtnCode.QUESTION_PARAM_ERROR);
			}
		}
		return null;
	}

	// �s��ݨ����D��
	@Transactional
	@Override
	public QuizResponse update(QuizRequest request) {
		// �ˬd�Ѽ�
		QuizResponse checkResult = checkParam(request);
		if (checkResult != null) {
			return checkResult;
		}
		// �ˬd�Ѽ� - �ݨ��y����
		checkResult = checkQuestionnaireId(request);
		if (checkResult != null) {
			return checkResult;
		}
		// �ˬd��ƬO�_�s�b (�] JPA save ��k�Aid �� PK �A�䤣�s�b�Y�s�W)
		// �]�������٭n�i��ާ@�Ӫ����ϥ� findById�AexistById �ȯ�Ǧ^���L��
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
		// �i�H�i��s�誺����
		// 1. �|���o���Gis_published == false
		// 2. �w�o�����|���}�l�i��Gis_published == true +��e�ɶ������p�� start_date
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

	// �ˬd�Ѽ�-�ݨ��y��������k
	private QuizResponse checkQuestionnaireId(QuizRequest request) {
		// �ˬd�Ѽ�-�ݨ��y����
		if (request.getQuestionnaire().getId() <= 0) {
			return new QuizResponse(RtnCode.QUESTIONNAIRE_ID_PARAM_ERROR);
		}
		// �ݨ��y�����O�_�P�D�ع����� qnId �ۦP�C�ݨ��� id �]�Ыث�~��Ai �ͦ����y�����A�D�ؤ~�� get �A�G�b��s(�s���D��)�ɡA�]���ˬd
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

	// �R���ݨ�
	@Transactional
	@Override
	public QuizResponse deleteQuestionnaire(List<Integer> qnidList) {
		// findByIdIn �����ˬd <= 0 �� null�A���N���A�S�����N�S��
		List<Questionnaire> qnList = qnDao.findByIdIn(qnidList);
		List<Integer> idList = new ArrayList<>();
		// �ŦX����N�R��
		for (Questionnaire qn : qnList) {
			if (!qn.isPublished() || qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate())) {
//				qnDao.deleteById(qn.getId());
				idList.add(qn.getId());
			}
		}
		// �ŦX���󤣬��ŮɡA�~�s�u��Ʈw�i��R���ݨ��A�åB�R���ݨ������D��
		if (!idList.isEmpty()) {
			qnDao.deleteAllById(idList);
			quDao.deleteAllByQnIdIn(idList);
		}
		return new QuizResponse(RtnCode.SUCCESSFUL);
	}

	// �R���D��
	@Transactional
	@Override
	public QuizResponse deleteQuestion(int qnId, List<Integer> quIdList) {
		// ��ݨ��y���� 1 1,2,3
		Optional<Questionnaire> qnOp = qnDao.findById(qnId);
		// �ݨ��y�������ŮɡA�ä��|�i��R���u���s�b�v���ݨ�
		if (qnOp.isEmpty()) {
			return new QuizResponse(RtnCode.SUCCESSFUL);
		}
		// �ݨ��s�b���ˬd�O�_�ŦX����A1. �|���o�� 2. �w�o�����|���}�l�i��A�~�i�i���D�اR��
		Questionnaire qn = qnOp.get();
		if (!qn.isPublished() || qn.isPublished() && LocalDate.now().isBefore(qn.getStartDate())) {
			quDao.deleteAllByQnIdIn(quIdList);
		}
		return new QuizResponse(RtnCode.SUCCESSFUL);
	}

	// �ҽk�j�M��k 1
	@Cacheable(cacheNames = "search",
			// key = #title_#startDate_#endDate (�� .concat() �걵�A�S��Ÿ��γ�޸� ' ' �걵)
			// key = "test_2023-11-10_2023-11-30"
			key = "#title.concat('_').concat(#startDate.toString()).concat('_').concat(#endDate.toString())", unless = "#result.rtnCode.code != 200")
	@Override
	public QuizResponse search(String title, LocalDate startDate, LocalDate endDate) {
//		title = StringUtils.hasText(title) ? title : "" ;		
//		startDate =  startDate != null ?  startDate : LocalDate.of(1971, 1, 1) ;
//		endDate = endDate != null ? endDate : LocalDate.of(2099, 12, 31) ;

		// ���X�j�M�᪺�ݨ��C�� (qnList�G�ݨ��M��A���e���ŦX�j�M���󤧰ݨ��A���t�D��)
		List<Questionnaire> qnList = qnDao
				.findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(title, startDate, endDate);
		// �P�ɼ��X�ݨ����������D��
		List<Integer> qnIds = new ArrayList<>(); // qnIds�G�ݨ��y�����M��A�s��ŦX����ݨ����y����
		// �� qnList �M���A���o��ݨ����y�����A��J List(qnIds) �G�s��ŦX����ݨ����y����
		for (Questionnaire qu : qnList) {
			qnIds.add(qu.getId());
		}
		// �̾��D�ؤ����ݨ��y���� QnId�A��JList(quList)�AquList ���t��Ʈw��
		List<Question> quList = quDao.findAllByQnIdIn(qnIds);
		List<QuizVo> quizVoList = new ArrayList<>();
		// �ݨ��P�D�ذt�� (�Ĥ@�� for �j��O�ݨ��C��)
		for (Questionnaire qn : qnList) { // ��ŦX�j�M���󤧰ݨ� (qnList) �M���A���F���O�N�C�i�ݨ����O�ˤJ�U�۪� vo (�զX)
			QuizVo vo = new QuizVo(); // �t�粒�˦^�h QuizVo �A�ݨ����X�i�A�N�|���X�� vo
			vo.setQuestionnaire(qn);
			List<Question> questionList = new ArrayList<>();
			for (Question qu : quList) { // �� quList (���t��Ʈw�Ҧ��D�ؤ����ݨ��y���� QnId) �M���A���F���
				if (qu.getQnId() == qn.getId()) { // ����D�ت��ݨ��y�����P�ŦX�ݨ����y�����ۦP�ɡA�N�ŦX���D�ة�J questionList
					questionList.add(qu);
				}
			}
			vo.setQuestionList(questionList); // ���᪺�D�زM�� set �^ vo
			quizVoList.add(vo);
		}
		return new QuizResponse(quizVoList, RtnCode.SUCCESSFUL);
	}

	// �ҽk�j�M��k 2 (���}�g����- �ݨ�)
	// �ݨ��C�� �� �i��Ʈw��w�X���P���X�����ݨ�
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
//		// �j�M����
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

	// �ҽk�j�M��k 2 (���}�g����- �D��)
	// �ݨ��̪��U�Ӱ��D
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

	// JOIN�G�Q�Τ��P��ƪ�����쪺���s�ʱN���X���X (= search ��M�����G)
	@Override
	public QuizResponse searchFuzzy(String title, LocalDate startDate, LocalDate endDate) {
		List <QnQuVo> res = qnDao.selectFuzzy(title, startDate, endDate) ;
		return new QuizResponse(null, res, RtnCode.SUCCESSFUL);
	}

}
