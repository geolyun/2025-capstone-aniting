package com.example.aniting.repository;

import com.example.aniting.entity.RecommendHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendHistoryRepository extends JpaRepository<RecommendHistory, Long> {
	
	void deleteByUsersId(String usersId);
	
}
