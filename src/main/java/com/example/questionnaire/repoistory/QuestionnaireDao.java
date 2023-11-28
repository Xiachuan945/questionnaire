package com.example.questionnaire.repoistory;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.questionnaire.entity.Questionnaire;
import com.example.questionnaire.vo.QnQuVo;

@Repository
public interface QuestionnaireDao extends JpaRepository<Questionnaire, Integer> {

		/** 
		 * ���o�̷s�@����ơG����������ƫ�˧ǡA�̫᪺������Ʒ|�ܦ��Ĥ@��
		 **/
		public Questionnaire findTopByOrderByIdDesc() ;
		
		public List<Questionnaire> findByIdIn(List<Integer> idList) ;
		
		public List<Questionnaire> findByIdInAndPublishedFalse(List<Integer> idList) ;

//		public List<Questionnaire> QuestionnaireRes(String title, LocalDate startDate, LocalDate endDate) ;

		public List<Questionnaire> findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
				String title, LocalDate startDate, LocalDate endDate);
		// publish �O true �� �w�o��
		public List<Questionnaire> findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
				String title, LocalDate startDate, LocalDate endDate);
		
		// DML ��ƾާ@�y�� -> INSRET INTO (�s�W) �APK�s�b���ܵL�k insert
		@Modifying               // insert �M update ���n�[�W�o��� (insert ���� 1 )
		@Transactional        // insert �M update ���n�[�W�o��� (insert ���� 1 )
		@Query(value = "INSERT INTO questionnaire (title, description, is_published, start_date, end_date)  "
											+ " values ( :title, :desp, :isPublished, :startDate, :endDate)",      // value��N�JParam ���� ( : + @Param�᪺�r��)
											nativeQuery = true)   //  �̫�n�[�W nativeQuery = true (insert ���� 2 )
		public int insert(
				@Param("title") String title, //
				@Param("desp") String descrption, //
				@Param("isPublished") boolean isPublished,  //
				@Param("startDate") LocalDate startDate, //
				@Param("endDate") LocalDate endDate) ;
		
		@Modifying               
		@Transactional        
		@Query(value = "INSERT INTO questionnaire (title, description, is_published, start_date, end_date) "
											+ " values ( ?1, ?2, ?3, ?4, ?5)",      // �� ?+��m  �P�U������m������
											nativeQuery = true)  
		public int insertData(
				String title, 
				String descrption, 
				boolean isPublished,  
				LocalDate startDate, 
				LocalDate endDate) ;
		
		// DML ��ƾާ@�y�� -> UPDATE (��s) 
		@Modifying               // insert �M update ���n�[�W�o��� (update ���� )
		@Transactional        // insert �M update ���n�[�W�o��� (update ���� )
		@Query (value = "UPDATE questionnaire set title = :title, description = :desp"      // update + table �P��ƪ����
											+"where id = :id" , nativeQuery = true)  // where �O����y�A��ܿ�ܧ�諸���
		public int update(
				@Param("id")int id, 
				@Param("title")	String title, 
				@Param("desp")String descrption) ;
		
		// �y�k�����W�٭n�ܦ� entity �� class �W�١G���W�٭n�ܦ��ݩʦW��
		@Modifying  (clearAutomatically = true)    //  clearAutomatically = true �M�����[�ƤW�U����M���Ȧs���
		@Transactional        
		@Query (value = "UPDATE Questionnaire set title = :title, description = :desp, startDate = :startDate "      
											+" where id = :id")  // ���g nativeQuery ���P��  nativeQuery  = false
		public int updateData(
				@Param("id")int id, 
				@Param("title")	String title, 
				@Param("desp")String descrption, 
				@Param("startDate")LocalDate startDate) ;
		
		// DML ��ƾާ@�y�� -> SELECT (�d��) 
		@Query (value = "SELECT * from questionnaire "
											+ " where start_date > :startDate" , nativeQuery = true)
		public List<Questionnaire> findByStartDate(@Param("startDate") LocalDate StartDate) ;
		
		
		@Query (value = "SELECT new Questionnaire(id, title, description, published, startDate, endDate)  " 
											+ " from Questionnaire where startDate > :startDate")
		public List<Questionnaire> findByStartDate1(@Param("startDate") LocalDate StartDate) ;
		
		// nativeQuery = false�Aselect �����n�ϥΫغc��k���覡�A�B entity ���]�n���������غc��k
		@Query (value = "SELECT new Questionnaire(id, title, published) " 
											+ " from Questionnaire where startDate > :startDate")
		public List<Questionnaire> findByStartDate2(@Param("startDate") LocalDate StartDate) ;
		
		// �O�W (Alias)�A�y�k as �O�W
		@Query (value = "SELECT qu from Questionnaire as qu " 
											+ " where startDate > :startDate or published = :isPublished")
		public List<Questionnaire> findByStartDate3(
						@Param("startDate") LocalDate StartDate, 
						@Param("isPublished")boolean published) ;
		
		// order by
		@Query (value = "SELECT qu from Questionnaire as qu " 
											+ " where startDate > :startDate or published = :isPublished ORDER BY id desc")
		public List<Questionnaire> findByStartDate4(
						@Param("startDate") LocalDate StartDate, 
						@Param("isPublished") boolean published) ;
		
		// order by + limit
		// 1. limit �y�k�u��ϥΦb nativeQuery = true 
		// 2. limit �n��b�y�k���̫�
		@Query (value = "SELECT * from questionnaire as qu " 
											+ " where start_date > :startDate or is_published = :isPublished ORDER BY id desc LIMIT :num"
											, nativeQuery = true )
		public List<Questionnaire> findByStartDate5(
						@Param("startDate") LocalDate StartDate, 
						@Param("isPublished") boolean published,
						@Param("num") int limitNum );
		
		// limit �j�M��Ƶ��� (����)
		@Query(value = "SELECT * from questionnaire " 
										+ " limit :startIndex, :limitNum", nativeQuery = true)
		public List<Questionnaire> findWithLimitAndStartIndex(
				@Param("startIndex") int startIndex,
				@Param("limitNum") int limitNum
				) ;
		
		// like �G�ҽk�j�M (���f�t WHERE �l�y�H�� %�Ÿ� )
		@Query(value = "SELECT * from questionnaire " 
										+ " where title LIKE %:title% ", nativeQuery = true)
		public List<Questionnaire> searchTitleLike(@Param("title")	String title) ;

		// regexp ���W��ܦ� (�u��Φb nativeQuery = true )
		@Query(value = "SELECT * from questionnaire " 
				+ " where title REGEXP :title ", nativeQuery = true)
		public List<Questionnaire> searchTitleLike2(@Param("title")	String title) ;

		// regexp or
		@Query(value = "SELECT* from questionnaire " 
				+ " where description REGEXP :keyword1 | :keyword2 ", nativeQuery = true)
		public List<Questionnaire> searchDescriptionContaining(
				@Param("keyword1")	String keyword1,
				@Param("keyword2")	String keyword2) ;

