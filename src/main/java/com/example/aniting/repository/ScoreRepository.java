package com.example.aniting.repository;

import com.example.aniting.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

	void deleteByUsersId(String usersId);
	
}
