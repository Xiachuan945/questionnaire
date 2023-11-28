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
		 * 取得最新一筆資料：撈取全部資料後倒序，最後的那筆資料會變成第一筆
		 **/
		public Questionnaire findTopByOrderByIdDesc() ;
		
		public List<Questionnaire> findByIdIn(List<Integer> idList) ;
		
		public List<Questionnaire> findByIdInAndPublishedFalse(List<Integer> idList) ;

//		public List<Questionnaire> QuestionnaireRes(String title, LocalDate startDate, LocalDate endDate) ;

		public List<Questionnaire> findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
				String title, LocalDate startDate, LocalDate endDate);
		// publish 是 true → 已發布
		public List<Questionnaire> findByTitleContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
				String title, LocalDate startDate, LocalDate endDate);
		
		// DML 資料操作語言 -> INSRET INTO (新增) ，PK存在的話無法 insert
		@Modifying               // insert 和 update 都要加上這兩個 (insert 限制 1 )
		@Transactional        // insert 和 update 都要加上這兩個 (insert 限制 1 )
		@Query(value = "INSERT INTO questionnaire (title, description, is_published, start_date, end_date)  "
											+ " values ( :title, :desp, :isPublished, :startDate, :endDate)",      // value後代入Param 的值 ( : + @Param後的字串)
											nativeQuery = true)   //  最後要加上 nativeQuery = true (insert 限制 2 )
		public int insert(
				@Param("title") String title, //
				@Param("desp") String descrption, //
				@Param("isPublished") boolean isPublished,  //
				@Param("startDate") LocalDate startDate, //
				@Param("endDate") LocalDate endDate) ;
		
		@Modifying               
		@Transactional        
		@Query(value = "INSERT INTO questionnaire (title, description, is_published, start_date, end_date) "
											+ " values ( ?1, ?2, ?3, ?4, ?5)",      // 用 ?+位置  與下面的位置做對應
											nativeQuery = true)  
		public int insertData(
				String title, 
				String descrption, 
				boolean isPublished,  
				LocalDate startDate, 
				LocalDate endDate) ;
		
		// DML 資料操作語言 -> UPDATE (更新) 
		@Modifying               // insert 和 update 都要加上這兩個 (update 限制 )
		@Transactional        // insert 和 update 都要加上這兩個 (update 限制 )
		@Query (value = "UPDATE questionnaire set title = :title, description = :desp"      // update + table 與資料表欄位
											+"where id = :id" , nativeQuery = true)  // where 是條件句，表示選擇更改的欄位
		public int update(
				@Param("id")int id, 
				@Param("title")	String title, 
				@Param("desp")String descrption) ;
		
		// 語法中表的名稱要變成 entity 的 class 名稱：欄位名稱要變成屬性名稱
		@Modifying  (clearAutomatically = true)    //  clearAutomatically = true 清除持久化上下文→清除暫存資料
		@Transactional        
		@Query (value = "UPDATE Questionnaire set title = :title, description = :desp, startDate = :startDate "      
											+" where id = :id")  // 不寫 nativeQuery 等同於  nativeQuery  = false
		public int updateData(
				@Param("id")int id, 
				@Param("title")	String title, 
				@Param("desp")String descrption, 
				@Param("startDate")LocalDate startDate) ;
		
		// DML 資料操作語言 -> SELECT (查詢) 
		@Query (value = "SELECT * from questionnaire "
											+ " where start_date > :startDate" , nativeQuery = true)
		public List<Questionnaire> findByStartDate(@Param("startDate") LocalDate StartDate) ;
		
		
		@Query (value = "SELECT new Questionnaire(id, title, description, published, startDate, endDate)  " 
											+ " from Questionnaire where startDate > :startDate")
		public List<Questionnaire> findByStartDate1(@Param("startDate") LocalDate StartDate) ;
		
		// nativeQuery = false，select 的欄位要使用建構方法的方式，且 entity 中也要有對應的建構方法
		@Query (value = "SELECT new Questionnaire(id, title, published) " 
											+ " from Questionnaire where startDate > :startDate")
		public List<Questionnaire> findByStartDate2(@Param("startDate") LocalDate StartDate) ;
		
		// 別名 (Alias)，語法 as 別名
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
		// 1. limit 語法只能使用在 nativeQuery = true 
		// 2. limit 要放在語法的最後
		@Query (value = "SELECT * from questionnaire as qu " 
											+ " where start_date > :startDate or is_published = :isPublished ORDER BY id desc LIMIT :num"
											, nativeQuery = true )
		public List<Questionnaire> findByStartDate5(
						@Param("startDate") LocalDate StartDate, 
						@Param("isPublished") boolean published,
						@Param("num") int limitNum );
		
		// limit 搜尋資料筆數 (分頁)
		@Query(value = "SELECT * from questionnaire " 
										+ " limit :startIndex, :limitNum", nativeQuery = true)
		public List<Questionnaire> findWithLimitAndStartIndex(
				@Param("startIndex") int startIndex,
				@Param("limitNum") int limitNum
				) ;
		
		// like ：模糊搜尋 (須搭配 WHERE 子句以及 %符號 )
		@Query(value = "SELECT * from questionnaire " 
										+ " where title LIKE %:title% ", nativeQuery = true)
		public List<Questionnaire> searchTitleLike(@Param("title")	String title) ;

		// regexp 正規表示式 (只能用在 nativeQuery = true )
		@Query(value = "SELECT * from questionnaire " 
				+ " where title REGEXP :title ", nativeQuery = true)
		public List<Questionnaire> searchTitleLike2(@Param("title")	String title) ;

		// regexp or
		@Query(value = "SELECT* from questionnaire " 
				+ " where description REGEXP :keyword1 | :keyword2 ", nativeQuery = true)
		public List<Questionnaire> searchDescriptionContaining(
				@Param("keyword1")	String keyword1,
				@Param("keyword2")	String keyword2) ;

//		// 用 concat() 接，結果與上面 regexp or 相同
//		@Query(value = "select * from questionnaire " 
//				+ " where description REGEXP concat( :keyword1, '|', :keyword2) ", nativeQuery = true)
//		public List<Questionnaire> searchDescriptionContaining2(
//				@Param("keyword1")	String keyword1,
//				@Param("keyword2")	String keyword2) ;

		// JOIN (條件是 join on)
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