//		// �� concat() ���A���G�P�W�� regexp or �ۦP
//		@Query(value = "select * from questionnaire " 
//				+ " where description REGEXP concat( :keyword1, '|', :keyword2) ", nativeQuery = true)
//		public List<Questionnaire> searchDescriptionContaining2(
//				@Param("keyword1")	String keyword1,
//				@Param("keyword2")	String keyword2) ;

		// JOIN (����O join on)
		@Query("SELECT new com.example.questionnaire.vo.QnQuVo("
				+ " qn.id, qn.title, qn.description, qn.published, qn.startDate, qn.endDate,"
				+ "  q.quId, q.qTitle, q.optionType, q.necessary, q.option) " 
				+ "  from Questionnaire as qn JOIN Question as q ON qn.id = q.qnId")
		public List<QnQuVo> selectJoinQnQu();

		@Query("SELECT new  com.example.questionnaire.vo.QnQuVo("
				+ "qn.id, qn.title, qn.description, qn.published, qn.startDate, qn.endDate,"
				+ "q.quId, q.qTitle, q.optionType, q.necessary, q.option)"
				+ "  from Questionnaire as qn JOIN Question as q ON qn.id = q.qnId"
				+ " where qn.title like %:title% and qn.startDate >= :startDate and qn.endDate <= :endDate")
		public List<QnQuVo> selectFuzzy(
				@Param("title")String title, 
				@Param("startDate")LocalDate startDate, 
				@Param("endDate")LocalDate endDate) ;
}
