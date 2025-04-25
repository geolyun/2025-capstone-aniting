package com.example.aniting.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.aniting.entity.RecommendHistory;

@Repository
public interface RecommendHistoryRepository extends JpaRepository<RecommendHistory, Long> {
	
	void deleteByUsersId(String usersId);
	int countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
	int countByCreatedAtGreaterThanEqual(LocalDateTime dateTime);
	
	@Query(value = """
		    SELECT COUNT(*) FROM (
		        SELECT USERS_ID, COUNT(*) as CNT
		        FROM RECOMMEND_HISTORY
		        GROUP BY USERS_ID
		        HAVING CNT >= :threshold
		    ) AS sub
		""", nativeQuery = true)
	int countAnomalyUsers(int threshold);
	
	@Query(value = """
		    SELECT P.BREED AS breed, COUNT(*) AS count
		    FROM RECOMMEND_HISTORY RH
		    JOIN PET P ON RH.TOP1_PET_ID = P.PET_ID
		    GROUP BY P.BREED
		    ORDER BY count DESC
		    LIMIT 3
		    """, nativeQuery = true)
	List<Object[]> findTop3Pets();
	
	@Query("""
		    SELECT FUNCTION('DAYOFWEEK', rh.createdAt) AS dayOfWeek, COUNT(rh) AS count
		    FROM RecommendHistory rh
		    GROUP BY dayOfWeek
		    ORDER BY dayOfWeek
		""")
		List<Object[]> countRecommendationsByDayOfWeek();
	
}
