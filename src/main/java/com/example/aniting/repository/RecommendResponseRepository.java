package com.example.aniting.repository;

import com.example.aniting.entity.RecommendResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendResponseRepository extends JpaRepository<RecommendResponse, Long> {

	void deleteByUsersId(String usersId);
	
}
