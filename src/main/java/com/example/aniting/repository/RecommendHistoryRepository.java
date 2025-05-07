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
		        SELECT users_id, COUNT(*) as cnt
		        FROM recommend_history
		        GROUP BY users_id
		        HAVING cnt >= :threshold
		    ) AS sub
		""", nativeQuery = true)
	int countAnomalyUsers(int threshold);
	
	@Query(value = """
		    SELECT p.breed AS breed, COUNT(*) AS count
		    FROM recommend_history rh
		    JOIN pet p ON rh.top1_pet_id = p.pet_id
		    GROUP BY p.breed
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
	
	@Query("""
			SELECT rh FROM RecommendHistory rh
			LEFT JOIN FETCH rh.top1PetId
			LEFT JOIN FETCH rh.top2PetId
			LEFT JOIN FETCH rh.top3PetId
			""")
	List<RecommendHistory> findAllWithPets();

}
