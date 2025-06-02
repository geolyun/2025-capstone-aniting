package com.example.aniting.repository;

import com.example.aniting.entity.RecommendResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendResponseRepository extends JpaRepository<RecommendResponse, Long> {

	void deleteByUsersId(String usersId);

	List<RecommendResponse> findAllByUsersIdLike(String prefix);

	// 사용자 ID로 전체 응답 가져오기
	List<RecommendResponse> findByUsersId(String usersId);

}
