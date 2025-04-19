package com.example.aniting.repository;

import com.example.aniting.entity.RecommendLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendLogRepository extends JpaRepository<RecommendLog, Long> {

	void deleteByUsersId(String usersId);
	
}
