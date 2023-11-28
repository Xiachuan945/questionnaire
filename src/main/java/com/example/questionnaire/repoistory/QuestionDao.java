package com.example.questionnaire.repoistory;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.questionnaire.entity.Question;
import com.example.questionnaire.entity.QuestionId;

@Repository
public interface QuestionDao extends JpaRepository<Question, QuestionId> {

	public void deleteAllByQnIdIn(List<Integer> qnIdList) ;
	
	public void deleteAllByQnIdAndQuIdIn(int qnId, List<Integer> qnIdList) ;
	
	public List<Question> findByQuIdInAndQnId(List<Integer> qnIdList ,int qnId) ;
	
	public List<Question> findAllByQnIdIn (List<Integer> idList) ;
	
	@Modifying  
	@Transactional
	@Query (value = "INSERT INTO question(id, qn_id, q_title, option_type, is_necessary, q_option)" 
										+ " values ( ?1, ?2, ?3, ?4, ?5, ?6)" , nativeQuery = true)
	public int insert(int id, int qnId, String qTitle, String optionType, boolean isNecessary, String qOption) ;
	
}
