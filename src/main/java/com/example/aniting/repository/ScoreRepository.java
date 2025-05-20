package com.example.aniting.repository;

import com.example.aniting.entity.Score;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

	List<Score> findByUsersId(String usersId);

	void deleteByUsersId(String usersId);
	
	@Query("SELECT s.scoreValue, COUNT(s) FROM Score s GROUP BY s.scoreValue ORDER BY s.scoreValue")
	List<Object[]> countGroupedByScore();

	@Query("SELECT AVG(s.scoreValue * 1.0) FROM Score s WHERE s.categoryId = :categoryId")
	Double avgScoreByCategory(@Param("categoryId") Long categoryId);
	
}
