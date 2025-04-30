package com.example.aniting.admin.recoResponse;

import java.time.LocalDateTime;
import java.util.List;

import com.example.aniting.dto.RecommendResponseDTO;

public interface AdminRecoResponseService {

	public List<RecommendResponseDTO> getFilteredResponses(String usersId, String keyword, LocalDateTime from, LocalDateTime to);
	public void deleteById(Long id);

}
